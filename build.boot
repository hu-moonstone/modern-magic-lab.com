(set-env!
 :source-paths #{"src"}
 :resource-paths #{"resources"}
 :dependencies '[[org.clojure/clojure "1.8.0"]
                 [perun "0.4.2-SNAPSHOT" :scope "test"]
                 [hiccup "1.0.5"]
                 [deraen/boot-sass "0.3.1"]
                 [pandeiro/boot-http "0.8.3"]
                 [clj-time "0.14.4"]])

(require '[clojure.string :as str]
         '[io.perun :as perun]
         '[deraen.boot-sass :refer [sass]]
         '[pandeiro.boot-http :refer [serve]])


;; コレクションページ判定用
;; 「->」マクロは、その次の値を、値以降の関数呼び出しの第一引数として取り扱う
;; 複数の関数呼び出しがある場合は、関数の結果が次の関数の第一引数として使われる
;; 「some->」マクロは、関数呼び出し結果がnilになったら処理を中断しnilを返す
(def not-collection-path?
  (fn [{:keys [path] :as m}]
    (not (some-> path (.endsWith "index.html")))))

;; カテゴリー設定
(def linux-path?
  (fn [{:keys [original-path] :as m}]
       (some-> original-path (.startsWith "linux/"))))

(def emacs-path?
  (fn [{:keys [original-path] :as m}]
       (some-> original-path (.startsWith "emacs/"))))

(def clojure-path?
  (fn [{:keys [original-path]}]
    (.startsWith original-path "clojure/")))

(def perun-path?
  (fn [{:keys [original-path] :as m}]
       (some-> original-path (.startsWith "perun/"))))


(deftask build
  "Build blog."
  []
  (comp
   ;; https://github.com/Deraen/boot-sass
   ;; resources/main.sassをmain.cssに変換。public/main.cssに移動
   (sass)
   ;;(sift :move {#"assets/reset.css" "public/assets/reset.css"})
   ;;(sift :move {#"assets/main.css" "public/assets/main.css"})
   ;;(sift :move {#"assets/images" "public/assets/images"})
   (perun/global-metadata)
   (perun/markdown :md-exts {:tables true})
   ;; (perun/draft)
   (perun/print-meta)
   ;; (perun/permalink)
   (perun/slug)
   ;; (perun/ttr)
   ;; (perun/word-count)
   (perun/build-date)
   (perun/render :renderer 'mmlab.core/render-post)

   ;; カテゴリー毎のコンテンツ一覧ページ生成
   (perun/collection :renderer 'mmlab.core/render-collection
                     ;; ソート
                     :sortby :date-published
                     ;; コレクションのentriesに含めるファイルのパス
                     :filterer clojure-path?
                     ;; コレクションのタイトル設定
                     :meta {:title "clojure"}
                     ;; コレクションの出力先
                     :page "clojure/index.html")

   (perun/collection :renderer 'mmlab.core/render-collection
                     ;; ソート
                     :sortby :date-published
                     ;; コレクションのentriesに含めるファイルのパス
                     :filterer perun-path?
                     ;; コレクションのタイトル設定
                     :meta {:title "perun"}
                     ;; コレクションの出力先
                     :page "perun/index.html")
   (perun/collection :renderer 'mmlab.core/render-collection
                     ;; ソート
                     :sortby :date-published
                     ;; コレクションのentriesに含めるファイルのパス
                     :filterer linux-path?
                     ;; コレクションのタイトル設定
                     :meta {:title "linux"}
                     ;; コレクションの出力{:class "tags"}先
                     :page "linux/index.html")

   ;; Collectionを除く記事一覧をHomeに表示
   (perun/collection :renderer 'mmlab.core/render-collection
                     :filterer not-collection-path?
                     :sortby :date-published
                     :meta {:title "Home"}
                     :page "index.html")

   ;; (perun/static :renderer 'mmlab.about/render :page "about.html")
   (perun/tags :renderer 'mmlab.core/render-tag :out-dir "public/tag/")
   ;; (perun/paginate :renderer 'mmlab.paginate/render)
   ;; (perun/assortment :renderer 'mmlab.core/render-assortment
   ;;                   :grouper (fn [entries]
   ;;                              (->> entries
   ;;                                   (mapcat (fn [entry]
   ;;                                             (if-let [kws (:keywords entry)]
   ;;                                               (map #(-> [% entry]) (str/split kws #"\s*,\s*"))
   ;;                                               [])))
   ;;                                   (reduce (fn [result [kw entry]]
   ;;                                             (let [path (str kw ".html")]
   ;;                                               (-> result
   ;;                                                   (update-in [path :entries] conj entry)
   ;;                                                   (assoc-in [path :entry :keyword] kw))))
   ;;                                           {}))))
   (perun/sitemap :filename "sitemap.xml")
   (perun/rss :description "")

   ;; (perun/atom-feed :filterer :filename "atom.xml")
   ;; (perun/print-meta)
   (target)
   (notify)))

(deftask dev
  []
  (comp (watch)
        (build)
        (serve :resource-root "public"))) ;; targetのDocumentRoot指定

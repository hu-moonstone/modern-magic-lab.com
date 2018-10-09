(ns mmlab.core
  (:require [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            )
  (:use [hiccup.core :only (html)]
        [hiccup.page :only (html5 include-css include-js)]))

(defn create-description [post]
  (try
    (subs (:content post) 0 10)
    (catch Exception e (str "caught exception:" (.getMessage e)))))

(def create-description-defaults
  (fnil create-description {:content "---------------------"}))

(defn create-head [global-meta posts post]
  [:head
   ;; メタ情報
   [:meta {:charset "utf-8"}]
   [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge,chrome=1"}]
   [:meta {:http-equiv "x-dns-prefetch-control" :content "on"}]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0, user-scalable=no"}]
   [:meta {:name "author" :itemprop "author" :content (:author post)}]
   [:meta {:name "keywords" :itemprop "keywords" :content (:keywords post)}]
   [:meta {:name "description" :itemprop "description" :content (:description post) }]
   [:title (str (:title post) "|" (:site-title global-meta))]

   ;;pwa manifest
   [:link {:rel "manifest" :href "/manifest.json"}]
   [:link {:rel "shortcut icon" :href ""}]
   ;;
   [:link {:rel "publisher" :href ""}]
   ;; 著作者ページ
   [:link {:rel "author" :href ""}]
   [:link {:rel "alternate" :type "application/rss+xml" :title "RSS" :href "/feed.rss"}]
   ;; スタイルシート
   [:link {:type "text/css" :href "https://use.fontawesome.com/releases/v5.1.0/css/all.css" :integrity "sha384-lKuwvrZot6UHsBSfcMvOkWwlCMgc0TaWr+30HWe3a4ltaBwTZhyTEggF5tJv8tbt" :crossorigin "anonymous" :rel "stylesheet"}]
   [:link {:type "text/css" :href "/assets/reset.css" :rel "stylesheet"}]
   [:link {:type "text/css" :href "/assets/main.css" :rel "stylesheet"}]
   [:link {:type "text/css" :href "/assets/highlight/zenburn.css" :rel "stylesheet"}]
   ;; Prefetch
   (for [post posts]
     [:link {:rel "prefetch" :href (:permalink post)}])])

(defn create-header [global-meta]
  [:header
   [:div.inner
    [:label {:id "drawer-open" :for "drawer-input"}
      [:i {:class "fas fa-bars"}]]
    [:h1
     [:a {:href "/"} (:site-title global-meta)]]
    [:ul
     [:li [:a {:href "/feed.rss" :class "feed-button"}
           [:i {:class "fas fa-rss-square"}]]]]]])


(defn create-footer []
  [:footer
   [:div.inner
    [:span.copyright "Copyright&copy;2018 www.kaleidoscope.link"]]
   [:script {:src "/assets/js/highlight.pack.js"}]
   [:script "hljs.initHighlightingOnLoad()"]])


(defn create-tag-list [posts]
  [:ul
   (for [post posts]
     (for [tag (:tags post)]
       [:li
        [:a {:href (str "/tag/" tag ".html")} tag ]]))])

;; 文字列の日付を別の形式の日付(文字列)に変換
(defn reformat-date-str [date-str initial-fmt final-fmt]
  (let [date (f/parse (f/formatter initial-fmt) date-str)]
    (f/unparse (f/formatter final-fmt) date)))

;; 文字列の日付をDate形式に変換
(defn str-to-date [str]
  (c/to-date (f/parse str)))

;; Date形式をfmtで指定した文字列の日付形式に変換
(defn reformat-date [date fmt]
  (f/unparse (f/formatter fmt) (c/from-date date)))

(defn update-date [date-published]
  [:span
   [:i {:class "far fa-clock"}]
   (reformat-date date-published "YYYY-MM-dd")])

  ;; (def data {:1 {:k1 5 :k2 10} :2 {:k1 10 :k2 5}})のようなデータをソートする際の処理
  ;; (sort-by (comp :k1 second) > data)

  ;; (let [posts2 (convert-long posts)]
  ;;   (for [post posts2]
  ;;     (println (:date-published  post)))))

  ;; (for [post posts]
  ;;   (println (str post))
  ;;   (println (str (map str-to-date (:date-published post))))))

(defn print-debug [post]
  [])

(defn create-aside [posts]
  [:aside {:id "drawer"}
   [:input {:id "drawer-input" :type "checkbox" :class "drawer-hide"}]
   [:label {:id "drawer-close" :for "drawer-input" :class "drawer-hide"}]
   [:div {:id "drawer-content"}
    [:section
     [:h1 "Menu"]
     [:ul {:class "side-list"}
      (for [element '("clojure" "debian" "emacs" "perun")]
        [:li
         [:a {:href (str "/" element "/")} element ]])
        [:li
         [:a {:href "/about.html"} "about"]]]]
    [:section
     [:h1 "Recent"]
     [:ul {:class "side-list"}
      (for [element (take 5 posts)]
        [:li
         [:a {:href (:permalink element)} (:title element) ]])]]

    [:section
     [:h1 "Tags"]
     [:ul {:class "side-list-tags"}
      (let [d (map :tags posts)]
        (for [element (distinct (flatten d))]
          [:li
           [:a {:href (str "/tag/" element ".html")} element ]]))]]
    [:section
     [:h1 "Archives"]]]])


(defn post-category
  ;; カテゴリー名をPermalinkから取得
  [permalink]
  (println permalink)
  (second (-> (.split permalink "/") seq)))



(defn render-post [{global-meta :meta posts :entries post :entry}]
  (html5 {:lang (:lang global-meta) :itemtype "http://schema.org/Blog"}
         (create-head global-meta posts post)
         [:body
          (create-header global-meta)
          [:div.inner
           [:div
            [:ul {:class "breadcrumb"}
             [:li
              [:a {:href "/"} "Home"]]
             [:li
              [:a {:href (str "/" (post-category (:permalink post)) "/") } (str (post-category (:permalink post)))]]
             [:li
              [:span (:title post)]]]]

           ;; [:div "post"]
           ;; [:pre (str posts)]
           ;; [:pre (str post)]
           ;; [:div (str (:slug post))]
           ;; [:div (str "parent-path = " (:parent-path post))]
           ;; [:div (str "path = " (:path post))]
           ;; [:div (str "original-path = " (:original-path post))]
           ;; [:div (str "permalink = " (:permalink post))]
           [:div {:class "contents article"}
            [:main
             [:section
              [:div {:class "timestamp"}
               [:time (update-date (:date-published post))]]
              ;;[:pre (str post)]
              [:h1 (:title post)]
              [:section (:content post)]]]
            (create-aside posts)]
           ]
          (create-footer)]))

(defn render-tag [{global-meta :meta posts :entries entry :entry}]
  (html5 {:lang (:lang global-meta) :itemtype "http://schema.org/Blog"}
         (create-head global-meta posts {:content (:description global-meta)})
         [:body
          (create-header global-meta)
          [:div.inner
           [:div
            [:ul {:class "breadcrumb"}
             [:li
              [:a {:href "/"} "Home"]]
             [:li
              [:span "Tag"]]
             [:li
              [:span {:href (str "/tag" (:permalink entry))} (:tag entry)]]]]
              ;;[:a {:href (str "/tag" (:permalink entry))} (:tag entry)]]]]


           [:div "list"]
           [:div (:parent-path entry)]
           [:pre (str entry "")]
           [:div {:class "contents"}
            [:main
             [:section
              [:h1 "Contents"]
              [:div
               [:ul {:class "article-list"}
                (for [post posts]
                  [:li
                   [:div {:class "timestamp"}
                    [:time (update-date (:date-published post))]]
                   [:div {:class "title"}
                    [:a {:href (:permalink post)} (str (:title post))]]
                   [:div {:class "description"}
                    [:div (:description post)]]
                   [:div
                    [:ul {:class "tag-list"}
                     (for [tag (:tags post)]
                       [:li
                        [:a {:href (str "/tag/" tag ".html")} tag]])
                     ]]])
                ]]]]
            (create-aside posts)]]
          (create-footer)]))

(defn render-collection [{global-meta :meta posts :entries post :entry}]
  (html5 {:lang (:lang global-meta) :itemtype "http://schema.org/Blog"}
         (create-head global-meta posts post)
         [:body
          (create-header global-meta)
          ;;[:img {:class "main-visual ":src "/images/main-visual.jpg" :alt "Main Visual"}]
          [:div.inner
           ;; Home以外でパンくずを表示
           (if (and (> (count (:permalink post)) 0) (not (= (:title post) "Home")))
             [:div
              [:ul {:class "breadcrumb"}
               [:li
                [:a {:href "/"} "Home"]]
               [:li
                [:span (:title post)]]]])
                ;;[:a {:href (:permalink post)} (:title post)]]]])

           ;; [:div "index"]
           ;; [:pre (str posts)]
           ;; [:div (str "parent-path = " (:parent-path post))]
           ;; [:div (str "path = " (:path post))]
           ;; [:div (str "original-path = " (:original-path post))]
           ;; [:div (str "permalink = " (:permalink post))]
           [:div {:class "contents"}
            [:main
             [:section
              [:div
               [:ul {:class "article-list"}
                (for [post posts]
                  [:li
                   [:div {:class "thumbnail"}
                    [:a {:href (:permalink post)}
                     [:img {:src (str "/assets/images/" (:image post))}]]]
                   [:div {:class "article-info"}
                    [:div {:class "title"}
                     [:a {:href (:permalink post)} (str (:title post))]]
                    [:div {:class "description"}
                     [:div (:description post)]]
                    [:div {:class "timestamp"}
                     [:time (update-date (:date-published post))]]

                    [:div
                     [:ul {:class "tag-list"}
                      (for [tag (:tags post)]
                        [:li
                         [:a {:href (str "/tag/" tag ".html")} tag]])
                      ]]]])
                ]]]]
             (create-aside posts)]]
          (create-footer)]))

---
author: Hironori Ueno
date-published: 2018-06-20
title: Docker&ClojureでのWebアプリケーション構築
description: Docker環境でClojureを使ったWebアプリケーション構築
keywords: docker, clojure, webアプリケーション
image: logo/clojure-logo.png
tags:
 - clojure
 - docker
 - web
---

# Docker, Clojure, Nginx

## EmacsでClojureする時の覚書

clojure-mode, ciderを入れておく。

```
M-x cider-jack-in # REPL起動
M-x cider-load-file # REPLにClojureファイルをロード
M-x cider-eval-last-sexp #カーソル前の式を評価
```


## Repl実行

```
user=>(require '[projectname.core]) ; パッケージをrequire
user=>(in-ns 'projectname.core) ; ネームスペースの内側で作業
user=>(start-server) ; サーバー起動
```

## Selmer(HTMLテンプレートエンジン)のパス設定

ProjectDir/resources/templates以下にhtmlテンプレートを配置する。
project.cljに、resource-pathの設定を追加する。

```
:resource-path "resources"
```

```
(ns kaleidoscope.core
  (:require [ring.adapter.jetty :as server]
            [selmer.parser :as parser]))

(parser/set-resource-path! (clojure.java.io/resource "template"))
(parser/render-file "index.html {:content "Hello,World"}))
```

## lein runで起動させるには

```
(defn -main []
  (start-server))
```

を記述して、以下のコマンドで起動。

```$ lein run -m kaleidoscope.core```

-mオプションを省きたい場合は、project.cljに:mainを記載する。

```
:main kaleidoscope.core/start-server
```

## Dockerで動かす

nginxとapp-serverのふたつのコンテナを構築してみる。

```
version: '2'
services:
  app-server:
    build: "./app-server"
    container_name: "app"
    working_dir: "/usr/src/app"
    ports:
      - "3000:3000"
    volumes:
      - ".:/usr/src/app"
      - ".m2:/home/docker/.m2"
  http-server:
    image: nginx:latest
    container_name: "http"
    volumes:
      - "./nginx/conf.d/default.conf:/etc/nginx/conf.d/default.conf"
      - "./public:/usr/share/nginx/html"
    links:
      - app-server
    ports:
      - "80:80"
```

app-server/Dockerfileは以下の内容。プロジェクトディレクトリを丸ごとコピーし、lein runを実行する。
```
FROM clojure
COPY . /usr/src/app
WORKDIR /usr/src/app
CMD ["lein", "run"]
```

docker-composeのportを3000番にしておく。


http-server(nginx)のほうは、volumesでnginxの設定ファイルと、public以下を結びつける。default.confは以下のように記述。

```
server {
    listen       80;
    server_name  localhost;

    location / {
        proxy_pass http://app-server:3000;
    }

    location ~* \.(css|js|jpg|png|gif|svg|html|htm)$ {
        root /usr/share/nginx/html;
        index index.html index.htm;
    }

    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        root   /usr/share/nginx/html;
    }
}
```

通常アクセスはすべてapp-serverの3000番ポートに。
HTMLで使いそうな静的ファイルは/sur/share/nginx/html以下のものを使う。
ここで使っているapp-serverという名前は、docker-compose.ymlのlinksの箇所で設定した名前を使う。エイリアスをつけておくこともできる。

```
links
  - app-server: myapp
```

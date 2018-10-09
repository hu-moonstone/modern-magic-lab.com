---
author: H.U
date-published: 2018-09-07
title: FirefoxのインストールとXFce4メニューへの追加
description:
image: logo/debian-logo.png
tags:
 - debian
 - firefox
 - xfce
---

## Firefox Developer Edition
https://www.mozilla.org/ja/firefox/developer/ からtar.bz2のファイルをダウンロードする。

```
$ tar jxvf firefox〜.tar.bz
$ sudo mv firefox /opt/
$ /opt/firefox/firefox
```

## Firefox Developer EditionをXFce4のメニューに追加する
```
$ cd ~/.local/share/applications/
$ vi firefox-developer-edition.desktop
```

```
[Desktop Entry]
Version=1.0
Name=Firefox Developer Edition
GenericName=Web Browser
X-XFCE-Category=WebBrowser
X-XFCE-Commands=/opt/firefox/firefox
Type=Application
Comment=
Exec=/opt/firefox/firefox
Icon=/opt/firefox/browser/chrome/icons/default/default128.png
Path=
Terminal=false
StartupNotify=false
Categories=Network;WebBrowser;

```

```
$ xfdesktop --reload
```
でメニューに反映します。
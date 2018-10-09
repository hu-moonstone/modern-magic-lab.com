---
author: H.U
date-published: 2018-09-07
title: Ctrl,Capsの入れ替え
description:
image: logo/debian-logo.png
tags:
 - debian
 - keybindings
---


## Ctrl,Capsの入れ替え

```
$ vi ~/.Xmodmap
```
```
remove Lock = Caps_Lock
remove Control = Control_L
keysym Caps_Lock = Control_L
keysym Control_L = Caps_Lock
add Lock = Caps_Lock
add Control = Control_L
```
```
$ xmodmap ~/.Xmodmap
```


Xの起動時に自動で設定するためには、/etc/X11/xinit/xinitrcをホームディレクトリ直下に.xinitrcという名前でコピーし、以下の内容を記述しておきます

```
$ xmodmap ~/.Xmodmap
```
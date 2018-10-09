---
author: H.U
date-published: 2018-09-07
title: Small Tips
description:
image: logo/debian-logo.png
tags:
 - debian
---

### ランレベル変更

デフォルトのランレベルの確認
```
systemctl get-default
```

デフォルトのランレベルを変更
```
systemctl set-default -f multi-user.target
```


## ディレクトリ名を英語に

```
$ sudo aptitude install xdg-user-dirs-gtk
$ LANG=C xdg-user-dirs-gtk-update
```

「デスクトップ」が残ってしまうのは再起動後に削除。

### ビープ音を消す

```
$ xset b off
```

## フォントサイズ
設定→外観→フォントのタブで、DPI設定を変更する(デフォルトは96)。

## 画面スケール
```
$ xrandr | grep -w connected
eDP connected primary 1920x1080+0+0 (normal left inverted right x axis y axis) 276mm x 155mm
$ xrandr --output eDP --scale 0.8x0.8
```


## タッチパッド無効化(Xfce4)

設定→設定マネージャー→マウスとタッチパッドを開き、デバイスにSynPS/2 Synaptics TouchPadを選択し、「このデバイスを有効にする」のチェックを外す。

## sources.listからcdromを削除
```
/etc/apt/sources.list
```
を開き、以下のように記述してあるcdromの項目をコメントアウトします

```
# deb cdrom:[Debian GNU/Linux 9.5.0 _Stretch_ - Official amd64 DVD Binary-1 20180714-10:25]/ stretch contrib main
```

## S.M.A.R.T情報確認

```
$ sudo apt install smartmontools
$ sudo smartctl -a /dev/sda
```

## SSD向けの設定
http://d.hatena.ne.jp/make_tomo/20110731/1312126119

```
$ sudo vi /etc/fstab
UUID=xxxxxxx / ext4 discard,noatime,errors=remount-ro 0 1
```
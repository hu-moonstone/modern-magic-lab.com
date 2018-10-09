---
author: H.U
date-published: 2018-09-07
title: LiveCDからの復旧
description:
image: logo/debian-logo.png
tags:
 - debian
---

## LiveCDからの復旧

起動後ターミナルを開く。

```
$ sudo fdisk -l
```
でハードディスク構成を確認し、システムをインストールしているパーティション(/dev/sda2など）を把握する。

```
$ sudo mount /dev/sda2 /mnt
$ sudo mount --bind /dev/ /mnt/dev
$ sudo chroot /mnt
```

これでインストールしているシステム内部にroot状態で入れるため、誤って記述した設定などを元に戻す。
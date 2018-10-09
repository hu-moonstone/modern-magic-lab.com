---
author: H.U
date-published: 2018-09-07
title: sudoの設定
description:
image: logo/debian-logo.png
tags:
 - debian
---

## sudoの設定

```
# apt install sudo
# visudo
```

以下のように、rootの行の下に自分のユーザー名を追記して設定を記述します。
```
# User privilege specification
root    ALL=(ALL:ALL) ALL
username    ALL=(ALL:ALL) ALL
```
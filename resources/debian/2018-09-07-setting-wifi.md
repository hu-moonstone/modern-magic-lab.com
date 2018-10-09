---
author: H.U
date-published: 2018-09-07
title: WiFi設定
description:
image: logo/debian-logo.png
tags:
 - debian
 - wifi
---



## wifi

### 1. GIT, gcc, makeのインストール
```
$ sudo apt install git gcc make
```

### 2. Makeのためにlinux headerをインストール

```
$ sudo apt install linux-headers-4.9.0-7-all-amd64
```

## モジュールのインストール

https://github.com/lwfinger/rtl8188eu をcloneし、構築後インストールします。
```
$ git clone https://github.com/lwfinger/rtl8188eu
$ cd rtl8188eu
$ make all
$ sudo make install
install -p -m 644 8188eu.ko  /lib/modules/4.9.0-4-amd64/kernel/drivers/net/wireless
cp rtl8188eufw.bin /lib/firmware/.
/sbin/depmod -a 4.9.0-4-amd64
mkdir -p /lib/firmware/rtlwifi
cp rtl8188eufw.bin /lib/firmware/rtlwifi/.
```

インストールが完了したら、/etc/modulesに"8188eu"という記述を書き込み、WDC-150SU2Mを有効化します。

```
$ sudo echo "8188eu" >> /etc/modules
$ sudo iwconfig
$ sudo iwlist wlxbc5c4ca54f23 rate
$ sudo ifconfig #デバイスが一覧にあることを確認
$ sudo ifconfig wlxbc5c4ca54f23 up

```
## カーネル更新時
カーネルの更新後は、新しいカーネルヘッダーでモジュールを改めて構築する必要があります。

## 無線LANが有効にならない場合
以下のコマンドを実行します。insmodは指定したファイルをモジュールとして現在のカーネルにロードし、depmodは依存関係情報を更新します。
```
$ sudo insmod /lib/modules/4.9.0-6-amd64/kernel/drivers/net/wireless/8188eu.ko
$ sudo depmod 4.9.0-6-amd64
$ sudo iwconfig
```

```
$ sudo iwconfig
```
で、以下のようにデバイスの情報が表示されることを確認します。

```
wlxbc5c4ca54f23  unassociated  ESSID:""  Nickname:"<WIFI@REALTEK>"
          Mode:Managed  Frequency=2.412 GHz  Access Point: Not-Associated
          Sensitivity:0/0
          Retry:off   RTS thr:off   Fragment thr:off
          Encryption key:off
          Power Management:off
          Link Quality=0/100  Signal level=0 dBm  Noise level=0 dBm
          Rx invalid nwid:0  Rx invalid crypt:0  Rx invalid frag:0
          Tx excessive retries:0  Invalid misc:0   Missed beacon:0

lo        no wireless extensions.

enp1s0f0  no wireless extensions.
```



## rtl8822BE(Kernel-4.18.5)

```
$ git clone https://github.com/lwfinger/rtlwifi_new.git
$ cd rtlwifi_new
$ git checkout origin/extended -b extended
$ sudo make install
$ sudo modprobe -r rtl8822be
$ sudo /sbin/depmod -a 4.18.5
$ sudo update-initramfs -u
$ sudo reboot
```

iwconfigでデバイス一覧にデバイスが表示されていれば成功。


---
author: H.U
date-published: 2018-09-07
title: バッテリー制御
description:
image: logo/debian-logo.png
tags:
 - debian
 - battery
---


https://askubuntu.com/questions/630429/unable-to-install-tp-smapi-0-41-on-thinkpad-t500
https://linrunner.de/en/tlp/docs/tlp-linux-advanced-power-management.html#installation
http://zaka-think.com/linux/ubuntu/ubuntuで省電力設定「tlp」編/

apt install tlp tlp-rdw
apt install tp-smapi-dkms acpi-call-dkms

tlp-rdw : wifi, bluetoothの省電力設定
smartmontools : S.M.A.R.T確認

tp_smapiは古いThinkPadのみ対応しているためインストールしない。
acpi_callは新しいThinkPadに対応済み
apt purge tp-smapi-dkms

tlp-stat -b

## バッテリー閾値
以下のコマンドで設定を行う。
```
$ sudo tlp setcharge 60 95 BAT0
$ sudo tlp setcharge 60 95 BAT1
$ sudo tlp start
```

tlp-statで設定を改めて確認する。ThinkPad A275ではバッテリーが２つ搭載されているため、BAT0、BAT1の設定値を見る。
```
$ sudo tlp-stat -b
--- TLP 0.9 --------------------------------------------

+++ ThinkPad Extended Battery Functions
tp-smapi   = inactive (kernel module 'tp_smapi' not installed)
tpacpi-bat = active

+++ ThinkPad Battery Status: BAT0 (Main / Internal)
/sys/class/power_supply/BAT0/manufacturer                   = LGC
/sys/class/power_supply/BAT0/model_name                     = 45N1113
/sys/class/power_supply/BAT0/cycle_count                    = (not supported)
/sys/class/power_supply/BAT0/energy_full_design             =  23480 [mWh]
/sys/class/power_supply/BAT0/energy_full                    =  23330 [mWh]
/sys/class/power_supply/BAT0/energy_now                     =  20810 [mWh]
/sys/class/power_supply/BAT0/power_now                      =      0 [mW]
/sys/class/power_supply/BAT0/status                         = Unknown (threshold effective)

tpacpi-bat.BAT0.startThreshold                              =     60 [%]
tpacpi-bat.BAT0.stopThreshold                               =     60 [%]
tpacpi-bat.BAT0.forceDischarge                              =      0

Charge                                                      =   89.2 [%]
Capacity                                                    =   99.4 [%]

+++ ThinkPad Battery Status: BAT1 (Ultrabay / Slice / Replaceable)
/sys/class/power_supply/BAT1/manufacturer                   = SANYO
/sys/class/power_supply/BAT1/model_name                     = 45N1767
/sys/class/power_supply/BAT1/cycle_count                    = (not supported)
/sys/class/power_supply/BAT1/energy_full_design             =  47520 [mWh]
/sys/class/power_supply/BAT1/energy_full                    =  48790 [mWh]
/sys/class/power_supply/BAT1/energy_now                     =  33560 [mWh]
/sys/class/power_supply/BAT1/power_now                      =  34358 [mW]
/sys/class/power_supply/BAT1/status                         = Charging

tpacpi-bat.BAT1.startThreshold                              =     60 [%]
tpacpi-bat.BAT1.stopThreshold                               =     60 [%]
tpacpi-bat.BAT1.forceDischarge                              =      0

Charge                                                      =   68.8 [%]
Capacity                                                    =  102.7 [%]

```

以下のように、開始と停止の値が同一の値になってしまう問題が発生。
```
tpacpi-bat.BAT0.startThreshold                              =     60 [%]
tpacpi-bat.BAT0.stopThreshold                               =     60 [%]
```

TLPのGitHubページを確認したところ、ThinkPadのEdgeシリーズ、Lシリーズなどではこの問題が発生するものの、動作に支障はないという説明。
ThinkPad A275でもこの問題が発生した。

https://github.com/linrunner/TLP/issues/81
https://linrunner.de/en/tlp/docs/tlp-faq.html#wrong-threshold-values

```
ThinkPad Edge (E/S) series, L series, SL410/510, Yoga series
On these models the threshold values shown by tlp-stat -b do not correspond to the written values. For example the setting START_CHARGE_THRESH_BATx=75 / STOP_CHARGE_THRESH_BATx=80 shows 75 / 74. The described behavior is caused by the firmware (UEFI/BIOS), not by TLP. Nonetheless the charge thresholds work as configured.
```
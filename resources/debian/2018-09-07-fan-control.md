---
author: H.U
date-published: 2018-09-07
title: ファン制御
description:
image: logo/debian-logo.png
tags:
 - debian
 - fan-control
---


## センサー情報の取得

```
$ sudo apt install lm-sensors
$ sensors
```
ThinkPad A275(Kernel 4.9.0-7)では、ファンの回転数情報以外取得できませんでした。

念の為温度情報をhwmonの値から確認。

```
# find /sys/devices -type f -name "temp*_input"
/sys/devices/pci0000:00/0000:00:18.3/hwmon/hwmon1/temp1_input
/sys/devices/pci0000:00/0000:00:01.0/hwmon/hwmon3/temp1_input
# cat /sys/devices/pci0000:00/0000:00:18.3/hwmon/hwmon1/temp1_input
0
# cat /sys/devices/pci0000:00/0000:00:01.0/hwmon/hwmon3/temp1_input
0
```

両方とも0のため、温度が取得できていないことが確認できます。

## ファン制御
http://www.thinkwiki.org/wiki/How_to_control_fan_speed
http://d.hatena.ne.jp/khiker/20130329/thinkfan
```
# apt install thinkfan
# vi /etc/modprobe.d/thinkfan.conf
```

confファイルに以下の内容を記述します。
```
options thinkpad_acpi fan_control=1
```

```
# echo level 6 > /proc/acpi/ibm/fan
```
でファンの速度を制御します。

番号は、0〜7までで指定します。(※括弧内はsensorsで出たrpm値です)

|番号|速度|
|---|---|
|0| 停止|
|2|低速(4900rpm)|
|4|中速(5800rpm)|
|7|高速|
|auto|自動(温度センサーが動かないため固定になる)|
|disengaged|最高速|

Kernel 4.9.0-7では温度情報を取得できず、ファンの回転速度は設定した速度で固定になります。
試しにthinkfanサービスを起動させようとすると、以下のようなエラーがjournalctlで確認できます。

```
# /etc/init.d/thinkfan start
# journalctl -xe
```

```
-- Unit thinkfan.service has begun starting up.
 8月 21 01:36:48 debian thinkfan[8835]: thinkfan 0.9.1 starting...
 8月 21 01:36:48 debian thinkfan[8835]: WARNING: Using default fan control in /pr
 8月 21 01:36:48 debian systemd[1]: thinkfan.service: Control process exited, cod
 8月 21 01:36:48 debian thinkfan[8835]: WARNING: Using default temperature inputs
 8月 21 01:36:48 debian systemd[1]: Failed to start simple and lightweight fan co
-- Subject: Unit thinkfan.service has failed
-- Defined-By: systemd
-- Support: https://www.debian.org/support
--
-- Unit thinkfan.service has failed.
--
-- The result is failed.
 8月 21 01:36:48 debian thinkfan[8835]: /proc/acpi/ibm/thermal: No such file or d
 8月 21 01:36:48 debian systemd[1]: thinkfan.service: Unit entered failed state.
 8月 21 01:36:48 debian thinkfan[8835]: add_sensor: Error getting temperature.
 8月 21 01:36:48 debian systemd[1]: thinkfan.service: Failed with result 'exit-co
 8月 21 01:36:48 debian thinkfan[8835]: /proc/acpi/ibm/thermal: No such file or d
 8月 21 01:36:48 debian thinkfan[8835]: Error parsing temperatures:
 8月 21 01:36:48 debian thinkfan[8835]: readconfig: Error getting temperature.
 8月 21 01:36:48 debian thinkfan[8835]: Refusing to run without usable config fil
```

## Kernel4.18.5で温度によってファンの回転数を制御する


Kernelを4.18.5にした際、以下のように温度情報を取得できるようになりました。
```
$ cat /sys/devices/pci0000:00/0000:00:01.0/hwmon/hwmon3/temp1_input
35000
```

/etc/thinkfan.confを編集し、以下の内容を書き込みます。

```
hwmon /sys/devices/pci0000:00/0000:00:01.0/hwmon/hwmon3/temp1_input

(0,	0,	30)
(1,	30,	35)
(2,	35,	38)
(3,	38,	45)
(4,	45,	48)
(5,	48,	55)
(7,	55,	32767)
```


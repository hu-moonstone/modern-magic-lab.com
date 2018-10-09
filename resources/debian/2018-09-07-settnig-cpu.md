---
author: H.U
date-published: 2018-09-07
title: CPUのクロック制御
description:
image: logo/debian-logo.png
tags:
 - debian
 - cpu
---

## CPUのクロック制御
http://aimingoff.way-nifty.com/blog/2017/07/linux-cpu-amd-a.html

```
$ sudo apt install cpufrequtils
```

利用可能なCPUの動作モードを取得
```
$ cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors
conservative userspace powersave ondemand performance schedutil
```

利用可能なCPUの動作周波数を取得
```
$ cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies
2700000 2300000 1800000 1400000
```

```
$ sudo cp /usr/share/doc/cpufrequtils/examples/cpufrequtils.sample /etc/default/cpufrequtils
$ sudo vi /etc/default/cpufrequtils
```

```
ENABLE="true"
GOVERNOR="ondemand"
MAX_SPEED=2700000
MIN_SPEED=1400000
```

```
$ sudo service cpufrequtils restart
```
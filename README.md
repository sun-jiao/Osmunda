# Osmunda
**The README in both Chinese and English is complete.**

**中文和英文版本的README均是完整的。**

# Introduction | 简介

Osmunda is an Android library to uncompress Open Street Map data, write into SQLite, for offline geocoding. 

Osmunda是一个Android库，读取开放街道地图数据，写入SQLite中，可用于离线地理编码等。

# How to use | 如何使用

## Gradle Settings | Gradle设置

### Step 1. | 第一步

Add the JitPack repository to your project build.gradle file:

将JitPack仓库添加进您的项目的build.gradle文件中：

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

### Step 2. | 第二步

Add the dependency

添加依赖

	dependencies {
	        implementation 'moe.sunjiao:osmunda:1.0.0'
	}

## Data import | 导入代码

  

# OSM data source | 开放街道地图数据源

# License | 许可证

    Copyright (C) 2020 [SUN JIAO](https://www.sunjiao.moe)
    Apache License Version 2.0, January 2004
    http://www.apache.org/licenses/


# References & Credits | 参考及致谢

Thanks to [spyhunter99/osmreader](https://github.com/spyhunter99/osmreader), I referred to it, rewrote its core algorithms in kotlin, and fixed some bugs.

感谢[spyhunter99/osmreader](https://github.com/spyhunter99/osmreader)，我参考了该项目，用kotlin重写了它的核心算法，并解决了一些bug。

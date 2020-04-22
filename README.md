# Osmunda
**The README in both Chinese and English is complete.**

**中文和英文版本的README均是完整的。**

## Introduction | 简介

Osmunda is an Android library to uncompress Open Street Map data, write into SQLite, for offline geocoding. 

Osmunda是一个Android库，读取开放街道地图数据，写入SQLite中，可用于离线地理编码等。

# How to use | 如何使用

## Gradle Settings | Gradle设置

#### Step 1. | 第一步

Add the JitPack repository to your project build.gradle file:

将JitPack仓库添加进您的项目的build.gradle文件中：

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

#### Step 2. | 第二步

Add the dependency

添加依赖

	dependencies {
	        implementation 'moe.sunjiao:osmunda:1.0.0'
	}

## Data import | 导入数据

Instantiate an OsmosisReader.

实例化一个OsmosisReader。
	
	val reader : OsmReader = OsmosisReader() 

Set import relations and ways data, if you don't need them, don't write these two lines. See [Performance/How to improve](## How to improve | 提升性能)

设置导入关系和道路数据，不需要的话，直接不写这两行就行了。参见 [性能/提升性能](## How to improve | 提升性能)

	reader.options.add(ImportOption.INCLUDE_RELATIONS) 
	reader.options.add(ImportOption.INCLUDE_WAYS) 

Set commitFrequency, don't set it to keep the default value (500,000 for pbf file, and 250,000 for xml file). See [Performance/How to improve](## How to improve | 提升性能)

设置提交频率，否则将使用默认设置（pbf文件：500,000，xml文件：250,000）。参见 [性能/提升性能](## How to improve | 提升性能)

# Performance | 性能

## Storage | 存储空间

## CPU

## Memory | 内存

## Time | 时间

## How to improve | 提升性能

# OSM data source | 开放街道地图数据源

# License | 许可证

Copyright (C) 2020 SUN JIAO (https://www.sunjiao.moe)
Apache License Version 2.0, January 2004
http://www.apache.org/licenses/


# References & Credits | 参考及致谢

Thanks to [spyhunter99/osmreader](https://github.com/spyhunter99/osmreader), I referred to it, rewrote its core algorithms in kotlin, fixed the Osmosis not work problem, and added reverse geocode features.

感谢[spyhunter99/osmreader](https://github.com/spyhunter99/osmreader)，我参考了该项目，用kotlin重写了它的核心算法，解决了Osmoisis在Android上不工作的问题，并添加了反向地理编码功能。

# My OSM Account | 我的OSM账号

[sun-jiao](https://www.openstreetmap.org/user/sun-jiao)

# Osmunda

**中文和英文版本的README均是完整的。**

## 简介

Osmunda是一个Android库，读取开放街道地图数据，写入SQLite中，可用于离线地理编码等。

# 如何使用

## Gradle设置

#### 第一步

将JitPack仓库添加进您的项目的build.gradle文件中：

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

#### 第二步

添加依赖

	dependencies {
	        implementation 'moe.sunjiao:osmunda:1.0.0'
	}

## 导入数据

实例化一个OsmosisReader。
	
	val reader : OsmReader = OsmosisReader() 

设置导入关系和道路数据，不需要的话，直接不写这两行就行了。参见 [性能/提升性能](#提升性能)

	reader.options.add(ImportOption.INCLUDE_RELATIONS) 
	reader.options.add(ImportOption.INCLUDE_WAYS) 

设置提交频率，否则将使用默认设置（pbf文件：500,000，xml文件：250,000）。参见 [性能/提升性能](#提升性能)

# 性能

## 存储空间

## CPU

## 内存

## 时间

## 提升性能

# 开放街道地图数据网站

# 许可证

Copyright (C) 2020 SUN JIAO (https://www.sunjiao.moe)
Apache License Version 2.0, January 2004
http://www.apache.org/licenses/


# 参考及致谢

感谢[spyhunter99/osmreader](https://github.com/spyhunter99/osmreader)，我参考了该项目，用kotlin重写了它的核心算法，解决了Osmoisis在Android上不工作的问题，并添加了反向地理编码功能。

# 我的OSM账号

[sun-jiao](https://www.openstreetmap.org/user/sun-jiao)

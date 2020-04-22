# Osmunda
[![](https://jitpack.io/v/moe.sunjiao/osmunda.svg)](https://jitpack.io/#moe.sunjiao/osmunda)

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

```kotlin
val reader : OsmReader = OsmosisReader() 
```

设置导入关系和道路数据，不需要的话，直接不写这两行就行了。参见 [性能/提升性能](#提升性能)

```kotlin
reader.options.add(ImportOption.INCLUDE_RELATIONS) 
reader.options.add(ImportOption.INCLUDE_WAYS) 
```

设置提交频率，否则将使用默认设置（pbf文件：500,000，xml文件：250,000）。参见 [性能/提升性能](#提升性能)

```
(reader as OsmosisReader).commitFrequency = 100000
```

设置OSM数据文件，context和数据库文件名，开始读取。

```kotlin
reader.read(File(requireContext().filesDir.absolutePath + "/hubei-latest.osm.pbf"), requireContext(), "hubei" )
```

## 获取导入状态

使用`reader.read`获取已读取的OSM记录数目，`reader.insert`获取已插入数据库的OSM记录数目。（二者不一致的原因参见 [性能/提升性能](#提升性能)）

使用`reader.progress`获取百分比形式的当前估计进度。

## 获取现有数据库列表

使用`Osmunda(requireContext()).getDatabaseList()`获取已经导入的数据库列表。

使用`Osmunda(requireContext()).getDatabaseByName(databaseName)`根据名称获取某一特定数据库。

## 地理编码（根据关键字搜索地点）

使用Geocoder类的search函数进行搜索，搜索时可以指定在数据库中的LIMIT和OFFSET，也可用经纬度指定范围。

例如，在武汉市范围内搜索「华中师范大学」，不设OFFSET，结果取前十条：

```kotlin
val hubeiDatabase: SQLiteDatabase = Osmunda(requireContext()).getDatabaseByName("hubei")
val list: List<SearchResult> = Geocoder(hubeiDatabase).search("华中师范大学", 10, 0, 30.7324, 114.6589, 30.3183, 114.0588)
val list2: List<SearchResult> = Geocoder(hubeiDatabase).search("华中师范大学", 10, 0)
```

如果是在地图上进行搜索，也可以将当前MapView的BoundingBox传入：

```kotlin
val box : BoundingBox = mapView.boundingBox
val list3: List<SearchResult> = Geocoder(hubeiDatabase).search("华中师范大学", 10, 0, box)
```

## 反向地理编码（根据地理坐标搜索地点名称）



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

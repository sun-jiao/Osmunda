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

设置导入关系和道路数据，如无需导入，请不要编写以下代码。参见 [性能/提升性能](#提升性能)

```kotlin
reader.options.add(ImportOption.INCLUDE_RELATIONS)	//导入关系数据
reader.options.add(ImportOption.INCLUDE_WAYS)		//导入道路数据
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
```

如果不设置范围，将会在数据库的所有记录中搜索：

```kotlin
val list2: List<SearchResult> = Geocoder(hubeiDatabase).search("华中师范大学", 10, 0)
```

如果是在地图上进行搜索，可以直接将当前MapView的BoundingBox传入：

```kotlin
val box : BoundingBox = mapView.boundingBox
val list3: List<SearchResult> = Geocoder(hubeiDatabase).search("华中师范大学", 10, 0, box)
```

## 反向地理编码（根据地理坐标搜索地点名称）

使用ReverseGeocoder类的search函数进行搜索，搜索时可以指定在数据库中的LIMIT和OFFSET。

```kotlin
val list: List<SearchResult> = ReverseGeocoder(hubeiDatabase).search(30.51910, 114.35775, 10, 0)
```

可以将Android的Location或Osmdroid的GeoPoint及IGeoPoint直接作为参数传入：

```kotlin
val location : Location = Location(GPS_PROVIDER)
val list2: List<SearchResult> = ReverseGeocoder(hubeiDatabase).search(location, 100, 0)
val geoPoint : GeoPoint = GeoPoint(30.51910, 114.35775)
val list3: List<SearchResult> = ReverseGeocoder(hubeiDatabase).search(geoPoint, 100, 0)
val iGeoPoint : IGeoPoint = mapView.mapCenter
val list4: List<SearchResult> = ReverseGeocoder(hubeiDatabase).search(iGeoPoint, 100, 0)
```

# 性能

## 存储空间

湖北省的pbf数据约11.64MiB (17,237,672字节)，其中含有2,417,117个元素，转换为了5,505,162条数据库记录。

解压出的数据库文件273.91MiB (287,219,712字节)，约为pbf的16.78倍。

bz2文件的大小约为相同内容的pbf文件的1.68倍，故解压出的数据库文件大小约为其10倍。

不同区域的文件大小并非「区域越大，数据越多」，还受到当地人口数量、人类聚居地密集程度、经济发展程度影响，还与开放街道地图服务的可用性有关。例如，人口密集、经济发达的广东省拥有73M的数据，而地广人稀的新疆和西藏仅分别有17M和18M的数据（均为pbf格式）。

## CPU

## 内存

## 时间

## 提升性能

# 开放街道地图数据网站

[Planet OSM](https://planet.openstreetmap.org/)是所有数据的原始来源，由开放街道地图运营，但其下载速度受限。

可从其它镜像数据网站下载：[网站列表](https://wiki.openstreetmap.org/wiki/Planet.osm)

对于中国用户，我推荐[开放街道地图法国社区网站](http://download.openstreetmap.fr/extracts/asia/china/)，其中有中国的分省数据，大大方便了下载和使用。

# 许可证

Copyright (C) 2020 SUN JIAO (https://www.sunjiao.moe)
Apache License Version 2.0, January 2004
http://www.apache.org/licenses/

# 参考及致谢

感谢[spyhunter99/osmreader](https://github.com/spyhunter99/osmreader)，我参考了该项目，用kotlin重写了它的核心算法，解决了Osmoisis在Android上不工作的问题，并添加了反向地理编码功能。

# 我的开放街道地图账号

[sun-jiao](https://www.openstreetmap.org/user/sun-jiao)，主要活跃于武汉市。

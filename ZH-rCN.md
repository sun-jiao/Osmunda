# Osmunda
[![](https://jitpack.io/v/moe.sunjiao/osmunda.svg)](https://jitpack.io/#moe.sunjiao/osmunda)

## 简介

*Osmunda*是一个Android库，读取开放街道地图数据（osm.pbf, osm.bz2及osm.gz格式, 后两者本质上都是xml），写入SQLite中，可用于离线地理编码等。

名称为拉丁文，来源于[紫萁属](http://www.iplant.cn/info/Osmunda)的学名，是一类蕨类植物，因其以开放街道地图的英文缩写osm开头而选用作名称。

## 开发进度

正在开发中。

### 当前开发计划

- 更多样化的数据导入选项。
- 数据库结构优化，避免Nodes和Way_Node必须分开两次查询的问题。
- 尝试采用NOSQL数据库，由于OSM数据使用大量键值对，因此NOSQL数据库可能是更好的选择。
- 更多语言的Statement支持。

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

关于数据的获取，参见 [开放街道地图数据网站](#开放街道地图数据网站)

实例化一个OsmosisReader。

```kotlin
val reader : OsmReader = OsmosisReader() 
```

设置导入关系和道路数据，如无需导入，请不要编写以下代码。参见 [存储空间](#存储空间)

```kotlin
reader.options.add(ImportOption.INCLUDE_RELATIONS)	//导入关系数据
reader.options.add(ImportOption.INCLUDE_WAYS)		//导入道路数据
```

设置提交频率，否则将使用默认设置（5,000）。参见 [commitFrequency(提交频率)](#commitFrequency(提交频率))

```
(reader as OsmosisReader).commitFrequency = 100000
```

设置OSM数据文件，context和数据库文件名，开始读取。

```kotlin
reader.readData(File(requireContext().filesDir.absolutePath + "/hubei-latest.osm.pbf"), requireContext(), "hubei" )
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

测试机器：Google Pixel 3，Android Q (10.0)

测试文件：hubei.osm.pbf，rhode-island.osm.bz2

以下测试数据均在以上环境中测得。

## 存储空间

湖北省的pbf文件大小为11.64 MiB (17,237,672 字节)，其中含有2,417,117个元素，转换为5,505,162条数据库记录。

解压出的数据库文件273.91 MiB (287,219,712 字节)，约为pbf的16.78倍。

罗德岛的osm.bz2文件大小为21.9 MiB (23,009,830 字节)，其中含有1,897,371个元素，转换为4,525,039条数据库记录。

解压出的数据库文件198.67 MiB (208,318,464 字节)，约为osm.bz2的9.05倍。

不同区域的文件大小并非「区域越大，数据越多」，还受到当地人口数量、人类聚居地密集程度、经济发展程度影响，还与开放街道地图服务的可用性有关。例如，人口密集、经济发达的广东省拥有73M的数据，而地广人稀的新疆和西藏仅分别有17M和18M的数据（均为pbf格式），请根据数据的实际大小合理安排您的应用中的数据，如果没有可用的数据，您可以从[overpass-api]https://overpass-api.de/api/map?bbox=114.3495,30.5132,114.3671,30.5258。

您还可以根据自己应用的需求，选择是否导入 relation 数据和 way 数据，具体代码见[导入数据](#导入数据)

## commitFrequency(提交频率)

由于读取操作发生在Osmosis库而非本库中，本库中的OsmosisReader类仅在每次Osmosis库读取一个元素后被调用一次process()函数，因此无法将所有插入操作包含在同一个Transaction中。

为了避免进行逐条数据插入时频繁开关Transaction造成的高耗时，我在OsmosisReader类中设置了commitFrequency变量，当待插入的记录达到commitFrequency规定的数量时，将会开启一个Transaction进行批量插入操作。

在批量插入之前，所有当前已读取的待插入记录都被存储在内存中，如果commitFrequency过高，将会导致过高的内存占用；而如果commitFrequency过低，则会频繁开关Transaction，导致过高的耗时。

commitFrequency的默认值为 5,000，您可以自行修改，还可以在您的应用中根据操作环境设置不同的值。

## 时间

### 数据转换

就具体操作而言，pbf文件的读取速度远大于xml格式，读取25万条数据约需要0.3-1秒不等，而读取相同大小的xml数据耗时5-15秒。（可能与读取内容有关，例如way node只有way_id和node_id两个属性，其读取速度可能快于有8个属性的node）插入25万条数据耗时4-7秒，与文件格式无关。

就总耗时而言，当commitFrequency设为 1,000 ~ 500,000 时，湖北省的pbf文件导出耗时均约为2分钟，罗德岛的osm.bz2文件导出耗时则约为4分钟，过低或过高均会导致操作耗时延长，甚至几乎不能完成。

### 地理编码

反向地理编码的查询操作耗时约3秒，根据查询结果获取完整地址的操作耗时0.3-4秒

## CPU

数据读取及数据库写入操作的CPU占用约为10%-30%。

## 内存

数据读取及数据库写入操作的内存占用约为200M-1G。

# 开放街道地图数据网站

[Planet OSM](https://planet.openstreetmap.org/)是所有数据的原始来源，由开放街道地图运营，但其下载速度受限。

可从其它镜像数据网站下载：[网站列表](https://wiki.openstreetmap.org/wiki/Planet.osm)

对于中国用户，我推荐[开放街道地图法国社区网站](http://download.openstreetmap.fr/extracts/asia/china/)，其中有中国的分省数据，大大方便了下载和使用。

您还可以

# 许可证

Copyright (C) 2020 SUN JIAO (https://www.sunjiao.moe)<br/>
Apache License Version 2.0, January 2004<br/>
http://www.apache.org/licenses/

# 参考及致谢

感谢[spyhunter99/osmreader](https://github.com/spyhunter99/osmreader)，我参考了该项目，用kotlin重写了它的核心算法，解决了Osmoisis在Android上不工作的问题，并添加了反向地理编码功能。

# 我的开放街道地图账号

[sun-jiao](https://www.openstreetmap.org/user/sun-jiao)，主要活跃于武汉市。

# Osmunda

[![](https://jitpack.io/v/moe.sunjiao/osmunda.svg)](https://jitpack.io/#moe.sunjiao/osmunda)

[中文版](./ZH-rCN.md)

## Introduction

*Osmunda* is an Android library that reads open street map data (osm.pbf, osm.bz2 and osm.gz formats, the latter two are essentially xml), is written to SQLite, and can be used for offline geocoding, etc.

Osmunda is the scientific name of [a genus of plant](https://en.wikipedia.org/wiki/Osmunda), it's a kind of fern, which was chosen because it starts with OSM.

## Development progress

pre-release

### To-do

- More data import options. 
- Optimizing database structure to avoid the problem that Nodes, Way and Relation must be queried separately.
- Try to use NOSQL database, because OSM data uses a lot of key-value pairs, so NOSQL database may be a better choice.
- Statement support in more languages.

# How to use

## Gradle Settings

#### Step 1.

Add the JitPack repository to your project build.gradle file:

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

#### Step 2.

Add the dependency
```
dependencies {
        implementation 'moe.sunjiao:osmunda:1.0.0'
}
```

## Data import

For data Source, see [OSM data website](#OSM data website)

Instantiate an OsmosisReader.

```kotlin
val reader: OsmReader = OsmosisReader ()
```

Set import relations and ways data, if you don't need them, please do not write the following code. See [Storage](#Storage)

```kotlin
reader.options.add (ImportOption.INCLUDE_RELATIONS) // Import relationa data
reader.options.add (ImportOption.INCLUDE_WAYS) // Import ways data
```

Set the commit frequency, otherwise the default setting (5,000) will be used. See [commitFrequency] (#commitFrequency)

```
(reader as OsmosisReader).commitFrequency = 100000
```

Set the OSM data file, context and database file name, and start reading.

```kotlin
reader.readData (File (requireContext (). filesDir.absolutePath + "/hubei-latest.osm.pbf"), requireContext (), "hubei")
			//filename								context		database filename
```

## Get import status

Use `reader.read` to get the number of OSM records read, and `reader.insert` to get the number of OSM records inserted into the database. (For the reason of difference between them, please refer to [Performance / Improve Performance] (#Improve Performance))

Use `reader.progress` to get the current estimated progress as a percentage.

## Get a list of existing databases

Use `Osmunda(requireContext()).GetDatabaseList()` to get the list of imported databases.

Use `Osmunda(requireContext()).GetDatabaseByName(databaseName)` to get a specific database based with the name.

## Geocoding

Use the search function of the Geocoder class to search. You can specify LIMIT and OFFSET in the database when searching, and you can also specify the range of latitude and longitude.

For example, searching for "Central China Normal University" within the scope of Wuhan, LIMIT 10 result, without setting OFFSET:

```kotlin
val hubeiDatabase: SQLiteDatabase = Osmunda(requireContext()).getDatabaseByName("hubei")
val list: List<SearchResult> = Geocoder(hubeiDatabase).search("Central China Normal University", 10, 0, 30.7324, 114.6589, 30.3183, 114.0588)
```

If you don't set the range, all records in the database will be searched:

```kotlin
val list2: List<SearchResult> = Geocoder(hubeiDatabase).search("Central China Normal University", 10, 0)
```

If you are searching on the map, you can directly pass the BoundingBox of the current MapView:

```kotlin
val box : BoundingBox = mapView.boundingBox
val list3: List<SearchResult> = Geocoder(hubeiDatabase).search("Central China Normal University", 10, 0, box)
```

## Reverse geocoding

Use the search function of the ReverseGeocoder class to search. You can specify LIMIT and OFFSET in the database when searching.

```kotlin
val list: List<SearchResult> = ReverseGeocoder(hubeiDatabase).search(30.51910, 114.35775, 10, 0)
```

You can directly pass the Android Location or Osmdroid GeoPoint and IGeoPoint as parameters:

```kotlin
val location : Location = Location(GPS_PROVIDER)
val list2: List<SearchResult> = ReverseGeocoder(hubeiDatabase).search(location, 100, 0)
val geoPoint : GeoPoint = GeoPoint(30.51910, 114.35775)
val list3: List<SearchResult> = ReverseGeocoder(hubeiDatabase).search(geoPoint, 100, 0)
val iGeoPoint : IGeoPoint = mapView.mapCenter
val list4: List<SearchResult> = ReverseGeocoder(hubeiDatabase).search(iGeoPoint, 100, 0)
```

# Performance

Test devices: Google Pixel 3, Android Q (10.0)

Test files: hubei.osm.pbf, rhode-island.osm.bz2

The following data are measured in above environment.

## Storage

The pbf file size of Hubei Province is 11.64 MiB (17,237,672 bytes), which contains 2,417,117 elements, converted into 5,505,162 database records.

The decompressed database file is 273.91 MiB (287,219,712 bytes), approximately 16.78 times of pbf.

The osm.bz2 file size of Rhode Island is 21.9 MiB (23,009,830 bytes), which contains 1,897,371 elements, converted into 4,525,039 database records.

The decompressed database file is 198.67 MiB (208,318,464 bytes), approximately 9.05 times of osm.bz2.

The data file in a large areas is not necessarily larger than ones in small areas, it is also infected by the local population, density of human settlements, and economic development. It is also related to the availability of Open Street Map services. For example, Guangdong is a populated and developed province and it has 73M of data, while the sparsely populated Xinjiang and Tibet only have 17M and 18M of data (all in pbf format). Please arrange your application according to the actual size of the data. If there is no available data, you can go to overpass-api https://overpass-api.de/api/map?bbox=min_longitude,min_latitude,max_longitude,max_latitude.

You can also choose whether to import relation data and way data according to the needs of your application. For the specific code, see [Data import] (#Data import)

## commitFrequency (commit frequency)

Since the read operation occurs in the Osmosis library instead of this library, the OsmosisReader class in this library is only called once after each time the Osmosis library reads an element. The process () function cannot be included in the same Transaction in.

In order to avoid the high time consumption caused by frequent switching of transactions when inserting data one by one, I set the commitFrequency variable in the OsmosisReader class. When the number of records to be inserted reaches the number specified by commitFrequency, a Transaction will be opened for batch insertion operations.

Before batch insertion, all currently read pending records are stored in memory. If the commitFrequency is too high, it will cause too high memory usage; if the commitFrequency is too low, the Transaction will be frequently switched, resulting in too high Time-consuming.

The default value of commitFrequency is 5,000, you can modify it in your code according to the environment of your application.

## Time

### Database operation

In terms of read and insert operations, the reading speed of the pbf file is much faster than the XML format. It takes about 0.3-1 seconds to read 250,000 pieces of data, and it takes 5-15 seconds to read the same size of XML data. Inserting 250,000 pieces of data takes 4-7 seconds, regardless of the file format.

In terms of total time consumption, when commitFrequency is set to 1,000 ~ 500,000, the export time of Hubei Province's pbf file is about 2 minutes, and the export time of Rhode Island's osm.bz2 file is about 4 minutes, too small or too large commitFrequency will cause the operation to take a long time, and even almost impossible to complete.

### Geocoding

The query operation of reverse geocoding takes 3-4 seconds, and the operation of obtaining the complete address according to the query result takes 0.3-3 seconds. If you query multiple geographic information records at one time, please do not get all the complete addresses at once, but do it when the user accesses a certain record.

## CPU

The CPU usage of data read and database write operations is about 10% -30%.

## Memory

The memory consumption of data read and database write operations is about 200M-1G.

# OSM data website

[Planet OSM](https://planet.openstreetmap.org/) is the original source of all data, operated by the Open Street Map, but its download speed is limited.

It can be downloaded from other mirror data websites: [Site List] (https://wiki.openstreetmap.org/wiki/Planet.osm)

You can also export the xml file of specific area by yourself:  https://overpass-api.de/api/map?bbox=min_longitude,min_latitude,max_longitude,max_latitude .

Please use files in pbf format as much as possible, because of its significant advantages in space occupation and time-consuming of import.

# License

Copyright (C) 2020 SUN JIAO (https://www.sunjiao.moe) <br/>
Apache License Version 2.0, January 2004 <br/>
http://www.apache.org/licenses/

# References & Credits

Thanks to [spyhunter99 / osmreader] (https://github.com/spyhunter99/osmreader), I referred to the project, rewrote its core algorithms in kotlin, fixed the Osmosis not work problem, and added reverse geocoding feature. 

# My OSM Account

[sun-jiao] (https://www.openstreetmap.org/user/sun-jiao), mainly active in Wuhan.

# Osmunda
**The README in both Chinese and English is complete.**

[中文介绍](./ZH-rCN.md)

## Introduction

Osmunda is an Android library to uncompress Open Street Map data, write into SQLite, for offline geocoding. 

# How to use

## Gradle Settings

#### Step 1.

Add the JitPack repository to your project build.gradle file:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

#### Step 2.

Add the dependency

	dependencies {
	        implementation 'moe.sunjiao:osmunda:1.0.0'
	}

## Data import

Instantiate an OsmosisReader.
	
	val reader : OsmReader = OsmosisReader() 

Set import relations and ways data, if you don't need them, don't write these two lines. See [Performance/How to improve](#improve)

	reader.options.add(ImportOption.INCLUDE_RELATIONS) 
	reader.options.add(ImportOption.INCLUDE_WAYS) 

Set commitFrequency, don't set it to keep the default value (500,000 for pbf file, and 250,000 for xml file). See [Performance/How to improve](#improve)

# Performance

## Storage

## CPU

## Memory

## Time

## Improve

# OSM data website

# License

Copyright (C) 2020 SUN JIAO (https://www.sunjiao.moe)
Apache License Version 2.0, January 2004
http://www.apache.org/licenses/


# References & Credits

Thanks to [spyhunter99/osmreader](https://github.com/spyhunter99/osmreader), I referred to it, rewrote its core algorithms in kotlin, fixed the Osmosis not work problem, and added reverse geocode features.

# My OSM Account

[sun-jiao](https://www.openstreetmap.org/user/sun-jiao)

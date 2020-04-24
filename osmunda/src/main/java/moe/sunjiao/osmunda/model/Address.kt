package moe.sunjiao.osmunda.model

import android.content.ContentValues.TAG
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.util.*
import kotlin.collections.HashMap

class Address(val name: String, databaseId: Long, database: SQLiteDatabase, val latitude: Double, val longitude: Double, private var locale: Locale? = null ) {
    var state: String = ""
    var city: String = ""
    var postcode: String = ""
    var housenumber: String = ""

    var fullAddress: String = ""
    var country: String = ""
    var county: String = ""
    var housename: String = ""
    var street: String = ""
    var suburb: String = ""
    var neighbourhood: String = ""
    var town: String = ""

    var phone: String = ""
    var website: String = ""

    init {
        val lat: String = latitude.toString()
        val lon: String = longitude.toString()
        try {
            database.beginTransaction()
            if (locale == null) {
                val cursor: Cursor = database.query("tag", arrayOf("k,v"), "id = ? and k like '%country_code' ", arrayOf(databaseId.toString()), null, null, null, null)
                if (cursor.moveToNext())
                    locale = Locale(cursor.getString(1))
                else {
                    val cursor1: Cursor = database.rawQuery("SELECT * FROM tag inner join nodes, way_no on tag.id=way_no.way_id and way_no.node_id=nodes.id where k like '%country_code' group by tag.id order by (lat - ?) * (lat - ?) + (lon - ?) * (lon - ?)  asc limit 1 ",
                        arrayOf(lat, lat, lon, lon))
                    if (cursor1.moveToNext())
                        locale = Locale( cursor1.getString(cursor1.getColumnIndex("v")))
                    else
                        locale = Locale.ROOT
                    cursor1.close()
                }
                cursor.close()
            }
            val tag: Cursor = database.query("tag", arrayOf("k,v"), "id = ?", arrayOf(databaseId.toString()), null, null, null, null)
            while (tag.moveToNext()) {
                val key : String = tag.getString(0).toLowerCase(Locale.ROOT)
                val value : String = tag.getString(1).toLowerCase(Locale.ROOT)
                if (phone=="" &&(key == "phone" || key.endsWith(":phone"))) {
                    phone = value
                } else if (website == "" && (key == "website" || key.endsWith(":website"))) {
                    website = value
                } else if (key == "city" || key.endsWith(":city")) {
                    city = value
                } else if (key == "province" || key.endsWith(":state") || key.endsWith(":province")) {
                    state = value
                } else if (key.endsWith("housenumber")) {
                    housenumber = value
                } else if (key.endsWith("housename")) {
                    housename = value
                } else if (key.endsWith("postcode")) {
                    postcode = value
                } else if (key.endsWith("_name") || key.endsWith("county") || key.endsWith("district")) {
                    county = value
                } else if (key.startsWith("addr:street") || key == "street:addr") {
                    street = value
                } else if (key == "gnis:country_name") {
                    country = value
                } else if ("addr:country" == key) {
                    country = value
                } else if ("is_in:country" == key) {
                    country = value
                } else  if ("operator" == key) {
                    neighbourhood = value
                } else if ("neighbourhood" == key) {
                    if (neighbourhood != "")
                        neighbourhood = value
                } else if ("addr:neighbourhood" == key) {
                    if (neighbourhood != "")
                        neighbourhood = value
                }
            }
            tag.close()
            if (country == ""){
                val cursor: Cursor = database.rawQuery("SELECT * FROM tag inner join nodes, way_no on tag.id=way_no.way_id and way_no.node_id=nodes.id where k like '%country' group by tag.id order by (lat - ?) * (lat - ?) + (lon - ?) * (lon - ?)  asc limit 1 ",
                    arrayOf(lat, lat, lon, lon))
                if (cursor.moveToNext())
                    country = cursor.getString(cursor.getColumnIndex("v"))
                cursor.close()
            }
            if (state == ""){
                val cursor: Cursor = database.rawQuery("SELECT * FROM tag inner join nodes, way_no on tag.id=way_no.way_id and way_no.node_id=nodes.id where k like '%state' or k like '%province' group by tag.id order by (lat - ?) * (lat - ?) + (lon - ?) * (lon - ?)  asc limit 1 ",
                    arrayOf( lat, lat, lon, lon))
                if (cursor.moveToNext())
                    state = cursor.getString(cursor.getColumnIndex("v"))
                cursor.close()
            }
            if (city == ""){
                val cursor: Cursor = database.rawQuery("SELECT * FROM tag inner join nodes, way_no on tag.id=way_no.way_id and way_no.node_id=nodes.id where k like '%city' group by tag.id order by (lat - ?) * (lat - ?) + (lon - ?) * (lon - ?)  asc limit 1 ",
                    arrayOf(lat, lat, lon, lon))
                if (cursor.moveToNext())
                    city = cursor.getString(cursor.getColumnIndex("v"))
                cursor.close()
            }
            if (county == ""){
                val list = arrayListOf<result>()
                val cursor: Cursor = database.rawQuery("SELECT * FROM tag inner join nodes, way_no on tag.id=way_no.way_id and way_no.node_id=nodes.id where k like '%county' or k like '%district' group by tag.id order by (lat - ?) * (lat - ?) + (lon - ?) * (lon - ?)  asc limit 1 ",
                    arrayOf(lat, lat, lon, lon))
                if (cursor.moveToNext())
                list.add(result(
                    cursor.getString(cursor.getColumnIndex("v")),
                    cursor.getDouble(cursor.getColumnIndex("lat")),
                    cursor.getDouble(cursor.getColumnIndex("lon"))))
                cursor.close()
                val cursor2: Cursor = database.rawQuery("SELECT * FROM tag inner join nodes on tag.id=nodes.id where (k = 'place' and (v = 'county' or v = 'district')) or (k = 'china_class' and ( v = 'xian')) group by tag.id order by (lat - ?) * (lat - ?) + (lon - ?) * (lon - ?)  asc limit 1 ",
                    arrayOf( lat, lat, lon, lon))
                if (cursor2.moveToNext()){
                    val id = cursor2.getLong(cursor2.getColumnIndex("id"))
                    val cursor3: Cursor = database.rawQuery("SELECT * FROM tag where id = $id and k = 'name' ", null)
                    if (cursor3.moveToNext())
                    list.add(
                        result(
                            cursor3.getString(cursor3.getColumnIndex("v")),
                            cursor2.getDouble(cursor2.getColumnIndex("lat")),
                        cursor2.getDouble(cursor2.getColumnIndex("lon")))
                    )
                    cursor3.close()
                }
                cursor2.close()
                list.sortBy { (it.lat - latitude) * (it.lat - latitude) + (it.lon - longitude) * (it.lon - longitude) }
                county = list[0].name
            }
            if (town == ""){
                val list = arrayListOf<result>()
                val cursor: Cursor = database.rawQuery("SELECT * FROM tag inner join nodes, way_no on tag.id=way_no.way_id and way_no.node_id=nodes.id where k like '%town' group by tag.id order by (lat - ?) * (lat - ?) + (lon - ?) * (lon - ?)  asc limit 1 ",
                    arrayOf(lat, lat, lon, lon))
                if (cursor.moveToNext())
                    list.add(result(
                        cursor.getString(cursor.getColumnIndex("v")),
                        cursor.getDouble(cursor.getColumnIndex("lat")),
                        cursor.getDouble(cursor.getColumnIndex("lon"))))
                cursor.close()
                val cursor2: Cursor = database.rawQuery("SELECT * FROM tag inner join nodes on tag.id=nodes.id where (k = 'place' and v = 'town') or (k = 'china_class' and ( v = 'zhen' or v = 'xiang' or v = 'jiedao')) group by tag.id order by (lat - ?) * (lat - ?) + (lon - ?) * (lon - ?)  asc limit 1 ",
                    arrayOf( lat, lat, lon, lon))
                if (cursor2.moveToNext()){
                    val id = cursor2.getLong(cursor2.getColumnIndex("id"))
                    val cursor3: Cursor = database.rawQuery("SELECT * FROM tag where id = $id and k = 'name' ", null)
                    if (cursor3.moveToNext())
                        list.add(
                            result(
                                cursor3.getString(cursor3.getColumnIndex("v")),
                                cursor2.getDouble(cursor2.getColumnIndex("lat")),
                                cursor2.getDouble(cursor2.getColumnIndex("lon")))
                        )
                    cursor3.close()
                }
                cursor2.close()
                list.sortBy { (it.lat - latitude) * (it.lat - latitude) + (it.lon - longitude) * (it.lon - longitude) }
                town = list[0].name
            }
            database.setTransactionSuccessful()
            database.endTransaction()
        } catch (ex: Exception) {
        } finally {
            if(housename != "")
                housename = "($housename)"
            if(housenumber != "")
                housenumber = "$housenumber, "
            if(postcode != "")
                postcode = " ($postcode)"
            fullAddress = "$name$housename, $neighbourhood, $housenumber$street, $suburb, $town, $county, $city, $state, $country$postcode"
            Log.i(TAG, "$name  $databaseId  $fullAddress")
        }
    }

    constructor(result: SearchResult) : this(result.name, result.databaseId, result.database, result.lat, result.lon)

    val CN: HashMap<String, String> = hashMapOf()

    class result (val name: String, val lat: Double, val lon: Double)
}

/*
* In China, "place = " may means:
* value                     in raw data                   var of this class
* neighbourhood             有时候是小区，有时候是社区       【小区、学校、自然村等】
* village                   村
* suburb                    社区                          【社区】
* town                      有时候是街道                   【乡镇、街道】
* city_district district    市辖区                         无（区县统一为county）
* county                    区、县                         【区、县】
* city                      街道, 区县，市                  【所有名字里带「市」的，直辖市除外，因为程序无法分辨不同的市】
* region                    有的地级市用region表示。         无（市统一为city）
* state_district            省辖区，地区级，地级市           无（市统一为city）
* state province            省，这个是确定的                【省:统一为state】
* country country_code      这两个表示国家是确定的           【国家，没啥好解释的】
*
* china_class:
* provincialcapital         省会
* prefecturalcapital        市府
* countytown                县城
* xian                      县
* zhen                      镇
* xiang                     乡
* jiedao                    街道
* village                   村
* */
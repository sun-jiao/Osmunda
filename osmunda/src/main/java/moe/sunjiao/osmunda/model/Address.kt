package moe.sunjiao.osmunda.model

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log

class Address(val name: String, databaseId: Long, database: SQLiteDatabase) {
    var state: String = ""
    var city: String = ""
    var postcode: String = ""
    var housenumber: String = ""

    var fullAddress: String = ""
    var country: String = ""
    var county: String = ""
    var housename: String = ""
    var unit: String = ""
    var street: String = ""
    var operator: String = ""
    var district: String = ""

    var phone: String = ""
    var website: String = ""

    init {
        try {
            val tag: Cursor = database.query("tag", arrayOf("k,v"), "id=?", arrayOf(databaseId.toString()), null, null, null, null)
            while (tag.moveToNext()) {
                Log.i(tag.getString(0), tag.getString(1))
                if ("phone".equals(tag.getString(0), ignoreCase = true)) {
                    phone = tag.getString(1)
                } else if ("contact:phone".equals(tag.getString(0), ignoreCase = true)) {
                    phone = tag.getString(1)
                } else if ("website".equals(tag.getString(0), ignoreCase = true)) {
                    website = tag.getString(1)
                } else if ("contact:website".equals(tag.getString(0), ignoreCase = true)) {
                    website = tag.getString(1)
                } else if ("note:website".equals(tag.getString(0), ignoreCase = true)) {
                    website = tag.getString(1)
                } else if ("incorporation:website".equals(tag.getString(0), ignoreCase = true)) {
                    website = tag.getString(1)
                } else if ("addr:city".equals(tag.getString(0), ignoreCase = true)) {
                    city = tag.getString(1)
                } else if ("addr:state".equals(tag.getString(0), ignoreCase = true)) {
                    state = tag.getString(1)
                } else if ("addr:province".equals(tag.getString(0), ignoreCase = true)) {
                    state = tag.getString(1)
                } else if ("is_in:province".equals(tag.getString(0), ignoreCase = true)) {
                    state = tag.getString(1)
                } else if ("province".equals(tag.getString(0), ignoreCase = true)) {
                    state = tag.getString(1)
                } else if ("addr:housenumber".equals(tag.getString(0), ignoreCase = true)) {
                    housenumber = tag.getString(1)
                } else if ("source:addr:housenumber".equals(tag.getString(0), ignoreCase = true)) {
                    housenumber = tag.getString(1)
                } else if ("addr:housename".equals(tag.getString(0), ignoreCase = true)) {
                    housename = tag.getString(1)
                } else if ("addr:postcode".equals(tag.getString(0), ignoreCase = true)) {
                    postcode = tag.getString(1)
                } else if ("gnis:county_name".equals(tag.getString(0), ignoreCase = true)) {
                    county = tag.getString(1)
                } else if ("addr:county".equals(tag.getString(0), ignoreCase = true)) {
                    county = tag.getString(1)
                } else if ("addr:street".equals(tag.getString(0), ignoreCase = true)) {
                    street = tag.getString(1)
                } else if ("street:addr".equals(tag.getString(0), ignoreCase = true)) {
                    street = tag.getString(1)
                } else if ("addr:unit".equals(tag.getString(0), ignoreCase = true)) {
                    unit = tag.getString(1)
                } else if ("addr:street_1".equals(tag.getString(0), ignoreCase = true)) {
                    street = tag.getString(1)
                } else if ("addr:street_2".equals(tag.getString(0), ignoreCase = true)) {
                    street = tag.getString(1)
                } else if ("addr:street_3".equals(tag.getString(0), ignoreCase = true)) {
                    street = tag.getString(1)
                } else if ("gnis:country_name".equals(tag.getString(0), ignoreCase = true)) {
                    country = tag.getString(1)
                } else if ("addr:country".equals(tag.getString(0), ignoreCase = true)) {
                    country = tag.getString(1)
                } else if ("is_in:country".equals(tag.getString(0), ignoreCase = true)) {
                    country = tag.getString(1)
                } else if ("is_in:country_code".equals(tag.getString(0), ignoreCase = true)) {
                    country = tag.getString(1)
                } else if ("operator".equals(tag.getString(0), ignoreCase = true)) {
                    operator = tag.getString(1)
                } else if ("addr:district".equals(tag.getString(0), ignoreCase = true)) {
                    district = tag.getString(1)
                } else if ("neighbourhood".equals(tag.getString(0), ignoreCase = true)) {
                    operator = tag.getString(1)
                }
            }
            tag.close()
        } catch (ex: Exception) {
            throw ex
        } finally {
            fullAddress =
                "$name ($housename), $housenumber, $street, $county, $city, $state, $country ($postcode)"
        }
    }

    constructor(result: SearchResult) : this(result.name, result.databaseId, result.database)
}
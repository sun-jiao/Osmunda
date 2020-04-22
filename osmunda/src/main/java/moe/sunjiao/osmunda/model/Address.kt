package moe.sunjiao.osmunda.model

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import java.sql.PreparedStatement

class Address(databaseId: Long, database: SQLiteDatabase) {
    var state: String? = null
    var city: String? = null
    var postcode: String? = null
    var housenumber: String? = null

    var fullAddress: String? = null
    var address_source: String? = null
    var country: String? = null
    var county: String? = null
    var housename: String? = null
    var unit: String? = null
    var street: String? = null
    var street_1: String? = null
    var street_2: String? = null
    var street_3: String? = null

    var phone: String? = null
    var website: String? = null

    init {
        try {
            val tag: Cursor = database.query("tag", arrayOf("k,v"), "id=?", arrayOf(databaseId.toString() + ""), null, null, null, null)
            while (tag.moveToNext()) {
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
                    street_1 = tag.getString(1)
                } else if ("addr:street_2".equals(tag.getString(0), ignoreCase = true)) {
                    street_2 = tag.getString(1)
                } else if ("addr:street_3".equals(tag.getString(0), ignoreCase = true)) {
                    street_3 = tag.getString(1)
                } else if ("gnis:country_name".equals(tag.getString(0), ignoreCase = true)) {
                    country = tag.getString(1)
                } else if ("addr:country".equals(tag.getString(0), ignoreCase = true)) {
                    country = tag.getString(1)
                }
            }
            tag.close()
        } catch (ex: Exception) {
            throw ex
        } finally {
        }
    }
}
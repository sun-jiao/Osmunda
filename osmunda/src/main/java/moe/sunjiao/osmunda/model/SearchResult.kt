package moe.sunjiao.osmunda.model

import android.database.sqlite.SQLiteDatabase

/**
 * search result of geocoder and reverse geocoder
 * can be converted to address
 * created on 4/22/2020.
 *
 * @author Sun Jiao(孙娇）
 *
 * @param lat latitude of location
 * @param lon longitude of location
 * @param name name of location
 * @param database database in which this element could be found
 * @param databaseId element id in osm data
 */

class SearchResult (
    val lat: Double,
    val lon: Double,
    val name: String,
    val database: SQLiteDatabase,
    val databaseId: Long
){

    /**
     * convert search result to a Address
     */
    fun toAddress() :  Address =  Address(this)
}
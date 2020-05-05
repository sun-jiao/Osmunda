package moe.sunjiao.osmunda.geocoder

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.location.Location
import moe.sunjiao.osmunda.model.SearchResult
import org.osmdroid.api.IGeoPoint
import org.osmdroid.util.GeoPoint
import java.util.*

/**
 * search place names using a geographic coordinates
 * created on 4/22/2020.
 *
 * @author Sun Jiao(孙娇）
 */

class ReverseGeocoder(val database: SQLiteDatabase) {

    /**
     * @param latitude latitude of target location
     * @param longitude longitude of target location
     * @param limit number of results
     * @param offset number of rows to skip before query
     * @return list of SearchResult
     */
    @Throws(Exception::class)
    fun search(
        latitude: Double,
        longitude: Double,
        limit: Int = 1,
        offset: Int = 0
    ): List<SearchResult>{
        if (latitude > 90.00 || latitude < -90.00 || longitude > 180.00 || longitude < -180.00 || limit < 0 || offset < 0)
            throw IllegalArgumentException()
        val lat = latitude.toString()
        val lon = longitude.toString()
        val resultList: MutableList<SearchResult> = ArrayList<SearchResult>()
        try {
            val cursor: Cursor = database.rawQuery("SELECT * FROM tag left join way_no on tag.id = way_no.way_id left join nodes on tag.id=nodes.id or nodes.id=way_no.node_id where k = \"name\" and lat > ? -0.01 and lat < ? +0.01 and lon > ? -0.01 and lon < ? +0.01 group by tag.v order by (lat - ?) * (lat - ?) + (lon - ?) * (lon - ?)  asc limit ? offset ? ",
                arrayOf(lat, lat, lon, lon, lat, lat, lon, lon, limit.toString(),offset.toString()))
            while (cursor.moveToNext()) {
                var databaseId : Long = cursor.getLong(cursor.getColumnIndex("way_id"))
                if (databaseId == 0L)
                    databaseId = cursor.getLong(cursor.getColumnIndex("id"))
                resultList.add(
                    SearchResult(cursor.getDouble(cursor.getColumnIndex("lat")),
                        cursor.getDouble(cursor.getColumnIndex("lon")),
                        cursor.getString(cursor.getColumnIndex("v")),
                         database, databaseId)
                )
            }
            cursor.close()
            resultList.sortBy { (it.lat - latitude) * (it.lat - latitude) + (it.lon - longitude) * (it.lon - longitude) }
        } catch (ex: Exception) {
            print( ex)
        } finally {

        }
        return resultList
    }

    /**
     * @param geoPoint coordinate of target location in osmdroid GeoPoint
     * @param limit number of results
     * @param offset number of rows to skip before query
     * @return list of SearchResult
     */
    @Throws(Exception::class)
    fun search(
        geoPoint: GeoPoint,
        limit: Int = 1,
        offset: Int = 0
    ): List<SearchResult>
            = search(geoPoint.latitude, geoPoint.longitude, limit, offset)

    /**
     * @param iGeoPoint coordinate of target location in osmdroid IGeoPoint
     * @param limit number of results
     * @param offset number of rows to skip before query
     * @return list of SearchResult
     */
    @Throws(Exception::class)
    fun search(
        iGeoPoint: IGeoPoint,
        limit: Int = 1,
        offset: Int = 0
    ): List<SearchResult>
            = search(iGeoPoint.latitude, iGeoPoint.longitude, limit, offset)

    /**
     * @param location coordinate of target location in android Location
     * @param limit number of results
     * @param offset number of rows to skip before query
     * @return list of SearchResult
     */
    @Throws(Exception::class)
    fun search(
        location: Location,
        limit: Int = 1,
        offset: Int = 0
    ): List<SearchResult>
            = search(location.latitude, location.longitude, limit, offset)

    /**
     * @param latitude latitude of target location
     * @param longitude longitude of target location
     * @param limit number of results
     * @param offset number of rows to skip before query
     * @return list of SearchResult
     */
    @Throws(Exception::class)
    fun search(
        latitude: String,
        longitude: String,
        limit: Int = 1,
        offset: Int = 0
    ): List<SearchResult>
            = search(latitude.toDouble(),longitude.toDouble(),limit,offset)

    /**
     * @param latitude latitude of target location
     * @param longitude longitude of target location
     * @param limit number of results
     * @param offset number of rows to skip before query
     * @return list of SearchResult
     */
    @Throws(Exception::class)
    fun search(
        latitude: Float,
        longitude: Float,
        limit: Int = 1,
        offset: Int = 0
    ): List<SearchResult>
            = search(latitude.toDouble(),longitude.toDouble(),limit,offset)
}
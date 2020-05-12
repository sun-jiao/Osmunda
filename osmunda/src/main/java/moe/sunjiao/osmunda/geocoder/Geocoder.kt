package moe.sunjiao.osmunda.geocoder

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import moe.sunjiao.osmunda.model.SearchResult
import org.osmdroid.util.BoundingBox
import java.util.*

/**
 * convert a name to geographic coordinates
 * created on 4/22/2020.
 *
 * @author Sun Jiao(孙娇）
 */

class Geocoder(val database: SQLiteDatabase) {

    /**
     * @param searchQueryOptional name of target location
     * @param limit number of results
     * @param offset number of rows to skip before query
     * @param maxLat max latitude of wuery range
     * @param maxLon max longitude of query range
     * @param minLat min latitude of query range
     * @param minLon min longitude of query range
     * @return list of SearchResult
     */
    @Throws(Exception::class)
    fun search (
        searchQueryOptional: String,
        limit: Int = 1,
        offset: Int = 0,
        maxLat: Double = 90.00,
        maxLon: Double = 180.00,
        minLat: Double = -90.00,
        minLon: Double = -180.00
    ): List<SearchResult> {
        val resultList: MutableList<SearchResult> = ArrayList<SearchResult>()

        try {
            val cursor: Cursor = database.rawQuery("SELECT * FROM tag left join way_no on tag.id = way_no.way_id left join nodes on tag.id=nodes.id or nodes.id=way_no.node_id where k like \"name%\" and v like ? and lat > ? and lat < ? and lon >? and lon < ? group by tag.v order by length(v) limit ? offset ?",
                    arrayOf("%$searchQueryOptional%", minLat.toString(), maxLat.toString(), minLon.toString(), maxLon.toString(), limit.toString(), offset.toString()))

            while (cursor.moveToNext()) {
                var databaseId : Long = cursor.getLong(cursor.getColumnIndex("way_id"))
                if (databaseId == 0L)
                    databaseId = cursor.getLong(cursor.getColumnIndex("id"))
                resultList.add(SearchResult(cursor.getDouble(cursor.getColumnIndex("lat")),
                    cursor.getDouble(cursor.getColumnIndex("lon")),
                    cursor.getString(cursor.getColumnIndex("v")),
                    database, databaseId))
            }
            cursor.close()
        } catch (ex: Exception) {
            print(ex)
        } finally {
        }
        return resultList
    }

    /**
     * @param searchQueryOptional name of target location
     * @param limit number of results
     * @param maxLat max latitude of wuery range
     * @param maxLon max longitude of query range
     * @param minLat min latitude of query range
     * @param minLon min longitude of query range
     * @return list of SearchResult
     */
    @Throws(Exception::class)
    fun search (
        searchQueryOptional: String,
        limit: Int,
        maxLat: Double,
        maxLon: Double,
        minLat: Double,
        minLon: Double
    ): List<SearchResult>
            = search(searchQueryOptional, limit, 0, maxLat, maxLon, minLat, minLon)

    /**
     * @param searchQueryOptional name of target location
     * @param limit number of results
     * @param offset number of rows to skip before query
     * @param boundingBox bounding box of query range
     * @return list of SearchResult
     */
    @Throws(Exception::class)
    fun search (
        searchQueryOptional: String,
        limit: Int,
        offset: Int,
        boundingBox: BoundingBox
    ): List<SearchResult>
            = search(searchQueryOptional, limit, offset, boundingBox.latNorth, boundingBox.lonEast,boundingBox.latSouth,boundingBox.lonWest)

    /**
     * @param searchQueryOptional name of target location
     * @param limit number of results
     * @param boundingBox bounding box of query range
     * @return list of SearchResult
     */
    @Throws(Exception::class)
    fun search (
        searchQueryOptional: String,
        limit: Int,
        boundingBox: BoundingBox
    ): List<SearchResult>
            = search(searchQueryOptional, limit, 0, boundingBox)

}
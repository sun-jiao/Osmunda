package moe.sunjiao.osmunda.geocoder

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import moe.sunjiao.osmunda.model.OsmType
import moe.sunjiao.osmunda.model.SearchResult
import org.osmdroid.util.BoundingBox
import java.util.*

class Geocoder(val database: SQLiteDatabase) {

    @Throws(Exception::class)
    fun search(
        searchQueryOptional: String,
        limit: Int,
        offset: Int,
        maxLat: Double,
        maxLon: Double,
        minLat: Double,
        minLon: Double
    ): List<SearchResult> {
        val resultList: MutableList<SearchResult> = ArrayList<SearchResult>()

        try {
            val cursor: Cursor = database.rawQuery("SELECT * FROM tag left join way_no on tag.id = way_no.way_id left join nodes on tag.id=nodes.id or nodes.id=way_no.node_id where k like \"name%\" and v like ? and lat > ? and lat < ? and lon >? and lon < ? group by tag.v order by length(v) limit ? offset ?",
                    arrayOf("%$searchQueryOptional%", minLat.toString(), maxLat.toString(), minLon.toString(), maxLon.toString(), limit.toString(), offset.toString()))

            while (cursor.moveToNext()) {
                val type = cursor.getInt(cursor.getColumnIndex("reftype"))
                val rowType: OsmType = OsmType.values()[type]
                var databaseId : Long = cursor.getLong(cursor.getColumnIndex("way_id"))
                if (databaseId == 0L)
                    databaseId = cursor.getLong(cursor.getColumnIndex("id"))
                resultList.add(SearchResult(cursor.getDouble(cursor.getColumnIndex("lat")),
                    cursor.getDouble(cursor.getColumnIndex("lon")),
                    cursor.getString(cursor.getColumnIndex("v")),
                    rowType, database, databaseId))
            }
            cursor.close()
        } catch (ex: Exception) {
            print(ex)
        } finally {

        }
        return resultList
    }

    fun search (searchQueryOptional: String, limit: Int, offset: Int): List<SearchResult>
            = search(searchQueryOptional, limit, offset, 90.00, 180.00, -90.00, -180.00)

    fun search (searchQueryOptional: String, limit: Int, offset: Int, boundingBox: BoundingBox): List<SearchResult>
            = search(searchQueryOptional, limit, offset, boundingBox.latNorth, boundingBox.lonEast,boundingBox.latSouth,boundingBox.lonWest)

}
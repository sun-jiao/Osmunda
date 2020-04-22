package moe.sunjiao.osmunda.geocoder

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import moe.sunjiao.osmunda.model.OsmType
import moe.sunjiao.osmunda.model.SearchResult
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
    ): List<SearchResult>? {
        val resultList: MutableList<SearchResult> = ArrayList<SearchResult>()

        try {
            val cursor: Cursor = database.rawQuery("SELECT * FROM tag inner join nodes on tag.id=nodes.id where k='name' and v like ? and lat > ? and lat < ? and lon >? and lon < ? limit ? offset ?",
                    arrayOf("%$searchQueryOptional%", minLat.toString(), maxLat.toString(), minLon.toString(), maxLon.toString(), limit.toString(), offset.toString()))

            while (cursor.moveToNext()) {
                val type = cursor.getInt(cursor.getColumnIndex("reftype"))
                val rowType: OsmType = OsmType.values().get(type)
                resultList.add(SearchResult(cursor.getDouble(cursor.getColumnIndex("lat")),
                    cursor.getDouble(cursor.getColumnIndex("lon")),
                    cursor.getString(cursor.getColumnIndex("v")),
                    rowType,
                    database,
                    cursor.getLong(cursor.getColumnIndex("id"))))
            }
            cursor.close()
        } catch (ex: Exception) {
            throw ex
        } finally {

        }
        return resultList
    }
}
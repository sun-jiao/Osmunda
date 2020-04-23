package moe.sunjiao.osmunda.geocoder

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.location.Location
import moe.sunjiao.osmunda.model.OsmType
import moe.sunjiao.osmunda.model.SearchResult
import org.osmdroid.util.GeoPoint
import java.util.*

class ReverseGeocoder(val database: SQLiteDatabase) {
    @Throws(Exception::class)
    fun search(
        lat: String,
        lon: String,
        limit: Int,
        offset: Int
    ): List<SearchResult>{
        val resultList: MutableList<SearchResult> = ArrayList<SearchResult>()
        try {
            val cursor: Cursor = database.rawQuery("SELECT * FROM tag inner join nodes on tag.id=nodes.id where k=\"name\" and lat > ? -0.1 and lat < ? +0.1 and lon > ? -0.1 and lon < ? +0.1 order by (lat - ?) * (lat - ?) + (lon - ?) * (lon - ?)  asc limit ? offset ? ",
                arrayOf(lat, lat, lon, lon, lat, lat, lon,lon,limit.toString(),offset.toString()))
            while (cursor.moveToNext()) {
                val type = cursor.getInt(cursor.getColumnIndex("reftype"))
                val rowType: OsmType = OsmType.values().get(type)
                resultList.add(
                    SearchResult(cursor.getDouble(cursor.getColumnIndex("lat")),
                        cursor.getDouble(cursor.getColumnIndex("lon")),
                        cursor.getString(cursor.getColumnIndex("v")),
                        rowType,
                        database,
                        cursor.getLong(cursor.getColumnIndex("id")))
                )
            }
            cursor.close()
        } catch (ex: Exception) {
            throw ex
        } finally {

        }
        return resultList
    }

    @Throws(Exception::class)
    fun search(
        geoPoint: GeoPoint,
        limit: Int,
        offset: Int
    ): List<SearchResult> {
        val lat: String = geoPoint.getLatitude().toString()
        val lon: String = geoPoint.getLongitude().toString()
        return search(lat,lon,limit,offset)
    }

    @Throws(Exception::class)
    fun search(
        location: Location,
        limit: Int,
        offset: Int
    ): List<SearchResult> {
        val lat: String = location.getLatitude().toString()
        val lon: String = location.getLongitude().toString()
        return search(lat,lon,limit,offset)
    }

    @Throws(Exception::class)
    fun search(
        latitude: Double,
        longitude: Double,
        limit: Int,
        offset: Int
    ): List<SearchResult> {
        val lat: String = latitude.toString()
        val lon: String = longitude.toString()
        return search(lat,lon,limit,offset)
    }
}
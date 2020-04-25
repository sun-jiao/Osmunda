package moe.sunjiao.osmunda.geocoder

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.location.Location
import moe.sunjiao.osmunda.model.OsmType
import moe.sunjiao.osmunda.model.SearchResult
import org.osmdroid.api.IGeoPoint
import org.osmdroid.util.GeoPoint
import java.util.*

class ReverseGeocoder(val database: SQLiteDatabase) {
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
            val cursor: Cursor = database.rawQuery(
                "SELECT * FROM tag inner join nodes on tag.id=nodes.id inner join way_no on tag.id=way_no.way_id and way_no.node_id=nodes.id where k = \"name\" and lat > ? -0.1 and lat < ? +0.1 and lon > ? -0.1 and lon < ? +0.1 group by tag.id order by (lat - ?) * (lat - ?) + (lon - ?) * (lon - ?)  asc limit ? offset ? ",
                arrayOf(lat, lat, lon, lon, lat, lat, lon, lon, limit.toString(),offset.toString()))
            while (cursor.moveToNext()) {
                val type = cursor.getInt(cursor.getColumnIndex("reftype"))
                val rowType: OsmType = OsmType.values()[type]
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
            resultList.sortBy { (it.lat - latitude) * (it.lat - latitude) + (it.lon - longitude) * (it.lon - longitude) }
        } catch (ex: Exception) {
            print( ex)
        } finally {

        }
        return resultList
    }

    @Throws(Exception::class)
    fun search(
        geoPoint: GeoPoint,
        limit: Int = 1,
        offset: Int = 0
    ): List<SearchResult> {
        val lat: Double = geoPoint.latitude
        val lon: Double = geoPoint.longitude
        return search(lat,lon,limit,offset)
    }

    @Throws(Exception::class)
    fun search(
        iGeoPoint: IGeoPoint,
        limit: Int = 1,
        offset: Int = 0
    ): List<SearchResult> {
        val lat: Double = iGeoPoint.latitude
        val lon: Double = iGeoPoint.longitude
        return search(lat,lon,limit,offset)
    }

    @Throws(Exception::class)
    fun search(
        location: Location,
        limit: Int = 1,
        offset: Int = 0
    ): List<SearchResult> {
        val lat: Double = location.latitude
        val lon: Double = location.longitude
        return search(lat,lon,limit,offset)
    }
}
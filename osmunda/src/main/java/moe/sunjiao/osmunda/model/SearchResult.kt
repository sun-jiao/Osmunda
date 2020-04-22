package moe.sunjiao.osmunda.model

import android.database.sqlite.SQLiteDatabase

class SearchResult (
    private val lat: Double,
    private val lon: Double,
    private val name: String,
    private val type: OsmType,
    private val database: SQLiteDatabase,
    private val databaseId: Long
)
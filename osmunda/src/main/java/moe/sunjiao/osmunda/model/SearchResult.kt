package moe.sunjiao.osmunda.model

import android.database.sqlite.SQLiteDatabase

class SearchResult (
    val lat: Double,
    val lon: Double,
    val name: String,
    val database: SQLiteDatabase,
    val databaseId: Long
){
    val toAddress : () -> Address = { Address(this)}
}
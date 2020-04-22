package moe.sunjiao.osmunda.model

class SearchResult (
    private val lat: Double,
    private val lon: Double,
    private val name: String,
    private val type: OsmType,
    private val databaseId: Long
)
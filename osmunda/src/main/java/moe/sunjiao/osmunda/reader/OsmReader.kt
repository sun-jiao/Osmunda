package moe.sunjiao.osmunda.reader

import android.content.Context
import moe.sunjiao.osmunda.model.ImportOption
import java.io.File

interface OsmReader {


    @Throws(Exception::class)
    fun read(file: File, context : Context, databaseName: String)

    val parserName: String
    val options: MutableSet<ImportOption>
    var read: () -> Long
    var elementCount: Long
    val batchSize: Int
    val progress: () -> Double
    var insert: () -> Long
}

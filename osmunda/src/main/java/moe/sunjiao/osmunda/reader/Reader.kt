package moe.sunjiao.osmunda.reader

import android.content.Context
import android.net.Uri
import moe.sunjiao.osmunda.model.ImportOption
import java.io.File

interface Reader {


    @Throws(Exception::class)
    fun readData(file: File, context : Context, databaseName: String)

    @Throws(Exception::class)
    fun readData(uri: Uri, context : Context, databaseName: String)

    val parserName: String
    val options: MutableSet<ImportOption>
    val read: Long
    var elementCount: Long
    val batchSize: Int
    val progress: Double
    val insert: Long
    var writerType : WriterType
}

package moe.sunjiao.osmunda.reader

import android.content.Context
import android.net.Uri
import moe.sunjiao.osmunda.model.ImportOption
import moe.sunjiao.osmunda.model.WriterType
import java.io.File

/**
 * base interface of all readers
 * created on 4/22/2020.
 *
 * @author Sun Jiao(孙娇）
 */

interface Reader {

    /**
     * @param file pbf or xml file to be read
     * @param context android context where this fun is called
     * @param databaseName name of database to be written
     */
    @Throws(Exception::class)
    fun readData(file: File, context : Context, databaseName: String)

    /**
     * @param uri pbf or xml uri to be read
     * @param context android context where this fun is called
     * @param databaseName name of database to be written
     */
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

package moe.sunjiao.osmunda.reader

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import crosby.binary.osmosis.OsmosisReader
import moe.sunjiao.osmunda.model.ImportOption
import moe.sunjiao.osmunda.model.WriterType
import moe.sunjiao.osmunda.writer.SQLiteWriter
import moe.sunjiao.osmunda.writer.SimpleSQLWriter
import moe.sunjiao.osmunda.writer.Writer
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer
import org.openstreetmap.osmosis.core.domain.v0_6.*
import org.openstreetmap.osmosis.core.task.v0_6.RunnableSource
import org.openstreetmap.osmosis.core.task.v0_6.Sink
import org.openstreetmap.osmosis.xml.common.CompressionMethod
import org.openstreetmap.osmosis.xml.v0_6.XmlReader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.*

/**
 * reader for xml and pbf, using osmosis reader
 * created on 4/22/2020.
 *
 * @author Sun Jiao(孙娇）
 */

class OsmosisReader :Reader, Sink {
    override val parserName: String = "Osmosis"
    override val read : Long
        get() {
            return if (::writer.isInitialized)
                writer.read
            else
                0
    }
    override var batchSize = 100
    override var elementCount: Long = 0
    override val options: MutableSet<ImportOption> = HashSet<ImportOption>()
    override val insert : Long
        get() {
            return if (::writer.isInitialized)
                writer.insert
            else
                0
    }

    private var isReading = false
    private var isFinished: Boolean = false
    var readProportion : Int = 1
    var insertProportion : Int = 1

    var commitFrequency : Int = 5000
    private var expectedRecordCount: Double = 0.00
    private lateinit var writer : Writer

    override var writerType : WriterType = WriterType.SQLITE_WRITER

    override val progress : Double
        get() {
            val prog0 = (read * readProportion + insert * insertProportion.toDouble()) / (expectedRecordCount * (readProportion + insertProportion))
            return if (isReading)
                if (prog0 > 1.00)
                    0.99
                else
                    prog0
            else
                if (isFinished)
                    1.toDouble()
                else
                    (-1).toDouble()
    }

    /**
     * @param file pbf or xml file to be read
     * @param context android context where this fun is called
     * @param databaseName name of database to be written
     */
    @Throws(Exception::class)
    override fun readData(file: File, context : Context, databaseName: String) {
        if (!file.exists())
            throw FileNotFoundException("File Not Found")

        reading(context, databaseName, FileInputStream(file), file.name)
    }

    /**
     * @param uri pbf or xml uri to be read
     * @param context android context where this fun is called
     * @param databaseName name of database to be written
     */
    override fun readData(uri: Uri, context: Context, databaseName: String) {

        val cursor: Cursor? = context.contentResolver.query( uri, null, null, null, null, null)
        var displayName = ""
        cursor?.use {
            if (it.moveToFirst()) {
                displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                Log.i("Osmosis", "Display Name: $displayName")
            } else {
                throw java.lang.IllegalArgumentException("Unable to determine file type.")
            }
        }?:throw java.lang.IllegalArgumentException("Unable to determine file type.")

        val fis: InputStream = context.contentResolver.openInputStream(uri) ?: throw java.lang.IllegalArgumentException()

        reading(context, databaseName, fis, displayName)
    }

    private fun reading(
        context: Context,
        databaseName: String,
        fis: InputStream,
        displayName: String
    ){
        val start = System.currentTimeMillis()
        val readerRunnableSource: RunnableSource
        expectedRecordCount = 0.1664 * fis.available()

        if (displayName.endsWith(".pbf")){
            readerRunnableSource = OsmosisReader(fis)
            expectedRecordCount = 0.33075 * fis.available()
            readProportion = 2
            insertProportion = 11
        } else if (displayName.endsWith(".gz")){
            readerRunnableSource = XmlReader(fis, false, CompressionMethod.GZip)
        } else if (displayName.endsWith(".bz2")){
            readerRunnableSource = XmlReader(fis, false, CompressionMethod.BZip2)
        } else
            throw java.lang.IllegalArgumentException("Unsupported file type.")
        isFinished = false
        when(writerType){
            WriterType.SIMPLE_SQL_WRITER -> {
                writer = SimpleSQLWriter(
                    context,
                    databaseName,
                    commitFrequency
                )
            }
            else -> {
                writer = SQLiteWriter(
                    context,
                    databaseName,
                    commitFrequency
                )
            }
        }
        val e : Exception = java.lang.Exception()
        println(e)
        isReading = true

        readerRunnableSource.setSink(this)
        println("starting import")
        val readerThread = Thread(
            readerRunnableSource
        )
        readerThread.uncaughtExceptionHandler =
            Thread.UncaughtExceptionHandler { thread, throwable ->
                println("import failed!")
                throwable.printStackTrace()
            }
        readerThread.start()
        while (readerThread.isAlive) {
            try {
                readerThread.join()
            } catch (e: InterruptedException) {
            }
        }
        println("import finished")
        writer.commit()
        fis.close()
        writer.setIndex()
        println("Total import time - " + (System.currentTimeMillis() - start) + "ms, total elements processed " + elementCount + " inserts " + read)
        isReading = false
        isFinished = true
    }

    override fun process(entityContainer: EntityContainer) {
        elementCount++
        writer.checkCommit()
        val entity = entityContainer.entity
        if (entity is Node) {
            val node : Node = entity
            if (!node.tags.isEmpty()) {
                val iterator: Iterator<Tag> = node.tags.iterator()
                while (iterator.hasNext()) {
                    val next = iterator.next()
                    writer.insertTag(node, next)
                }
            }
            writer.insertNode(node)
        } else if (entity is Way) {
            if (options.contains(ImportOption.INCLUDE_WAYS)) {
                val way:Way = entity
                if (!way.tags.isEmpty()) {
                    val iterator: Iterator<Tag> = way.tags.iterator()
                    while (iterator.hasNext()) {
                        val next = iterator.next()
                        writer.insertTag(way,next)
                    }
                }
                writer.insertWay(way)
                if (way.wayNodes != null) {
                    val iterator: Iterator<WayNode> = way.wayNodes.iterator()
                    while (iterator.hasNext()) {
                        val next = iterator.next()
                        writer.insertWayNode(way, next)
                    }
                }
            }
        } else if (entity is Relation) {
            if (options.contains(ImportOption.INCLUDE_RELATIONS)) {
                val relation: Relation = entity
                if (!relation.tags.isEmpty()) {
                    val iterator: Iterator<Tag> = relation.tags.iterator()
                    while (iterator.hasNext()) {
                        val next = iterator.next()
                        writer.insertTag(relation, next)
                    }
                }
                writer.insertRelation(relation)
                if (relation.members != null) {
                    val iterator: Iterator<RelationMember> =
                        relation.members.iterator()
                    while (iterator.hasNext()) {
                        val next = iterator.next()
                        writer.insertMember(relation, next)
                    }
                }
            }
        }
    }

    override fun close() {
    }

    override fun initialize(metaData: Map<String?, Any?>?) {}

    override fun complete() {}
}

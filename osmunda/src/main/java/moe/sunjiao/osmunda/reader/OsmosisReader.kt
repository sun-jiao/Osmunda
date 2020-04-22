package moe.sunjiao.osmunda.reader

import android.content.Context
import crosby.binary.osmosis.OsmosisReader
import moe.sunjiao.osmunda.model.ImportOption
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer
import org.openstreetmap.osmosis.core.domain.v0_6.*
import org.openstreetmap.osmosis.core.task.v0_6.RunnableSource
import org.openstreetmap.osmosis.core.task.v0_6.Sink
import org.openstreetmap.osmosis.xml.common.CompressionMethod
import org.openstreetmap.osmosis.xml.v0_6.XmlReader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.*

class OsmosisReader :OsmReader, Sink {
    override val parserName: String = "Osmosis"
    override var read:() -> Long = {
        if (::writer.isInitialized)
            writer.read
        else
            0
    }
    override var batchSize = 100
    override var elementCount: Long = 0
    override val options: MutableSet<ImportOption> = HashSet<ImportOption>()
    override var insert :() -> Long = {
        if (::writer.isInitialized)
            writer.insert
        else
            0
    }

    private var isReading = false
    var readProportion : Int = 1
    var insertProportion : Int = 1

    var commitFrequency : Int = 500000
    private var expectedRecordCount: Double = 0.00
    lateinit var writer : SQLiteWriter

    override val progress:()-> Double = {
        if (isReading) {
             (read() * readProportion + insert() * insertProportion.toDouble()) / (expectedRecordCount * (readProportion + insertProportion))
        } else
             (-1).toDouble()
    }

    @Throws(Exception::class)
    override fun read(file: File, context : Context, databaseName: String) {
        val reader: RunnableSource
        var fis: FileInputStream? = null
        val start = System.currentTimeMillis()
        var isPbf = false

        expectedRecordCount = 0.1968 * file.length()

        if (file.name.toLowerCase(Locale.ROOT).endsWith(".pbf")) {
            fis = FileInputStream(file)
            reader = OsmosisReader(fis)
            isPbf = true
            expectedRecordCount = 0.33075 * file.length()
            readProportion = 2
            insertProportion = 11
        } else if (file.name.toLowerCase(Locale.ROOT).endsWith(".gz")) {
            reader = XmlReader(file, false, CompressionMethod.GZip)
            commitFrequency = 250000
        } else if (file.name.toLowerCase(Locale.ROOT).endsWith(".bz2")) {
            reader = XmlReader(file, false, CompressionMethod.BZip2)
            commitFrequency = 250000
        } else
            throw IllegalArgumentException()

        if (!file.exists())
            throw FileNotFoundException("File Not Found")

        writer = SQLiteWriter(context, databaseName, commitFrequency)

        isReading = true

        reader.setSink(this)
        println("starting import")
        val readerThread = Thread(
            reader
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
        if (isPbf){
            fis!!.close()
        }
        println("Total import time - " + (System.currentTimeMillis() - start) + "ms, total elements processed " + elementCount + " inserts " + read())
        isReading = false
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

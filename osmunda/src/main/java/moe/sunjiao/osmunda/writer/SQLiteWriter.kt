package moe.sunjiao.osmunda.writer

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import moe.sunjiao.osmunda.Osmunda
import moe.sunjiao.osmunda.model.OsmType
import org.openstreetmap.osmosis.core.domain.v0_6.*

class SQLiteWriter (context : Context, databaseName: String, private val commitFrequency : Int = 5000): Writer {
    override var read: Long = 0
    override var insert : Long = 0
    private var commitCount = 1
    private val TAG = "SQLiteWriter"
    private var batchCount: Long = 0
    private val database : SQLiteDatabase
    private var nodeValuesList = ArrayList<ContentValues>()
    private var relationValuesList = ArrayList<ContentValues>()
    private var wayValuesList = ArrayList<ContentValues>()
    private var tagValuesList = ArrayList<ContentValues>()
    private var wayNodeValuesList = ArrayList<ContentValues>()
    private var memberValuesList = ArrayList<ContentValues>()

    init{
        database = Osmunda(context).getDatabaseByName(databaseName)
        database.execSQL("CREATE TABLE IF NOT EXISTS \"nodes\" (\"id\" INTEGER PRIMARY KEY  NOT NULL , \"lat\" DOUBLE NOT NULL , \"lon\" DOUBLE NOT NULL , \"version\" INTEGER, \"timestamp\" DATETIME, \"uid\" INTEGER, \"user\" TEXT, \"changeset\" INTEGER)")
        database.execSQL("CREATE TABLE IF NOT EXISTS \"relation_members\" (\"type\" TEXT NOT NULL , \"member_id\" INTEGER NOT NULL , \"role\" TEXT, \"relation_id\" INTEGER NOT NULL,\"insert_id\" INTEGER NOT NULL, PRIMARY KEY( \"relation_id\",\"member_id\",\"insert_id\" ))")
        database.execSQL("CREATE TABLE IF NOT EXISTS \"relations\" (\"id\" INTEGER PRIMARY KEY  NOT NULL , \"user\" TEXT, \"uid\" INTEGER, \"version\" INTEGER, \"changeset\" INTEGER, \"timestamp\" BIGINT)")
        database.execSQL("CREATE TABLE IF NOT EXISTS \"tag\" (\"id\" INTEGER NOT NULL , \"k\" TEXT NOT NULL , \"v\" TEXT NOT NULL , \"reftype\" INTEGER NOT NULL  DEFAULT -1, PRIMARY KEY( \"reftype\",\"k\" ,\"id\" )   )")
        database.execSQL("CREATE TABLE IF NOT EXISTS \"way_no\" (\"way_id\" INTEGER NOT NULL , \"node_id\" INTEGER NOT NULL, \"insert_id\" INTEGER NOT NULL,  PRIMARY KEY (\"way_id\", \"node_id\",\"insert_id\")  )  ")
        database.execSQL("CREATE TABLE IF NOT EXISTS \"ways\" (\"id\" INTEGER PRIMARY KEY  NOT NULL , \"changeset\" INTEGER, \"version\" INTEGER, \"user\" TEXT, \"uid\" INTEGER, \"timestamp\" BIGINT)")
    }

    override fun setIndex() {
        database.execSQL("CREATE INDEX tag_index ON tag (id, k, v)")
        database.execSQL("CREATE INDEX way_no_index ON way_no (way_id, node_id)")
        database.execSQL("CREATE INDEX nodes_index ON nodes (id, lat, lon)")
    }

    override fun checkCommit(){
        if (read > (commitCount*commitFrequency).toLong())
            commit()
    }

    override fun commit() {
        commitCount ++
        Log.i(TAG , "Pause read $read Start Transaction $insert")
        val list = arrayListOf<ArrayList<ContentValues>>(
            nodeValuesList,
            relationValuesList,
            wayValuesList,
            tagValuesList,
            wayNodeValuesList,
            memberValuesList)

        val names = arrayListOf<String>(
            "nodes",
            "relations",
            "ways",
            "tag",
            "way_no",
            "relation_members")

        database.beginTransaction()
        for ((index, childlist: ArrayList<ContentValues>)  in list.withIndex()){
            val table = names[index]
            for (values : ContentValues in childlist){
                insert++
                try{
                    database.insert(table, null, values)
                } catch (e : SQLiteConstraintException){
                    print(e)
                }
            }
            childlist.clear()
        }
        database.setTransactionSuccessful()
        database.endTransaction()
        Log.i(TAG , "Stop Transaction $insert continue read $read" )
    }

    private fun translate(type: EntityType): Int {
        return when (type) {
            EntityType.Bound -> OsmType.BOUND.ordinal
            EntityType.Node -> OsmType.NODE.ordinal
            EntityType.Relation -> OsmType.RELATION.ordinal
            EntityType.Way -> OsmType.WAY.ordinal
        }
    }

    private fun insertNode(id : Long, changeset: Long, version: Int, user:String, uid: Int, timestamp : Long, lat : Double, lon: Double ){
        try {
            val values = ContentValues()
            values.put("id", id)
            values.put("changeset", changeset)
            values.put("version", version)
            values.put("user",user)
            values.put("uid", uid)
            values.put("timestamp",timestamp )
            values.put("lat", lat)
            values.put("lon", lon)
            nodeValuesList.add(values)
            read++
            batchCount++
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun insertNode(node : Node){
        insertNode(node.id, node.changesetId,node.version, node.user.name,node.user.id,node.timestamp.time,node.latitude,node.longitude)
    }

    private fun insertTag(id: Long, k: String, v:String, reftype : Int){
        try {
            val values = ContentValues()
            values.put("id", id)
            values.put("k", k)
            values.put("v", v)
            values.put("reftype", reftype)
            tagValuesList.add(values)
            read++
            batchCount++
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun insertTag(entity : Entity, tag: Tag){
        insertTag(entity.id,tag.key,tag.value,translate(entity.type))
    }

    private fun insertWay(id : Long, changeset: Long, version: Int, user:String, uid: Int, timestamp : Long){
        try {
            val values = ContentValues()
            values.put("id", id)
            values.put("changeset", changeset)
            values.put("version", version)
            values.put("user", user)
            values.put("uid", uid)
            values.put("timestamp",timestamp)
            wayValuesList.add(values)
            read++
            batchCount++
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun insertWay(way: Way){
        insertWay(way.id, way.changesetId, way.version, way.user.name, way.user.id, way.timestamp.time)
    }

    private fun insertWayNode(way_id: Long, node_id:Long, insert_id:Long){
        try {
            val values = ContentValues()
            values.put("way_id", way_id)
            values.put("node_id", node_id)
            values.put("insert_id", insert_id)
            wayNodeValuesList.add(values)
            batchCount++
            read++
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun insertWayNode(way: Way, node:WayNode){
        insertWayNode(way.id, node.nodeId, read)
    }

    private fun insertRelation(id : Long, changeset: Long, version: Int, user:String, uid: Int, timestamp : Long){
        try {
            val values = ContentValues()
            values.put("id", id)
            values.put("changeset",changeset)
            values.put("version",version)
            values.put("user",user)
            values.put("uid",uid)
            values.put("timestamp",timestamp)
            relationValuesList.add(values)
            batchCount++
            read++
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun insertRelation(relation:Relation ){
        insertRelation(relation.id, relation.changesetId, relation.version, relation.user.name, relation.user.id, relation.timestamp.time)
    }

    private fun insertMember(relation_id: Long, type:String, member_id:Long, role:String, insert_id:Long){
        try {
            val insertRelationMember1 = ContentValues()
            insertRelationMember1.put("relation_id", relation_id)
            insertRelationMember1.put("type", type)
            insertRelationMember1.put("member_id",member_id)
            insertRelationMember1.put("role",role)
            insertRelationMember1.put("insert_id",insert_id)
            memberValuesList.add(insertRelationMember1)
            read++
            batchCount++
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun insertMember(relation: Relation, member: RelationMember){
        insertMember(relation.id, member.memberType.name, member.memberId, member.memberRole, read)
    }
}
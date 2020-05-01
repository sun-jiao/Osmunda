package moe.sunjiao.osmunda.writer

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import moe.sunjiao.osmunda.Osmunda
import moe.sunjiao.osmunda.model.OsmType
import org.openstreetmap.osmosis.core.domain.v0_6.*

class SimpleSQLWriter (context : Context, databaseName: String, private val commitFrequency : Int = 5000): OsmWriter {
    var read: Long = 0
    var insert : Long = 0
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
        database.execSQL("CREATE TABLE IF NOT EXISTS \"nodes\" (\"id\" INTEGER PRIMARY KEY  NOT NULL , \"lat\" DOUBLE NOT NULL , \"lon\" DOUBLE NOT NULL)")
        database.execSQL("CREATE TABLE IF NOT EXISTS \"relation_members\" (\"type\" TEXT NOT NULL , \"member_id\" INTEGER NOT NULL , \"role\" TEXT, \"relation_id\" INTEGER NOT NULL,\"insert_id\" INTEGER NOT NULL, PRIMARY KEY( \"relation_id\",\"member_id\",\"insert_id\" ))")
        database.execSQL("CREATE TABLE IF NOT EXISTS \"tag\" (\"id\" INTEGER NOT NULL , \"k\" TEXT NOT NULL , \"v\" TEXT NOT NULL , PRIMARY KEY( \"reftype\",\"k\" ,\"id\" )   )")
        database.execSQL("CREATE TABLE IF NOT EXISTS \"way_no\" (\"way_id\" INTEGER NOT NULL , \"node_id\" INTEGER NOT NULL, \"insert_id\" INTEGER NOT NULL,  PRIMARY KEY (\"way_id\", \"node_id\",\"insert_id\")  )  ")
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

    private fun insertNode(id : Long, lat : Double, lon: Double ){
        try {
            val values = ContentValues()
            values.put("id", id)
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
        insertNode(node.id, node.latitude, node.longitude)
    }

    private fun insertTag(id: Long, k: String, v:String){
        try {
            val values = ContentValues()
            values.put("id", id)
            values.put("k", k)
            values.put("v", v)
            tagValuesList.add(values)
            read++
            batchCount++
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun insertTag(entity : Entity, tag: Tag){
        insertTag(entity.id,tag.key,tag.value)
    }

    override fun insertWay(way: Way) {}

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

    override fun insertRelation(relation:Relation ){}

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
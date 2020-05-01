package moe.sunjiao.osmunda.writer

import android.content.ContentValues
import android.content.Context
import org.openstreetmap.osmosis.core.domain.v0_6.*

interface OsmWriter{
    abstract fun checkCommit()
    abstract fun commit()
    abstract fun insertNode(id : Long, changeset: Long ,version: Int, user:String, uid: Int,  timestamp : Long, lat : Double, lon: Double )
    abstract fun insertNode(node : Node)
    abstract fun insertTag(id: Long, k: String, v:String, reftype : Int)
    abstract fun insertTag(entity : Entity, tag: Tag)
    abstract fun insertWay(id : Long, changeset: Long ,version: Int, user:String, uid: Int,  timestamp : Long)
    abstract fun insertWay(way: Way)
    abstract fun insertWayNode(way_id: Long,node_id:Long, insert_id:Long)
    abstract fun insertWayNode(way: Way, node: WayNode)
    abstract fun insertRelation(id : Long, changeset: Long ,version: Int, user:String, uid: Int,  timestamp : Long)
    abstract fun insertRelation(relation: Relation)
    abstract fun insertMember(relation_id: Long, type:String, member_id:Long, role:String, insert_id:Long)
    abstract fun insertMember(relation: Relation, member: RelationMember)
}
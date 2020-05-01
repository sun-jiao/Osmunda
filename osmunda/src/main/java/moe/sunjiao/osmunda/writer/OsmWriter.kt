package moe.sunjiao.osmunda.writer

import android.content.ContentValues
import android.content.Context
import org.openstreetmap.osmosis.core.domain.v0_6.*

interface OsmWriter{
    fun checkCommit()
    fun commit()
    fun insertNode(node : Node)
    fun insertTag(entity : Entity, tag: Tag)
    fun insertWay(way: Way)
    fun insertWayNode(way: Way, node: WayNode)
    fun insertRelation(relation: Relation)
    fun insertMember(relation: Relation, member: RelationMember)
}
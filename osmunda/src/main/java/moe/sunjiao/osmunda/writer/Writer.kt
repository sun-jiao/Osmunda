package moe.sunjiao.osmunda.writer

import org.openstreetmap.osmosis.core.domain.v0_6.*

interface Writer{
    val read : Long
    val insert : Long
    fun checkCommit()
    fun commit()
    fun insertNode(node : Node)
    fun insertTag(entity : Entity, tag: Tag)
    fun insertWay(way: Way)
    fun insertWayNode(way: Way, node: WayNode)
    fun insertRelation(relation: Relation)
    fun insertMember(relation: Relation, member: RelationMember)
}
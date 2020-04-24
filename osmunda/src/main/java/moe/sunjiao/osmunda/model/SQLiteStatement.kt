package moe.sunjiao.osmunda.model

class SQLiteStatement (
    val city : String,
    val county : String,
    val town : String,
    val suburb : String
){
    init {

    }

    constructor():this("","","","")

}
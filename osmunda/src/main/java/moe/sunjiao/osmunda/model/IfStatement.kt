package moe.sunjiao.osmunda.model

class IfStatement (
    val state : (key: String, value: String) -> Boolean,
    val city : (key: String, value: String) -> Boolean,
    val county : (key: String, value: String) -> Boolean,
    val town : (key: String, value: String) -> Boolean,
    val suburb : (key: String, value: String) -> Boolean
)
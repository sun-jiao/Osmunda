package moe.sunjiao.osmunda.model

import java.util.*

class IfStatement (
    _city            : (current: String, key: String, value: String) -> Boolean,
    _county          : (current: String, key: String, value: String) -> Boolean,
    _town            : (current: String, key: String, value: String) -> Boolean,
    _neighbourhood   : (current: String, key: String, value: String) -> Boolean
){

    var city            : (current: String, key: String, value: String) -> Boolean = {_, _, _ -> true}
        private set(value) {
            field = value
        }
    var county          : (current: String, key: String, value: String) -> Boolean = {_, _, _ -> true}
        private set(value) {
            field = value
        }
    var town            : (current: String, key: String, value: String) -> Boolean = {_, _, _ -> true}
        private set(value) {
            field = value
        }
    var neighbourhood   : (current: String, key: String, value: String) -> Boolean = {_, _, _ -> true}
        private set(value) {
            field = value
        }

    init{
        city = _city
        county = _county
        town = _town
        neighbourhood = _neighbourhood
    }

    constructor() : this(
        { _, key, _ -> key == "city" || key.endsWith(":city") },
        { _, key, _ -> key == "county_name" || key == "district_name" || key.endsWith("county") || key.endsWith("district")},
        { _,key,_ -> key.endsWith("town")},
        { current, key, value -> "operator" == key || (current == "" && (key.endsWith("neighbourhood") || (key.endsWith("village") )))}
    )



    constructor(locale: Locale?): this(){
        when (locale) {
            Locale.SIMPLIFIED_CHINESE, Locale.CHINA, Locale.CHINESE, Locale.PRC -> this(
                { _, key, value -> key.startsWith("addr:city") && value.endsWith("市") },
                { _, key, value -> key == "county_name" || key == "district_name" || (key.startsWith("addr:") && (value.endsWith("县") || value.endsWith("区")))},
                { _, key, value -> key.startsWith("addr:") && (value.endsWith("乡") || value.endsWith("镇") || value.endsWith("街道") || value.endsWith("街道办事处")) },
                { current, key, value -> "operator" == key || (current == "" && (key.endsWith("neighbourhood") || key.endsWith("village") || value.endsWith("村")))}
            )
        }
    }

    private operator fun invoke(
        _city: (String, String, String) -> Boolean,
        _county: (String, String, String) -> Boolean,
        _town: (String, String, String) -> Boolean,
        _neighbourhood: (String, String, String) -> Boolean
    ) {
        city = _city
        county = _county
        town = _town
        neighbourhood = _neighbourhood
    }
}
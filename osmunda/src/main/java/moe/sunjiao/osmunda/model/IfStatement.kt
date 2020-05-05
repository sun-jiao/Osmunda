package moe.sunjiao.osmunda.model

import java.util.*

/**
 * if statement in different language to fix issue in osm raw data
 * created on 4/24/2020.
 *
 * @author Sun Jiao(孙娇）
 */

class IfStatement (){

    var city            : (current: String, key: String, value: String) -> Boolean = { _, key, _ -> key == "city" || key.endsWith(":city") }
        private set
    var county          : (current: String, key: String, value: String) -> Boolean = { _, key, _ -> key == "county_name" || key == "district_name" || key.endsWith("county") || key.endsWith("district")}
        private set
    var town            : (current: String, key: String, value: String) -> Boolean = { _,key,_ -> key.endsWith("town")}
        private set
    var neighbourhood   : (current: String, key: String, value: String) -> Boolean = { current, key, value -> "operator" == key || (current == "" && (key.endsWith("neighbourhood") || (key.endsWith("village") )))}
        private set

    init{ }

    /**
     * @param locale country or area or language of address
     */
    constructor(locale: Locale?): this(){
        when (locale) {
            Locale.SIMPLIFIED_CHINESE, Locale.CHINA, Locale.CHINESE, Locale.PRC -> this(
                { _, key, value -> key == "addr:city" && value.endsWith("市") },
                { _, key, value -> key == "county_name" || key == "district_name" || ((key.startsWith("addr:") && (value.endsWith("县")) || value.endsWith("区")))},
                { _, key, value -> key.startsWith("addr:") && ((value.endsWith("乡") || value.endsWith("镇") || value.endsWith("街道") || value.endsWith("街道办事处"))) },
                { current, key, value -> "operator" == key || (current == "" && (key.endsWith("neighbourhood") || key.endsWith("village") || (key.startsWith("addr:") && (value.endsWith("村") || value.endsWith("庄") ))))}
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
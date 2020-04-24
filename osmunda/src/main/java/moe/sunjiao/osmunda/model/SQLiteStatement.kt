package moe.sunjiao.osmunda.model

import java.util.*

class SQLiteStatement (){
    var city : String = " k like '%city' "
        private set
    var county_way : String = " k like '%county' or k like '%district' "
        private set
    var county_node : String = " k = 'place' and ( v = 'county' or v = 'district' ) "
        private set
    var town_way : String = " k like '%town' "
        private set
    var town_node : String = " (k = 'place' and v = 'town') "
        private set
    var neighbourhood_way : String = " k like '%neighbourhood' or k like '%village' or k = 'operator' "
        private set
    var neighbourhood_node : String = " (k = 'place' and (v = 'neighbourhood' or v = 'village')) "
        private set

    init { }

    constructor(locale: Locale?) : this(){
        when (locale) {
            Locale.SIMPLIFIED_CHINESE, Locale.CHINA, Locale.CHINESE, Locale.PRC -> this(
                " k like '%city' and v like '%市' ",
                " k like '%county' or k like '%district' or ( k like 'addr:%' and ( v like '%县' or v like '%区' )) ",
                " ( k = 'place' and ( v = 'county' or v = 'district' )) or ( k = 'china_class' and ( v = 'xian' )) or ( k = 'name' and ( v like '%县' or v like '%区' )) ",
                " k like '%town' or ( k like 'addr:%' and ( v like '%乡' or v like '%镇' or v like '%街道' or v like '%街道办事处' )) ",
                " (k = 'place' and v = 'town') or (k = 'china_class' and ( v = 'zhen' or v = 'xiang' or v = 'jiedao'))  or ( k = 'name' and ( v like '%乡' or v like '%镇' or v like '%街道' or v like '%街道办事处'  )) ",
                " k like '%neighbourhood' or k like '%village' or k = 'operator' ",
                " ((k = 'place' or k = 'china_class') and (v = 'neighbourhood' or v = 'village')) or ( k = 'name' and ( v like '%村' or v like '%庄' )) "
            )
        }
    }

    private operator fun invoke(
        _city : String,
        _county_way : String,
        _county_node : String,
        _town_way : String,
        _town_node : String,
        _neighbourhood_way : String,
        _neighbourhood_node : String
    ) {
        city = _city
        county_way = _county_way
        county_node = _county_node
        town_way = _town_way
        town_node = _town_node
        neighbourhood_way = _neighbourhood_way
        neighbourhood_node = _neighbourhood_node
    }
}
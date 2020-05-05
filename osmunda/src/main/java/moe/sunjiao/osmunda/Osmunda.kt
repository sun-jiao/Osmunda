package moe.sunjiao.osmunda

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.io.File
import java.util.*

/**
 * get database, dir and list
 * base class of osmunda
 * created on 4/22/2020.
 *
 * @author Sun Jiao(孙娇）
 *
 * @param context android context where this fun is called
 */

class Osmunda(private val context: Context) {

    fun getDatabaseList() : Array<File>?{
        return getDatabaseDir()
            .listFiles { dir, name -> name.toLowerCase(Locale.ROOT).endsWith("-osmunda.sqlite") }
    }

    /**
     * @param name name of database
     */
    fun getDatabaseByName(name : String): SQLiteDatabase{
        return context.openOrCreateDatabase(getDatabaseDir().absolutePath + "/" + name + ".sqlite", 0, null)
    }

    fun getDatabaseDir(): File{
        return File(context.filesDir.absolutePath.replace("files", "databases"))
    }

}
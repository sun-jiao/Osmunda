package moe.sunjiao.osmunda

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.io.File
import java.util.*

class Osmunda(private val context: Context) {

    fun getDatabaseList() : Array<File>?{
        return getDatabaseDir()
            .listFiles { dir, name -> name.toLowerCase(Locale.ROOT).endsWith("-osmunda.sqlite") }
    }

    fun getDatabaseByName(name : String): SQLiteDatabase{
        return context.openOrCreateDatabase(getDatabaseDir().absolutePath + "/" + name + ".sqlite", 0, null)
    }

    fun getDatabaseDir(): File{
        return File(context.filesDir.absolutePath.replace("files", "databases"))
    }

}
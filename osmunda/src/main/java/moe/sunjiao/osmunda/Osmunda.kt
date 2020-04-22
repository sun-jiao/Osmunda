package moe.sunjiao.osmunda

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.io.File
import java.util.*

class Osmunda(val context: Context) {

    fun getDatabaseList() : Array<File>?{
        return getOsmundaDir()
            .listFiles { dir, name -> name.toLowerCase(Locale.ROOT).endsWith(".sqlite") }
    }

    fun getDatabaseByName(name : String): SQLiteDatabase{
        return context.openOrCreateDatabase(getOsmundaDir().absolutePath + "/" + name + "sqlite", 0, null)
    }

    fun getOsmundaDir(): File{
        return File(context.filesDir.absolutePath + "/osmunda")
    }

}
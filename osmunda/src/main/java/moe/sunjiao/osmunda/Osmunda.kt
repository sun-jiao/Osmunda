package moe.sunjiao.osmunda

import android.content.Context
import java.io.File
import java.io.FilenameFilter
import java.util.*
import kotlin.collections.ArrayList

class Osmunda {
    fun getDatabaseList(context: Context) : Array<File>?{
        val path = File(context.filesDir.absolutePath.replace("files", "osmunda"))
        return path.listFiles { dir, name -> name.toLowerCase(Locale.ROOT).endsWith(".sqlite") }
    }
}
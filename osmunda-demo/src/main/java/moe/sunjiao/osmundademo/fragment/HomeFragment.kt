package moe.sunjiao.osmundademo.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_home.*
import moe.sunjiao.osmunda.model.ImportOption
import moe.sunjiao.osmunda.reader.OsmosisReader
import moe.sunjiao.osmunda.reader.Reader
import moe.sunjiao.osmunda.model.WriterType
import moe.sunjiao.osmundademo.R
import java.util.*


class HomeFragment : Fragment() {
    private lateinit var file: Uri
    val reader : Reader = OsmosisReader()
    val TAG = "Home"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reader.options.add(ImportOption.INCLUDE_RELATIONS)
        reader.options.add(ImportOption.INCLUDE_WAYS)
        reader.writerType = WriterType.SIMPLE_SQL_WRITER

        select_button.setOnClickListener{
            val intent = Intent()
            intent.type = "*/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Osm Source File"), 100)
        }

        val thread = Thread(Runnable {
            val databaseName = database_name.text.toString()
            if (this::file.isInitialized && databaseName != ""){
                reader.readData(file, requireContext(), databaseName)
                val task: TimerTask = object : TimerTask() {
                    override fun run() {
                        val message = handler.obtainMessage()
                        message.obj = arrayOf(reader.progress, import_progress)
                        message.what = 0
                        handler.sendMessage(message)
                    }
                }
                val timer = Timer(true)

                timer.schedule(task, 0, 500)
            } else {
                Toast.makeText(requireContext(), "select a file and set its name!", LENGTH_LONG).show()
            }
        })

        import_button.setOnClickListener {
           thread.start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null){
            file = data.data!!
            import_text.text = file.path
        } else {
            Toast.makeText(requireContext(), "file incorrect", LENGTH_LONG).show()
        }
    }

    private val handler = object : Handler(Looper.getMainLooper()) {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0-> {
                    if (msg.obj is Array<*>){
                        val array : Array<Any> = msg.obj as Array<Any>
                        if (array[0] is Double && array[1] is ProgressBar)
                            (array[1] as ProgressBar).progress = (array[0] as Double).toInt()
                    }
                }
                else -> {
                }
            }
        }
    }
}

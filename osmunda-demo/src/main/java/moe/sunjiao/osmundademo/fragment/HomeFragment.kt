package moe.sunjiao.osmundademo.fragment

import android.app.Activity.MODE_PRIVATE
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Toast.LENGTH_SHORT
import androidx.core.view.get
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import moe.sunjiao.osmunda.Osmunda
import moe.sunjiao.osmunda.model.ImportOption
import moe.sunjiao.osmunda.model.WriterType
import moe.sunjiao.osmunda.reader.OsmosisReader
import moe.sunjiao.osmunda.reader.Reader
import moe.sunjiao.osmundademo.MainActivity
import moe.sunjiao.osmundademo.R
import java.util.*


class HomeFragment : Fragment() {
    private lateinit var uri: Uri
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

        val stringList : MutableList<String> = mutableListOf()

        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_list_item_1,  stringList)

        sql_list.adapter = adapter
        sql_list.onItemClickListener = AdapterView.OnItemClickListener{ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            val selected = adapterView.getItemAtPosition(i).toString()
            requireContext().getSharedPreferences("database", Context.MODE_PRIVATE).edit().putString("current", selected).apply()
            requireActivity().current_database.text = String.format(getString(R.string.current_db), selected)
        }

        val fileList = Osmunda(requireContext()).getDatabaseList()

        if (fileList != null) {
            for (file in fileList){
                adapter.add(file.name.split(".")[0])
            }
        }

        select_button.setOnClickListener{
            val intent = Intent()
            intent.type = "*/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Osm Source File"), 100)
        }

        val handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0-> {
                        if (msg.obj is Array<*>){
                            @Suppress("UNCHECKED_CAST")
                            val array : Array<Any> = msg.obj as Array<Any>
                            if (array[0] is Double && array[1] is ProgressBar){
                                (array[1] as ProgressBar).progress = ((array[0] as Double) * 100).toInt()
                                if (array[0] == 1.0 && array[2] is Timer)
                                    (array[2] as Timer).cancel()
                                    adapter.add("")
                            }
                        }
                    }
                    else -> {
                    }
                }
            }
        }

        import_button.setOnClickListener {
            Thread(Runnable {
                val databaseName = database_name.text.toString()
                if (this::uri.isInitialized && databaseName != ""){
                    val timer = Timer(true)
                    val reader : Reader = OsmosisReader()
                    reader.options.add(ImportOption.INCLUDE_RELATIONS)
                    reader.options.add(ImportOption.INCLUDE_WAYS)
                    reader.writerType = WriterType.SIMPLE_SQL_WRITER
                    val task: TimerTask = object : TimerTask() {
                        override fun run() {
                            val message = handler.obtainMessage()
                            Log.i(TAG, reader.progress.toString())
                            message.obj = arrayOf(reader.progress, import_progress, timer, databaseName)
                            message.what = 0
                            handler.sendMessage(message)
                        }
                    }
                    timer.schedule(task, 0, 500)
                    try {
                        reader.readData(uri, requireContext(), databaseName)
                    } catch (e : Exception){
                        if (e is IllegalArgumentException)
                            Toast.makeText(requireContext(), "file incorrect", LENGTH_SHORT).show()
                        else
                            e.printStackTrace()
                    }
                } else {
                    Toast.makeText(requireContext(), "select a file and set its name!", LENGTH_SHORT).show()
                }
            }).start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null){
            uri = data.data!!
            import_text.text = uri.path
        } else {
            Toast.makeText(requireContext(), "no available file selected.", LENGTH_SHORT).show()
        }
    }
}

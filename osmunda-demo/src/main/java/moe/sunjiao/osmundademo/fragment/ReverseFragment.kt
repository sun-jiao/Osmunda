package moe.sunjiao.osmundademo.fragment

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_reverse.*
import moe.sunjiao.osmunda.Osmunda
import moe.sunjiao.osmunda.geocoder.ReverseGeocoder
import moe.sunjiao.osmunda.model.SearchResult
import moe.sunjiao.osmundademo.R

class ReverseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reverse,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val TAG = "ForwardFragment"

        val stringList : MutableList<String> = mutableListOf()

        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_list_item_1,  stringList)

        reverse_result_list.adapter = adapter

        val handler : Handler = object : Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message){
                adapter.add(msg.obj as String)
            }
        }

        val thread = Thread(Runnable {
            Log.i(TAG, "start search")
            val lat = latitude_text.text.toString()
            val lon = longitude_text.text.toString()
            val database: SQLiteDatabase = getDatabase()
            val list: List<SearchResult> = ReverseGeocoder(database).search(lat, lon, 10, 0)
            Log.i(TAG, "complete")
            for (result in list) {
                val address = result.toAddress()
                Log.i(TAG, result.name + "  " + result.databaseId + "  " + address.fullAddress)

                val message = handler.obtainMessage()
                message.obj = address.fullAddress
                handler.sendMessage(message)
            }
        })

        reverse_geocode_button.setOnClickListener { thread.start() }
    }

    private fun getDatabase() : SQLiteDatabase {
        return Osmunda(requireContext()).getDatabaseByName("hubei")
    }
}

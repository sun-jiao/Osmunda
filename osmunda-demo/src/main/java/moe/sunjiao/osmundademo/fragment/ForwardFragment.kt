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
import android.widget.EditText
import android.widget.ListView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_forward.*
import kotlinx.android.synthetic.main.fragment_forward.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import moe.sunjiao.osmunda.geocoder.Geocoder
import moe.sunjiao.osmunda.model.SearchResult
import moe.sunjiao.osmundademo.MainActivity
import moe.sunjiao.osmundademo.R

class ForwardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val thisView: View = inflater.inflate(R.layout.fragment_home,container,false)

        val TAG = "ForwardFragment"

        val stringList : List<String> = listOf()

        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_list_item_1,  stringList)

        val resultList = thisView.findViewById<ListView>(R.id.result_list)
        resultList.adapter = adapter

        val handler : Handler = object : Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message){
                adapter.add(msg.obj as String)
            }
        }

        val placeName = thisView.findViewById<EditText>(R.id.place_name)

        geocode_button.setOnClickListener {
            val keyWord = placeName.text.toString()
            val hubeiDatabase: SQLiteDatabase = MainActivity::getDatabase.call(arguments)
            val list: List<SearchResult> = Geocoder(hubeiDatabase).search(keyWord, 100, 0, 30.7324, 114.6589, 30.3183, 114.0588)
            for (result in list) {
                val address = result.toAddress()

                val message = handler.obtainMessage()
                message.obj = address.fullAddress
                handler.sendMessage(message)
            }
        }

        return thisView
    }
}

package moe.sunjiao.osmundademo

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import moe.sunjiao.osmundademo.fragment.ForwardFragment
import moe.sunjiao.osmundademo.fragment.HomeFragment
import moe.sunjiao.osmundademo.fragment.ReverseFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        fragment_view_pager.currentItem = 1
        title = getString(R.string.title_home)
        val currentDb = getSharedPreferences("database", Context.MODE_PRIVATE).getString("current", "not specified")
        current_database.text = String.format(getString(R.string.current_db), currentDb)
    }

    private fun initView() {
        val fragmentList : ArrayList<Fragment> = ArrayList()
        fragmentList.add(ForwardFragment())
        fragmentList.add(HomeFragment())
        fragmentList.add(ReverseFragment())
        val myFragmentStatePagerAdapter = MyFragmentStatePagerAdapter(supportFragmentManager, fragmentList)
        fragment_view_pager.adapter = myFragmentStatePagerAdapter

        nav_view.setOnNavigationItemSelectedListener(object:
            BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item:MenuItem):Boolean {

                when (item.itemId) {
                    R.id.navigation_forward -> {
                        fragment_view_pager.currentItem = 0
                        title = getString(R.string.title_forward)
                        return true
                    }
                    R.id.navigation_home -> {
                        fragment_view_pager.currentItem = 1
                        title = getString(R.string.title_home)
                        return true
                    }
                    R.id.navigation_reverse -> {
                        fragment_view_pager.currentItem = 2
                        title = getString(R.string.title_reverse)
                        return true
                    }
                    else -> {}
                }
                return false
            }
        })

        fragment_view_pager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position:Int, positionOffset:Float, positionOffsetPixels:Int) {

            }
            override fun onPageSelected(position:Int) {
                nav_view.menu.getItem(position).isChecked = true
                when(position){
                    0 ->
                        title = getString(R.string.title_forward)
                    1 ->
                        title = getString(R.string.title_home)
                    2 ->
                        title = getString(R.string.title_reverse)
                }
            }
            override fun onPageScrollStateChanged(state:Int) {

            }
        })
    }
}

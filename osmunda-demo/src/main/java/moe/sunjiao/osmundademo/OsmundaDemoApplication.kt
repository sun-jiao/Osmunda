package moe.sunjiao.osmundademo

import android.app.Application
import io.realm.Realm

class OsmundaDemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize Realm. Should only be done once when the application starts.
        Realm.init(this)
    }
}

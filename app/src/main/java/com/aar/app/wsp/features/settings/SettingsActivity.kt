package com.aar.app.wsp.features.settings

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.aar.app.wsp.R
/**
 * Created by abdularis on 21/07/17.
 */
class SettingsActivity : AppCompatActivity() {
    // Former kotlin-android-extensions synthetic views (plugin removed in Kotlin 2).
    private val back by lazy(LazyThreadSafetyMode.NONE) { findViewById<android.widget.ImageView>(R.id.back) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()

        back.setOnClickListener {
            onBackPressed()
        }
        supportActionBar!!.hide()
    }

    open class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.prefs_main, rootKey)
        }

    }
}

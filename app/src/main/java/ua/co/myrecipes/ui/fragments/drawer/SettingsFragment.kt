package ua.co.myrecipes.ui.fragments.drawer

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import ua.co.myrecipes.BuildConfig
import ua.co.myrecipes.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val feedback: Preference? = findPreference("feedback")
        feedback?.intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:serhiooreh@gmail.com"))
        feedback?.setOnPreferenceClickListener {
            it?.intent?.apply {
                this.putExtra(
                    Intent.EXTRA_TEXT,
                    "Device Name:${android.os.Build.MODEL}\n" +
                            "Device OS Version:${android.os.Build.VERSION.SDK_INT}\n" +
                            "App Name:${getString(R.string.app_name)}\n" +
                            "App Version:${BuildConfig.VERSION_NAME}"
                )
                startActivity(this) }
            true
        }

        val app: Preference? = findPreference("app")
        app?.intent = Intent(Intent.ACTION_VIEW)
        app?.setOnPreferenceClickListener {
            it?.intent?.apply {
                try {
                    this.data = Uri.parse("market://details?id=ua.silpo.android")               //later change
                    startActivity(this)
                } catch (exception: ActivityNotFoundException) {
                    this.data = Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
                    startActivity(this)
                } }
            true
        }
    }
}
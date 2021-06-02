package ua.co.myrecipes.ui.fragments.drawer

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import ua.co.myrecipes.BuildConfig
import ua.co.myrecipes.R
import ua.co.myrecipes.util.Constants
import ua.co.myrecipes.util.LocaleHelper

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onResume() {
        super.onResume()
        activity?.title = resources.getString(R.string.settings)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val language: ListPreference? = findPreference("language")
        language?.setDefaultValue(LocaleHelper.getSystemLang())
        language?.setOnPreferenceChangeListener { _, _ ->
            activity?.recreate()
            true
        }

        val theme: SwitchPreferenceCompat? = findPreference(Constants.FIELD_THEME)
        theme?.setOnPreferenceChangeListener { _, _ ->
            activity?.recreate()
            true
        }

        val feedback: Preference? = findPreference(Constants.FIELD_FEEDBACK)
        feedback?.intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:sergiooreh@ukr.net"))
        feedback?.setOnPreferenceClickListener {
            it?.intent?.apply {
                this.putExtra(
                    Intent.EXTRA_TEXT,
                    "Device Name:${Build.MODEL}\n" +
                            "Device OS Version:${Build.VERSION.SDK_INT}\n" +
                            "App Name:${getString(R.string.app_name)}\n" +
                            "App Version:${BuildConfig.VERSION_NAME}"
                )
                startActivity(this) }
            true
        }

        val app: Preference? = findPreference(Constants.FIELD_APP)
        app?.intent = Intent(Intent.ACTION_VIEW)
        app?.setOnPreferenceClickListener {
            it?.intent?.apply {
                try {
                    this.data = Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
                    startActivity(this)
                } catch (exception: ActivityNotFoundException) {
                    this.data = Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
                    startActivity(this)
                } }
            true
        }
    }
}
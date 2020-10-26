package ua.co.myrecipes.ui.fragments.drawer

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_about_us.*
import ua.co.myrecipes.BuildConfig
import ua.co.myrecipes.R

class AboutUsFragment : Fragment(R.layout.fragment_about_us){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        app_version_tv.text = "${getString(R.string.app_version)}  ${BuildConfig.VERSION_NAME}"
    }

    override fun onResume() {
        super.onResume()
        activity?.title = resources.getString(R.string.about_us)
    }
}

package ua.co.myrecipes.ui.fragments.drawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import ua.co.myrecipes.BuildConfig
import ua.co.myrecipes.R
import ua.co.myrecipes.databinding.FragmentAboutUsBinding
import ua.co.myrecipes.ui.fragments.BaseFragment

class AboutUsFragment : BaseFragment<FragmentAboutUsBinding>(){
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentAboutUsBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appVersionTv.text = getString(R.string.app_version_string, getString(R.string.app_version), BuildConfig.VERSION_NAME)
    }

    override fun onResume() {
        super.onResume()
        activity?.title = resources.getString(R.string.about_us)
    }
}

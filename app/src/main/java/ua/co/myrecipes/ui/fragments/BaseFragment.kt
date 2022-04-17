package ua.co.myrecipes.ui.fragments

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import ua.co.myrecipes.ui.MainActivity


abstract class BaseFragment<out T : ViewBinding>: Fragment() {
    private var _binding: ViewBinding? = null
    @Suppress("UNCHECKED_CAST")
    protected val binding: T
        get() = _binding as T

    private var uri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = bindingInflater(inflater)
        return _binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected abstract val bindingInflater: (LayoutInflater) -> ViewBinding


    fun showSnackBar(textResource: Int = 0, text: String = ""){
        if (textResource != 0){
            Snackbar.make((requireActivity() as MainActivity).binding.drawerLayout, textResource, Snackbar.LENGTH_LONG).show()
            return
        } else Snackbar.make((requireActivity() as MainActivity).binding.drawerLayout, text, Snackbar.LENGTH_LONG).show()
    }

    fun showToast(textResource: Int = 0, text: String = ""){
        if (textResource != 0) {
            Toast.makeText(requireContext(), textResource, Toast.LENGTH_SHORT).show()
            return
        }
        else Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    fun displayProgressBar(progressBar: ProgressBar,isDisplayed: Boolean = true){
        progressBar.visibility = if(isDisplayed) View.VISIBLE else View.GONE
    }

    private fun isPermissionGranted(permission: String) =
        ActivityCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
}
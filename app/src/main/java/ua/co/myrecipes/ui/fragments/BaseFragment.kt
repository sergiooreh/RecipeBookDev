package ua.co.myrecipes.ui.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AppSettingsDialog
import ua.co.myrecipes.R

abstract class BaseFragment(layoutId: Int): Fragment(layoutId) {

    lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>
     val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>(){
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setAspectRatio(500,500)
                .setFixAspectRatio(true)
                .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }

    val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        granted?.let {
            if (granted){
                cropActivityResultLauncher.launch(null)
            } else {
                AppSettingsDialog.Builder(this)
                    .setTitle(getString(R.string.permissions_required))
                    .setRationale(getString(R.string.you_have_to_accept_permission_to_load_image))
                    .build().show()
            }
        }
    }

    fun showSnackBar(textResource: Int = 0, text: String = ""){
        if (textResource != 0){
            Snackbar.make(requireActivity().drawerLayout, textResource, Snackbar.LENGTH_LONG).show()
            return
        } else Snackbar.make(requireActivity().drawerLayout, text, Snackbar.LENGTH_LONG).show()
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
}
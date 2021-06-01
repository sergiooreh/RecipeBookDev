package ua.co.myrecipes.ui.fragments

import android.Manifest
import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

abstract class BaseFragment(layoutId: Int): Fragment(layoutId) {

    lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

     val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>(){
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setAspectRatio(500,500)
                .setFixAspectRatio(true)
                .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? = CropImage.getActivityResult(intent)?.uri
    }

    val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()){
        lifecycleScope.launch {
            val isSavedSuccessfully = savePhotoToInternalStorage(UUID.randomUUID().toString(), it)                 //Internal storage
            if (isSavedSuccessfully){

            }
        }
    }

    private fun savePhotoToInternalStorage(filename: String, bmp: Bitmap): Boolean {
        return try {
            activity?.openFileOutput("$filename.jpg", MODE_PRIVATE).use { stream ->
                if(!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                    throw IOException("Couldn't save bitmap.")
                }
            }
            true
        } catch(e: IOException) {
            e.printStackTrace()
            false
        }
    }


    /*PERMISSIONS*/
    val requestReadExternalPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        granted?.let {
            /*if (granted){
                cropActivityResultLauncher.launch(null)
            } else {
                AppSettingsDialog.Builder(this)
                    .setTitle(getString(R.string.permissions_required))
                    .setRationale(getString(R.string.you_have_to_accept_permission_to_load_image))
                    .build().show()
            }*/
        }
    }
    val requestCameraPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->
        if (permissions[CAMERA] == false) true
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

    fun isPermissionGranted(permission: String) =
        ActivityCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
}
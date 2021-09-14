package ua.co.myrecipes.ui.fragments

import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.theartofdev.edmodo.cropper.CropImage
import pub.devrel.easypermissions.AppSettingsDialog
import ua.co.myrecipes.BuildConfig
import ua.co.myrecipes.R
import ua.co.myrecipes.ui.MainActivity
import java.io.File


abstract class BaseFragment<out T : ViewBinding>: Fragment() {
    private var _binding: ViewBinding? = null
    @Suppress("UNCHECKED_CAST")
    protected val binding: T
        get() = _binding as T

    private var uri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = bindingInflater(inflater)
        return _binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected abstract val bindingInflater: (LayoutInflater) -> ViewBinding

    lateinit var cropActivityResultLauncher: ActivityResultLauncher<Uri?>
    val cropActivityResultContract = object : ActivityResultContract<Uri?, Uri?>(){
        override fun createIntent(context: Context, input: Uri?): Intent =
            CropImage.activity(input)
                .setAspectRatio(500,500)
                .setFixAspectRatio(true)
                .getIntent(requireContext())

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? = CropImage.getActivityResult(intent)?.uri
    }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { cropActivityResultLauncher.launch(uri) }
    }

    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicture()){ successful ->
        if (successful) cropActivityResultLauncher.launch(uri)
    }

    fun openImageSource(){
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.choose_resource)
            /*.setPositiveButton(R.string.camera) { _, _ ->
                if (isPermissionGranted(CAMERA)){
                    createFileAndGetItsUri()
                    takePhoto.launch(uri)
                } else {
                    requestCameraPermissions.launch(arrayOf(CAMERA))
                }
            }*/
            .setNegativeButton(R.string.gallery) { _, _ ->
                getContent.launch("image/*")
            }
            .show()
    }

    private fun createFileAndGetItsUri() {
        val file = File(activity?.filesDir, "picFromCamera")
        uri = FileProvider.getUriForFile(
            requireContext(),
            BuildConfig.APPLICATION_ID + ".provider",
            file
        )
        uri
    }


    /*PERMISSIONS*/
    private val requestCameraPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->
        if (permissions[CAMERA] == true){
            createFileAndGetItsUri()
            takePhoto.launch(uri)
        } else {
            AppSettingsDialog.Builder(this).build().show()
        }
    }


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
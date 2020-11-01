package ua.co.myrecipes.ui.fragments

import android.content.Intent
import android.graphics.Canvas
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import com.google.android.material.snackbar.Snackbar
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import ua.co.myrecipes.R
import ua.co.myrecipes.adapters.BaseAdapter
import ua.co.myrecipes.util.Constants
import ua.co.myrecipes.util.Permissions

const val REQUEST_CODE_EXTERNAL_STORAGE = 1

abstract class BaseFragment(layoutId: Int): Fragment(layoutId), EasyPermissions. PermissionCallbacks {

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

    fun <T>setupRecycleView(recycleView: RecyclerView, myAdapter: BaseAdapter<T>, mode: Int = -1, list: MutableList<T> = mutableListOf()){
        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {            //direction of swiping

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.layoutPosition
                val item = myAdapter.items[position]
                list.removeAt(position)
                myAdapter.notifyItemRemoved(position)
                Snackbar.make(requireView(),"Successfully deleted", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        list.add(item)
                        myAdapter.notifyItemInserted(position)
                    }
                    show()
                }
                myAdapter.notifyDataSetChanged()
            }
        }

        recycleView.apply {
            adapter = myAdapter
            when(mode){
                0 -> { addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                    ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this)
                }
                1 -> { overScrollMode = View.OVER_SCROLL_NEVER }
                2 ->{
                    layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
                    return
                }
            }
            layoutManager = LinearLayoutManager(activity)
        }
    }

    fun displayProgressBar(progressBar: ProgressBar,isDisplayed: Boolean = false){
        progressBar.visibility = if(isDisplayed) View.VISIBLE else View.GONE
    }


    /*PERMISSIONS REQUEST*/

    fun launchImageCrop(uri: Uri) {
        CropImage.activity(uri)
            .setAspectRatio(500,500)
            .setFixAspectRatio(true)
            .start(requireContext(), this)
    }

    fun openGalleryForImage() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivityForResult(this, Constants.REQUEST_CODE)
        }
    }

    fun requestPermissions(){
        if (Permissions.hasStoragePermissions(requireContext())){ return }
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.you_have_to_accept_permission_to_load_image),
            REQUEST_CODE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) { openGalleryForImage() }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        } else{
            requestPermissions()
            return
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}
package ua.co.myrecipes.ui.dialogs.addDirections

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import kotlinx.android.synthetic.main.dialog_add_direction_item.*
import kotlinx.android.synthetic.main.dialog_add_ingridient_item.*
import kotlinx.android.synthetic.main.dialog_add_ingridient_item.tvCancel
import kotlinx.android.synthetic.main.fragment_new_recipe_direc.*
import ua.co.myrecipes.R
import ua.co.myrecipes.ui.dialogs.AddDialogListenerDir
import ua.co.myrecipes.ui.dialogs.AddDialogListenerIngr

class AddDirectionsDialog(
    context: Context,
    private var addDialogListenerDir: AddDialogListenerDir
): AppCompatDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_add_direction_item)

        this.setTitle("New direction")
        add_ingr_tv.setOnClickListener {
            val direction = direction_edt.text.toString()

            if (direction.isEmpty()){
                Toast.makeText(context,"Please, enter the direction", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            addDialogListenerDir.onAddButtonClick(direction)
            dismiss()
        }

        tvCancel.setOnClickListener {
            cancel()
        }
    }

}
package ua.co.myrecipes.ui.dialogs.addDirections

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import kotlinx.android.synthetic.main.dialog_add_direction_item.*
import kotlinx.android.synthetic.main.dialog_add_ingridient_item.tvCancel
import ua.co.myrecipes.R
import ua.co.myrecipes.ui.dialogs.AddDialogListenerDir
import java.util.*

class AddDirectionsDialog(
    context: Context,
    private var addDialogListenerDir: AddDialogListenerDir
): AppCompatDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_add_direction_item)

        this.setTitle(R.string.new_direction)
        add_ingr_tv.setOnClickListener {
            val direction = direction_edt.text.toString().capitalize(Locale.ROOT)

            if (direction.isEmpty()){
                Toast.makeText(context,R.string.please_enter_the_direction, Toast.LENGTH_SHORT).show()
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
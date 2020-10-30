package ua.co.myrecipes.ui.dialogs

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import kotlinx.android.synthetic.main.dialog_add_ingridient_item.*
import ua.co.myrecipes.R
import ua.co.myrecipes.model.Ingredient
import java.util.*

class AddIngrItemDialog(
    context: Context,
    private var addDialogListenerIngr: AddDialogListenerIngr
): AppCompatDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_add_ingridient_item)
        this.setTitle(R.string.new_ingredient)

        tvAdd.setOnClickListener {
            val ingredient = Ingredient().apply {
                name = etName.text.toString().capitalize(Locale.ROOT)
                amount = if (etAmount.text.toString().isNotBlank()){
                    etAmount.text.toString()
                } else
                    ""
                unit = etUnit.text.toString()
            }

            if (ingredient.name.isEmpty()){
                Toast.makeText(context,R.string.please_enter_the_ingredient, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            addDialogListenerIngr.onAddButtonClick(ingredient)
            dismiss()
        }

        tvCancel.setOnClickListener {
            cancel()
        }
    }

}
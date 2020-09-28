package ua.co.myrecipes.ui.dialogs.addItem

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import kotlinx.android.synthetic.main.dialog_add_ingridient_item.*
import ua.co.myrecipes.R
import ua.co.myrecipes.model.Ingredient
import ua.co.myrecipes.ui.dialogs.AddDialogListenerIngr

class AddIngrItemDialog(
    context: Context,
    private var addDialogListenerIngr: AddDialogListenerIngr
): AppCompatDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_add_ingridient_item)
        this.setTitle(R.string.new_ingredient)

        val types = arrayOf("","slice","tsp","cup","spoon","grams","ml")                                                    ////
        unit_spinner.apply {
            adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, types)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                    (parent.getChildAt(0) as TextView).setTextColor(Color.GRAY)
                    (parent.getChildAt(0) as TextView).textSize = 18f
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        tvAdd.setOnClickListener {
            val ingredient = Ingredient().apply {
                name = etName.text.toString()
                unit = unit_spinner.selectedItem.toString()
                amount = etAmount.text.toString().toInt()
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
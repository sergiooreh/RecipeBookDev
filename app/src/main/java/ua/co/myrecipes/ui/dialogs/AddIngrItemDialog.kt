package ua.co.myrecipes.ui.dialogs

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

class AddIngrItemDialog(                                                /////
    context: Context,
    private var addDialogListener: AddDialogListener
): AppCompatDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_add_ingridient_item)

        val types = arrayOf("","slice","tsp","cup","spoon","grams","ml")
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
            val unit = unit_spinner.selectedItem.toString()


            if (ingredient.name.isEmpty()){
                Toast.makeText(context,"Please, enter the ingredient", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
                /*In Kotlin, return inside a lambda means return from the innermost nesting fun (ignoring lambdas), and it is not allowed in lambdas that are not inlined.
                    The return@label syntax is used to specify the scope to return from.
                    You can use the name of the function the lambda is passed to (fetchUpcomingTrips) as the label:*/
            }

            addDialogListener.onAddButtonClick(ingredient)
            dismiss()
        }

        tvCancel.setOnClickListener {
            cancel()
        }
    }

}
package ua.co.myrecipes.ui.dialogs

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import ua.co.myrecipes.R
import ua.co.myrecipes.databinding.DialogAddIngridientItemBinding
import ua.co.myrecipes.model.Ingredient
import java.util.*

class AddIngrItemDialog(
    context: Context,
    private var addDialogListenerIngr: AddDialogListenerIngr
): AppCompatDialog(context) {
    private var _binding: DialogAddIngridientItemBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DialogAddIngridientItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.setTitle(R.string.new_ingredient)

        binding.tvAdd.setOnClickListener {
            val ingredient = Ingredient().apply {
                name = binding.etName.text.toString().capitalize(Locale.ROOT)
                amount = if (binding.etAmount.text.toString().isNotBlank()){
                    binding.etAmount.text.toString()
                } else
                    ""
                unit = binding.etUnit.text.toString()
            }

            if (ingredient.name.isEmpty()){
                Toast.makeText(context,R.string.please_enter_the_ingredient, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            addDialogListenerIngr.onAddButtonClick(ingredient)
            dismiss()
        }

        binding.tvCancel.setOnClickListener {
            cancel()
        }
    }
}

interface AddDialogListenerIngr {
    fun onAddButtonClick(ingredient: Ingredient)
}
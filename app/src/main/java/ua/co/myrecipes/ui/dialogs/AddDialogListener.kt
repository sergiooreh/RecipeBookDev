package ua.co.myrecipes.ui.dialogs

import ua.co.myrecipes.model.Ingredient


interface AddDialogListener {
    fun onAddButtonClick(ingredient: Ingredient)
}
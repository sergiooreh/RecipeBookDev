package ua.co.myrecipes.ui.dialogs

import ua.co.myrecipes.model.Ingredient


interface AddDialogListenerIngr {
    fun onAddButtonClick(ingredient: Ingredient)
}

interface AddDialogListenerDir {
    fun onAddButtonClick(direction: String)
}
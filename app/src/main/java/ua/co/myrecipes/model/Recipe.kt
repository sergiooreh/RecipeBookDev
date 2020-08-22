package ua.co.myrecipes.model

import android.graphics.Bitmap
import ua.co.myrecipes.util.RecipeType

class Recipe() {
    var id: Int = 0
    var name = ""
    lateinit var type: RecipeType
    var durationPrepare = 0
    var ingredients = listOf<Ingredient>()
    var directions = listOf<String>()
    lateinit var img: Bitmap
}

class Ingredient{
    var name: String = ""
    var amount: Int = 0
    var unit: String = ""
}
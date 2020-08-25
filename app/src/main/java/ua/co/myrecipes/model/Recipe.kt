package ua.co.myrecipes.model

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import ua.co.myrecipes.util.RecipeType

class Recipe() : Parcelable {
    var id: Int = 0

    var name = ""

    lateinit var type: RecipeType

    var durationPrepare = 0

    var ingredients = listOf<Ingredient>()

    var directions = listOf<String>()

    lateinit var img: Bitmap

    constructor(source: Parcel) : this(
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {}

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Recipe> = object : Parcelable.Creator<Recipe> {
            override fun createFromParcel(source: Parcel): Recipe = Recipe(source)
            override fun newArray(size: Int): Array<Recipe?> = arrayOfNulls(size)
        }
    }
}

class Ingredient{
    var name: String = ""
    var amount: Int = 0
    var unit: String = ""
}
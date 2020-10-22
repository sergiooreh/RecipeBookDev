package ua.co.myrecipes.model

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import ua.co.myrecipes.util.RecipeType

class Recipe() : Parcelable {
    var id: Int = 0
    var name = ""
    var author = ""
    lateinit var type: RecipeType
    var durationPrepare = ""
    var ingredients = listOf<Ingredient>()
    var directions = listOf<String>()
    lateinit var img: String

    @get:Exclude
    var imgBitmap: Bitmap? = null
        get() = field

    constructor(source: Parcel) : this(
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {}

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Recipe

        if (id != other.id) return false
        if (name != other.name) return false
        if (author != other.author) return false
        if (type != other.type) return false
        if (durationPrepare != other.durationPrepare) return false
        if (ingredients != other.ingredients) return false
        if (directions != other.directions) return false
        if (img != other.img) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + durationPrepare.hashCode()
        result = 31 * result + ingredients.hashCode()
        result = 31 * result + directions.hashCode()
        result = 31 * result + img.hashCode()
        return result
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Recipe> = object : Parcelable.Creator<Recipe> {
            override fun createFromParcel(source: Parcel): Recipe = Recipe(source)
            override fun newArray(size: Int): Array<Recipe?> = arrayOfNulls(size)
        }
    }
}
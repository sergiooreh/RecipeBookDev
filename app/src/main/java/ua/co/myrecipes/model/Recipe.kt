package ua.co.myrecipes.model

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import ua.co.myrecipes.util.RecipeType
import java.util.*

class Recipe() : Parcelable {
    var id = UUID.randomUUID().toString()
    var name = ""
    var author = ""
    lateinit var type: RecipeType
    var durationPrepare = ""
    var ingredients = mutableListOf<Ingredient>()
    var directions = mutableListOf<String>()
    var userLiked = mutableListOf<String>()
    var imgUrl = ""

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
        if (userLiked != other.userLiked) return false
        if (imgUrl != other.imgUrl) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + durationPrepare.hashCode()
        result = 31 * result + ingredients.hashCode()
        result = 31 * result + directions.hashCode()
        result = 31 * result + userLiked.hashCode()
        result = 31 * result + imgUrl.hashCode()
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
package ua.co.myrecipes.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ua.co.myrecipes.util.RecipeType
import java.util.*

@Parcelize
data class Recipe(
    val id: String = UUID.randomUUID().toString(),
    var name: String = "",
    var author: String = "",
    var type: RecipeType = RecipeType.COOKIES,
    var durationPrepare: String = "",
    var ingredients: @RawValue List<Ingredient> = mutableListOf(),
    var directions: List<String> = mutableListOf(),
    var likedBy: List<String> = mutableListOf(),
    var imgUrl: String = "",
    @get:Exclude var isLiked: Boolean = false,
    @get:Exclude var isLiking: Boolean = false

): Parcelable

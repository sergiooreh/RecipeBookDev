package ua.co.myrecipes.db.recipes

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.room.TypeConverter
import ua.co.myrecipes.model.Ingredient
import ua.co.myrecipes.util.RecipeType
import java.util.stream.Collectors

class Converters {
    @TypeConverter
    fun fromUri(uri: Uri): String{
        return uri.toString()
    }

    @TypeConverter
    fun toUri(data: String): Uri {
        return data.toUri()
    }

    @TypeConverter
    fun fromEnum(type: RecipeType): String {
        return type.toString()
    }

    @TypeConverter
    fun toEnum(type: String): RecipeType {
        return RecipeType.valueOf(type)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @TypeConverter
    fun fromDirections(directions: List<String>): String {
        return directions.stream().collect(Collectors.joining(","))
    }

    @TypeConverter
    fun toDirections(data: String): List<String> {
        return data.split(",".toRegex()).toList()
    }

    @TypeConverter
    fun fromIngredient(ingredients: List<Ingredient>): String {
        return ingredients.forEach {
            "${it.name} - ${it.amount} ${it.unit},"
        }.toString()
    }

    @TypeConverter
    fun toIngredient(data: String): List<Ingredient>{
        val list = mutableListOf<Ingredient>()
        data.substringBefore(',').onEach {
            val ingredient = Ingredient().apply {
                name = data.substringBefore('-')
                amount = data.substringAfter('-').toInt()
                unit = data.substringAfter('-')
            }
            list.add(ingredient)
        }
        return list
    }

}
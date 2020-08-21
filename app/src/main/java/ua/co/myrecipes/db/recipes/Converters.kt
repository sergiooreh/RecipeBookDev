package ua.co.myrecipes.db.recipes

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import ua.co.myrecipes.util.RecipeType
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.Arrays.asList
import java.util.stream.Collectors

class Converters {
    @TypeConverter
    fun toBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
    @TypeConverter
    fun fromBitMap(bmp: Bitmap): ByteArray{
        val outputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toString(type: RecipeType): String {
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

}
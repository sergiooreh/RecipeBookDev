package ua.co.myrecipes.db.recipes

import android.graphics.Bitmap
import androidx.room.*
import ua.co.myrecipes.util.RecipeType

@Entity(tableName = "complexes")
@TypeConverters(Converters::class)
data class RecipeCacheEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "type")
    var type: RecipeType,

    @ColumnInfo(name = "durationPrepare")
    var durationPrepare: Int,

   /* @ColumnInfo(name = "ingredients")
    var ingredients: Map<String,String>,*/

    @ColumnInfo(name = "directions")
    var directions: List<String>,

    @ColumnInfo(name = "img")
    var img: Bitmap,

) {
}
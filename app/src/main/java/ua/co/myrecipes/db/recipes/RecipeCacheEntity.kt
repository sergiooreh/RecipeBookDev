package ua.co.myrecipes.db.recipes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ua.co.myrecipes.model.Ingredient
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
    var durationPrepare: String,

    @ColumnInfo(name = "ingredients")
    var ingredients: List<Ingredient>,

    @ColumnInfo(name = "directions")
    var directions: List<String>,

    @ColumnInfo(name = "img")
    var img: String,
    ) {
}
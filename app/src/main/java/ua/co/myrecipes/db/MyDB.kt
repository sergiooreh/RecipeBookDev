package ua.co.myrecipes.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ua.co.myrecipes.db.recipes.RecipeCacheEntity
import ua.co.myrecipes.db.recipes.RecipeDao


@Database(entities = [RecipeCacheEntity::class],version = 1)
abstract class MyDB: RoomDatabase() {
    abstract fun recipeDao(): RecipeDao

    companion object{
        const val DATABASE_NAME: String = "recipe_db"
    }
}

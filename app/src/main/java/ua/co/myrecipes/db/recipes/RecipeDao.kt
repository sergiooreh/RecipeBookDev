package ua.co.myrecipes.db.recipes

import androidx.room.*
import ua.co.myrecipes.util.RecipeType

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipeEntity: RecipeCacheEntity): Long             //Long - what row inside db was inserted

    @Query("SELECT * FROM complexes")
    suspend fun get(): List<RecipeCacheEntity>

    @Query("SELECT * FROM complexes WHERE type = :type")
    suspend fun getByName(@TypeConverters(Converters::class) type :RecipeType): List<RecipeCacheEntity>

}
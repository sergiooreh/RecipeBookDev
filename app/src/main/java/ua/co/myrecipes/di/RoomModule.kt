package ua.co.myrecipes.di

/*
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import ua.co.myrecipes.db.MyDB
import ua.co.myrecipes.db.recipes.RecipeDao
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object RoomModule {

    @Singleton
    @Provides
    fun provideComplexDb(@ApplicationContext context: Context): MyDB {
        return Room.databaseBuilder(
            context,
            MyDB::class.java,
            MyDB.DATABASE_NAME).fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideRecipeDAO(myDB: MyDB): RecipeDao {
        return myDB.recipeDao()
    }


}*/

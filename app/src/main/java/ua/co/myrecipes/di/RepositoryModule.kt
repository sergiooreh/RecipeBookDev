package ua.co.myrecipes.di

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import ua.co.myrecipes.db.recipes.RecipeCacheMapper
import ua.co.myrecipes.db.recipes.RecipeDao
import ua.co.myrecipes.repository.RecipeRepository
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object RepositoryModule {

    @Singleton
    @Provides
    fun provideCollectionRecipes(): CollectionReference = FirebaseFirestore.getInstance().collection("RecipeType")

    @Singleton
    @Provides
    fun provideRecipeRepository(
        collectionReference: CollectionReference,
        recipeCacheMapper: RecipeCacheMapper,
        recipeDao: RecipeDao,
    ): RecipeRepository{
        return RecipeRepository(collectionReference, recipeCacheMapper, recipeDao)
    }
}
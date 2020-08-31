package ua.co.myrecipes.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import ua.co.myrecipes.db.recipes.RecipeCacheMapper
import ua.co.myrecipes.db.recipes.RecipeDao
import ua.co.myrecipes.repository.RecipeRepository
import ua.co.myrecipes.repository.UserRepository
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object RepositoryModule {

    @Recipes
    @Singleton
    @Provides
    fun provideCollectionRecipes(): CollectionReference = FirebaseFirestore.getInstance().collection("RecipeType")

    @Users
    @Singleton
    @Provides
    fun provideCollectionUsers(): CollectionReference = FirebaseFirestore.getInstance().collection("Users")

    @Singleton
    @Provides
    fun provideAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideRecipeRepository(
        @Recipes collectionReference: CollectionReference,
        recipeCacheMapper: RecipeCacheMapper,
        recipeDao: RecipeDao,
    ): RecipeRepository{
        return RecipeRepository(collectionReference, recipeCacheMapper, recipeDao)
    }

    @Singleton
    @Provides
    fun provideUserRepository(
        @Users collectionReference: CollectionReference,
        firebaseAuth: FirebaseAuth
    ): UserRepository{
        return UserRepository(collectionReference, firebaseAuth)
    }


    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Recipes

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Users
}
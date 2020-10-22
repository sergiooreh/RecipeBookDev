package ua.co.myrecipes.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import ua.co.myrecipes.repository.recipe.RecipeRepository
import ua.co.myrecipes.repository.recipe.RecipeRepositoryInt
import ua.co.myrecipes.repository.user.UserRepository
import ua.co.myrecipes.repository.user.UserRepositoryInt
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

    @Stat
    @Singleton
    @Provides
    fun provideCollectionStat(): CollectionReference = FirebaseFirestore.getInstance().collection("Statistics")

    @Singleton
    @Provides
    fun provideAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideRecipeRepository(
        @Recipes collectionReference: CollectionReference,
        @Stat collectionReferenceStat: CollectionReference,
        @Users collectionReferenceUser: CollectionReference,
        firebaseAuth: FirebaseAuth
    ): RecipeRepositoryInt = RecipeRepository(collectionReference, collectionReferenceStat, collectionReferenceUser, firebaseAuth)


    @Singleton
    @Provides
    fun provideUserRepository(
        @Users collectionReference: CollectionReference,
        @Stat collectionReferenceStat: CollectionReference,
        firebaseAuth: FirebaseAuth
    ): UserRepositoryInt = UserRepository(collectionReference, collectionReferenceStat, firebaseAuth)



    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Recipes

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Users

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Stat
}
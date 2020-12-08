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
    fun provideCollectionRecipes(): CollectionReference = FirebaseFirestore.getInstance().collection("Recipes")

    @Users
    @Singleton
    @Provides
    fun provideCollectionUsers(): CollectionReference = FirebaseFirestore.getInstance().collection("Users")

    @Singleton
    @Provides
    fun provideAuth(): FirebaseAuth = FirebaseAuth.getInstance().also{
        it.useAppLanguage()
    }

    @Singleton
    @Provides
    fun provideUserUid(): String = FirebaseAuth.getInstance().uid ?: ""

    @Singleton
    @Provides
    fun provideRecipeRepository(
        @Recipes collectionReference: CollectionReference,
        @Users collectionReferenceUser: CollectionReference,
        userUid: String
    ): RecipeRepositoryInt = RecipeRepository(collectionReference, collectionReferenceUser, userUid)


    @Singleton
    @Provides
    fun provideUserRepository(
        @Users collectionReference: CollectionReference,
        firebaseAuth: FirebaseAuth
    ): UserRepositoryInt = UserRepository(collectionReference, firebaseAuth)



    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Recipes

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Users
}
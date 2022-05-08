package ua.co.myrecipes.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import ua.co.myrecipes.R
import ua.co.myrecipes.util.Constants.SHARED_PREFERENCES_NAME
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Singleton
    @Provides
    fun provideMainDispatcher() = Dispatchers.Main as CoroutineDispatcher

    @Singleton
    @Provides
    fun provideSharedPreferences(app: Application): SharedPreferences =
        app.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideGlideInstance(
        context: Application
    ) = Glide.with(context).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.ic_broken)
            .error(R.drawable.ic_broken)
            .fitCenter().diskCacheStrategy(DiskCacheStrategy.DATA)
    )

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

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Recipes

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Users
}
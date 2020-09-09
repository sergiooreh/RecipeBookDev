package ua.co.myrecipes.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import ua.co.myrecipes.util.Constants
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object TestAppModule {

    @Singleton
    @Provides
    @Named("test_preferences")
    fun provideSharedPreferences(@ApplicationContext app: Context): SharedPreferences =
        app.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    @Named("test_preferences_app_enter")
    fun provideFirstTimeEnter(@Named("test_preferences")sharedPreferences: SharedPreferences) =
        sharedPreferences.getBoolean(Constants.KEY_FIRST_TIME_ENTER, true)
}
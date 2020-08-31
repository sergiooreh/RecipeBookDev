package ua.co.myrecipes.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import ua.co.myrecipes.util.Constants.KEY_FIRST_TIME_ENTER
import ua.co.myrecipes.util.Constants.SHARED_PREFERENCES_NAME
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object AppModule {
    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context): SharedPreferences =
        app.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideFirstTimeEnter(sharedPreferences: SharedPreferences) =
        sharedPreferences.getBoolean(KEY_FIRST_TIME_ENTER, true)
}
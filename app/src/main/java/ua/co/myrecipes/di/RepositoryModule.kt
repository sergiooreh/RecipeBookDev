package ua.co.myrecipes.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.co.myrecipes.repository.recipe.RecipeRepository
import ua.co.myrecipes.repository.recipe.RecipeRepositoryInt
import ua.co.myrecipes.repository.user.UserRepository
import ua.co.myrecipes.repository.user.UserRepositoryInt
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {

//@Binds eliminate code generated, should use it if you provide abstraction!!!! (How to Build a Clean Architecture Stock Market App 1:38:00

    @Singleton
    @Binds
    abstract fun bindRecipeRepository(recipeRepository: RecipeRepository): RecipeRepositoryInt

    @Singleton
    @Binds
    abstract fun bindUserRepository(userRepository: UserRepository): UserRepositoryInt

}
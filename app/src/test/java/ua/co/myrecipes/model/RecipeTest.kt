package ua.co.myrecipes.model

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import ua.co.myrecipes.util.RecipeType

class RecipeTest{
    private lateinit var recipe: Recipe

    @Before
    fun setup(){
        recipe = Recipe()
    }

    @Test
    fun `likedBy, directions and ingredients are empty`(){
        assertThat(recipe.likedBy.size + recipe.directions.size + recipe.ingredients.size).isEqualTo(0)
    }

    @Test
    fun `recipe's id is not empty`(){
        assertThat(recipe.id).isNotEmpty()
    }

    @Test
    fun `recipe's type is COOKIES`(){
        assertThat(recipe.type).isEqualTo(RecipeType.COOKIES)
    }
}
package ua.co.myrecipes.model

import com.google.common.truth.Truth.assertThat
import androidx.test.filters.SmallTest
import org.junit.Before
import org.junit.Test

@SmallTest
class RecipeTest{
    private lateinit var recipe: Recipe

    @Before
    fun setup(){
        recipe = Recipe()
    }

    @Test
    fun `directions and ingredients are empty when recipe created`(){
        assertThat(recipe.directions.size + recipe.ingredients.size).isEqualTo(0)
    }
}
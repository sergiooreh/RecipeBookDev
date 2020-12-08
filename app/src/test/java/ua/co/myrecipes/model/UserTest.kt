package ua.co.myrecipes.model

import com.google.common.truth.Truth.assertThat
import androidx.test.filters.SmallTest
import org.junit.Before
import org.junit.Test
@SmallTest
class UserTest{
    private lateinit var user: User

    @Before
    fun setup(){
        user = User("fff@fff.fff", "1234567")
    }

    @Test
    fun `nickname is formed`(){
        val userNickname = user.nickname
        assertThat(userNickname).isEqualTo("fff")
    }

    @Test
    fun `all lists are empty when user created`(){
        assertThat(user.recipes.size + user.likedRecipes.size).isEqualTo(0)
    }
}
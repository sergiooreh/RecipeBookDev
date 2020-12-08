package ua.co.myrecipes.model


data class User(
    var uid: String = "",
    val email: String = "",
    var token: String = "",
    var nickname: String = email.substringBefore("@"),
    var about: String = "",
    var img: String = "",

    var recipes: List<String> = listOf(),
    var likedRecipes: List<String> = listOf()
)
package ua.co.myrecipes.model

class User() {
    var id = 0
    lateinit var email: String
    private lateinit var password: String
    lateinit var nickname: String

    var recipe = listOf<Recipe>()
    var likedRecipes = listOf<Recipe>()
    var followers = listOf<User>()
    var following = listOf<User>()

    constructor(_email: String, _password: String) : this() {
        this.email = _email
        this.password = _password
        this.nickname = _email.substringBefore("@")
    }
}
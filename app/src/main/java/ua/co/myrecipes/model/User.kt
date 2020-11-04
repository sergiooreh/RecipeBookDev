package ua.co.myrecipes.model


class User() {
    lateinit var id: String
    lateinit var email: String
    private lateinit var password: String
    var token = ""
    var nickname = ""
    var about: String = ""
    var img: String = ""

    var recipe = mutableMapOf<String,String>()
    var likedRecipes = mutableMapOf<String,String>()

    constructor(_email: String, _password: String) : this() {
        this.email = _email
        this.password = _password
        this.nickname = _email.substringBefore("@")
    }
}
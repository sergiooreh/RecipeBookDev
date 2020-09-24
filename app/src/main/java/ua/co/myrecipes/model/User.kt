package ua.co.myrecipes.model

import android.graphics.Bitmap
import com.google.firebase.firestore.Exclude

class User() {
    var id = 0
    lateinit var email: String
    private lateinit var password: String
    lateinit var nickname: String
    var about: String = ""
    lateinit var img: String

    @get:Exclude
    var imgBitmap: Bitmap? = null
        get() = field

    var recipe = mutableMapOf<String,String>()
    var likedRecipes = mutableMapOf<String,String>()

    constructor(_email: String, _password: String) : this() {
        this.email = _email
        this.password = _password
        this.nickname = _email.substringBefore("@")
    }
}
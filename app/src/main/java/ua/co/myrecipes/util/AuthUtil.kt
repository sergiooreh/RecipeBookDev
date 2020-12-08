package ua.co.myrecipes.util

import com.google.firebase.auth.FirebaseAuth

class AuthUtil {

    companion object{
        val email : String
            get() = FirebaseAuth.getInstance().currentUser?.email ?: ""
    }

}
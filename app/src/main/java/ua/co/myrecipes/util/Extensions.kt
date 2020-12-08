package ua.co.myrecipes.util

import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException

inline fun <T> authCall(action: () -> Resource<T>): Resource<T> {
    return try {
        action()
    } catch (e: FirebaseAuthException){
        Resource.Error(e.errorCode, null)
    }
}

inline fun <T> dataCall(action: () -> Resource<T>): Resource<T> {
    Resource.Loading(null)
    return try {
        action()
    } catch (e: FirebaseFirestoreException){
        Resource.Error(e.message ?: "An unknown error", null)
    }
    catch (e: Exception){
        Resource.Error(e.message ?: "An unknown error", null)
    }
}
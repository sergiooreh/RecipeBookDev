package ua.co.myrecipes.util

import java.lang.Exception

sealed class DataState<out R> {
    object Loading: DataState<Nothing>()
    data class Success<out T>(val data: T): DataState<T>()
    data class Error(val exception: Exception): DataState<Nothing>()
}

/*Wrapper class to handle responses from server*/
data class Resource<out T>(val status: Status,val data: T?, val message: String?) {
    companion object{
        fun <T> success(data: T?): Resource<T>{
            return Resource(Status.SUCCESS,data,null)
        }
        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(Status.ERROR,data,msg)
        }
        fun <T> loading(data: T?): Resource<T>{
            return Resource(Status.LOADING,data,null)
        }
    }
}

enum class Status{
    SUCCESS,
    ERROR,
    LOADING
}
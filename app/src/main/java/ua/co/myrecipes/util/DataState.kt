package ua.co.myrecipes.util

import java.lang.Exception

sealed class DataState<out R> {
    object Loading: DataState<Nothing>()
    data class Success<out T>(val data: T): DataState<T>()
    data class Error(val exception: Exception): DataState<Nothing>()
}
package ua.co.myrecipes.notification.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ua.co.myrecipes.util.Constants.BASE_URL

class RetrofitInstance {

    companion object{
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api by lazy {
            retrofit.create(NotificationAPI::class.java)
        }
    }
}
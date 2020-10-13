package ua.co.myrecipes.notification.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import ua.co.myrecipes.notification.PushNotification
import ua.co.myrecipes.util.Constants.CONTENT_TYPE
import ua.co.myrecipes.util.Constants.SERVER_KEY

interface NotificationAPI {

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")                      //overrides default header
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}
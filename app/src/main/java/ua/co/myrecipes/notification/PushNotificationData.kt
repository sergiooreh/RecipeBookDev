package ua.co.myrecipes.notification

import androidx.annotation.Keep

@Keep
data class PushNotificationData(
    val title: String,
    val message: String
)
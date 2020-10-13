package ua.co.myrecipes.notification

class PushNotification(
    val data: PushNotificationData,
    val to: String                  //topic(all who subscribed to this topic) or recipient token(unique id for specific device
) {
}
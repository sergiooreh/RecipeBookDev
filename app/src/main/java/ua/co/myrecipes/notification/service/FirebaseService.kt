package ua.co.myrecipes.notification.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.co.myrecipes.R
import ua.co.myrecipes.repository.user.UserRepository
import ua.co.myrecipes.ui.MainActivity
import javax.inject.Inject
import kotlin.random.Random

private const val CHANNEL_ID = "my_channel"                     //const here cause we need this nowhere else

class FirebaseService @Inject constructor(
    private val userRepository: UserRepository
): FirebaseMessagingService() {                 //to have ability get notifications when we don't use app

    override fun onNewToken(newToken: String) {                         //whenever we get a new token
        super.onNewToken(newToken)
        CoroutineScope(Dispatchers.IO).launch {
            userRepository.updateToken(newToken)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {        //when this device gets message
        super.onMessageReceived(message)

        val intent = Intent(this, MainActivity::class.java)      //when we clicked notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this,0,intent,FLAG_ONE_SHOT)      //when we click notification this pending intent consumes one time and all
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.ic_humb_up)
            .setAutoCancel(true)                                                            //notification deletes when we click on it
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationID,notification)
    }

    /*Starting in Android 8.0 (API level 26), all notifications must be assigned to a channel.
    For each channel, you can set the visual and auditory behavior that is applied to all notifications in that channel. */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID,channelName,IMPORTANCE_LOW).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GRAY
        }
        notificationManager.createNotificationChannel(channel)
    }
}
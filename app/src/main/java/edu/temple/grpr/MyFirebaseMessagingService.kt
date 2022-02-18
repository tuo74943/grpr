package edu.temple.grpr

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    //on initial startup, FCM SDK generates a registration token for the client app instance.
    //accessing this token by ovverriding this
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d("TOKEN", "Refreshed token: $p0")
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
    }
}
package edu.temple.grpr

import android.content.Intent
import android.util.Log
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.remoteMessage
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        val UPDATE_ACTION = "grpr_action_update"
        val UPDATE_KEY = "grpr_update_key"
        val UPDATE_MESSAGE = "grpr_action_message"
        val MESSAGE_KEY = "grpr_message_key"
    }

    //on initial startup, FCM SDK generates a registration token for the client app instance.
    //accessing this token by overriding this
    override fun onNewToken(token: String) {
        Helper.user.clearFCMToken(this)
        Helper.user.registerTokenFlow(this, token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM Message", remoteMessage.data["payload"].toString())
        val message = JSONObject(remoteMessage.data["payload"].toString())

        when (message.getString("action")) {
            "UPDATE" -> {
                sendBroadcast(Intent(UPDATE_ACTION).putExtra(UPDATE_KEY, message.getJSONArray("data").toString()))
            }
            "MESSAGE" -> {
                sendBroadcast(Intent(UPDATE_MESSAGE).putExtra(MESSAGE_KEY, message.toString()))
            }
        }
    }
}
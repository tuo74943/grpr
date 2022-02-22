package edu.temple.grpr

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.remoteMessage
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {
    //on initial startup, FCM SDK generates a registration token for the client app instance.
    //accessing this token by overriding this
    override fun onNewToken(token: String) {
        //get updated InstanceID token
        Log.d("On New Token", "Refreshed token: $token")

        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val message : JSONObject = JSONObject(remoteMessage.data.get("payload")!!)
        Log.d("Payload", message.toString())
    }

    private fun sendRegistrationToServer(token: String){
        Log.d("Sending registration to server", "but not really")
        //TODO if the token is refreshed send this token to the server (API call to update)
    }
}
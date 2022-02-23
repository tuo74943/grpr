package edu.temple.grpr

import android.content.Intent
import android.util.Log
import androidx.core.os.bundleOf
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
        val message : String = (remoteMessage.data.get("payload")!!)
//        Log.d("Payload", message.toString())
        Intent().also { intent ->
            intent.setAction("com.example.broadcast.MY_NOTIFICATION")
            intent.putExtra("payload", message)
            sendBroadcast(intent)
        }

    }

    private fun sendRegistrationToServer(token: String){
        if(!Helper.user.getSessionKey(this).isNullOrEmpty()){
            Helper.api.updateFCM(this, Helper.user.get(this), Helper.user.getSessionKey(this)!!, token, null)
        }
    }
}
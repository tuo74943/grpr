package edu.temple.grpr

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONObject
import java.io.*


class MainActivity : AppCompatActivity(), DashboardFragment.DashboardInterface, GroupFragment.GroupControlInterface{
    var serviceIntent : Intent? = null
    val grprViewModel : GrPrViewModel by lazy {
        ViewModelProvider(this).get(GrPrViewModel::class.java)
    }

    var isConnected = false
    private val filename = "MessageList"
    val file : File by lazy {
        File(filesDir, filename)
    }

    //updates Viewmodel with location data whenever we recieve it from the locationservice
    var locationHandler = object : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            grprViewModel.setLocation(msg.obj as LatLng)
        }
    }

    private val groupBroadCastReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            val participantArray = JSONArray(p1!!.getStringExtra(MyFirebaseMessagingService.UPDATE_KEY))
            val group = Group()
            var participantObject : JSONObject
            for (i in 0 until participantArray.length()) {
                participantObject = participantArray.getJSONObject(i)
                group.addParticipant(
                    Participant(
                        participantObject.getString("username"),
                        LatLng(
                            participantObject.getDouble("latitude"),
                            participantObject.getDouble("longitude")
                        )
                    )
                )
            }

            grprViewModel.setGroup(group)
        }

    }

    var serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {

            // Provide service with handler
            (iBinder as LocationService.LocationBinder).setHandler(locationHandler)
            isConnected = true
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            isConnected = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()
        serviceIntent = Intent(this, LocationService::class.java)

        if(file.exists()){
//            Log.d("Item", readFromFile()?.size.toString())
            grprViewModel.setMessageList(readFromFile()!!)
        }

        grprViewModel.getGroupId().observe(this) {
            if (!it.isNullOrEmpty())
                supportActionBar?.title = "GRPR - $it"
            else
                supportActionBar?.title = "GRPR"
        }



        Helper.user.getGroupId(this)?.run {
            grprViewModel.setGroupId(this)
            startLocationService()
        }

        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.RECORD_AUDIO), 123)
        }
    }

    private fun writeToFile() {
        try {
            val fileOutputStream: FileOutputStream = this.openFileOutput(filename, MODE_PRIVATE)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            objectOutputStream.writeObject(grprViewModel.getMessageList())
            objectOutputStream.close()
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun readFromFile() : MessageList? {
        var messageList: MessageList? = null

        try {
            val fileInputStream: FileInputStream = openFileInput(filename)
            val objectInputStream = ObjectInputStream(fileInputStream)
            messageList = objectInputStream.readObject() as MessageList
            objectInputStream.close()
            fileInputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }

        return messageList
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(groupBroadCastReceiver, IntentFilter(MyFirebaseMessagingService.UPDATE_ACTION))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(groupBroadCastReceiver)
        if(!Helper.user.getGroupId(this).isNullOrBlank()){
            Log.d("File", "written to")
            writeToFile()
        }
        else{
            Log.d("File", "deleted")
            file.delete()
        }
    }

    private fun createNotificationChannel() {
        val channel =
            NotificationChannel("default", "Active Convoy", NotificationManager.IMPORTANCE_HIGH)

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    override fun createGroup() {
        Helper.api.createGroup(this, Helper.user.get(this), Helper.user.getSessionKey(this)!!, object: Helper.api.Response {
            override fun processResponse(response: JSONObject) {
                if (Helper.api.isSuccess(response)) {
                    grprViewModel.setGroupId(response.getString("group_id"))
                    Helper.user.saveGroupId(this@MainActivity, grprViewModel.getGroupId().value!!)
                    invalidateOptionsMenu()
                    startLocationService()
                } else {
                    Toast.makeText(this@MainActivity, Helper.api.getErrorMessage(response), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun endGroup() {
        AlertDialog.Builder(this).setTitle("Close Group")
            .setMessage("Are you sure you want to close the group?")
            .setPositiveButton("Yes"
            ) { _, _ -> Helper.api.closeGroup(
                this,
                Helper.user.get(this),
                Helper.user.getSessionKey(this)!!,
                grprViewModel.getGroupId().value!!,
                object: Helper.api.Response {
                    override fun processResponse(response: JSONObject) {
                        if (Helper.api.isSuccess(response)) {
                            grprViewModel.setGroupId("")
                            Helper.user.clearGroupId(this@MainActivity)
                            invalidateOptionsMenu()
                            stopLocationService()
                            grprViewModel.removeMessageList()
                        } else
                            Toast.makeText(this@MainActivity, Helper.api.getErrorMessage(response), Toast.LENGTH_SHORT).show()
                    }

                }
            )}
            .setNegativeButton("Cancel") { p0, _ -> p0.cancel() }
            .show()
    }
    override fun joinGroup() {
        Navigation.findNavController(findViewById(R.id.fragmentContainerView))
            .navigate(R.id.action_dashboardFragment_to_groupFragment, Bundle().apply {
                putBoolean("JOIN_ACTION", true)
            })
    }

    override fun leaveGroup() {
        Navigation.findNavController(findViewById(R.id.fragmentContainerView))
            .navigate(R.id.action_dashboardFragment_to_groupFragment, Bundle().apply {
                putBoolean("JOIN_ACTION", false)
            })
    }

    override fun logout() {
        Helper.user.clearSessionData(this)
        Navigation.findNavController(findViewById(R.id.fragmentContainerView))
            .navigate(R.id.action_dashboardFragment_to_loginFragment)
    }

    override fun loadAudio() {
        Navigation.findNavController(findViewById(R.id.fragmentContainerView))
            .navigate(R.id.action_dashboardFragment_to_audioContainerFragment)
    }

    private fun startLocationService(){
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE)
        startService(serviceIntent)
    }

    private fun stopLocationService(){
        unbindService(serviceConnection)
        stopService(serviceIntent)
    }

    override fun joinGroupFlow(groupId: String) {
        Helper.api.joinGroup(
            this,
            Helper.user.get(this),
            Helper.user.getSessionKey(this)!!,
            groupId,
            object: Helper.api.Response {
                override fun processResponse(response: JSONObject) {
                    Helper.user.saveGroupId(this@MainActivity, groupId)
                    startLocationService()
                    // Refresh action bar menu items
                    invalidateOptionsMenu()
                }

            }
        )
    }

    override fun leaveGroupFlow(groupId: String) {
        Helper.api.leaveGroup(
            this,
            Helper.user.get(this),
            Helper.user.getSessionKey(this)!!,
            Helper.user.getGroupId(this)!!,
            object: Helper.api.Response {
                override fun processResponse(response: JSONObject) {
                    Helper.user.clearGroupId(this@MainActivity)
                    stopLocationService()
                    grprViewModel.removeMessageList()
                    // Refresh action bar menu items
                    invalidateOptionsMenu()
                }

            }
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 123){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[2] != PackageManager.PERMISSION_GRANTED){
                //if permissions were not granted we close the app
                finish()
            }
        }
    }
}
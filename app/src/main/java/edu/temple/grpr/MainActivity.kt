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
import org.json.JSONObject


class MainActivity : AppCompatActivity(), DashboardFragment.DashboardInterface, GroupFragment.GroupInterface{

    val brTag = "com.example.broadcast.MY_NOTIFICATION"
    var serviceIntent : Intent? = null
    val grprViewModel : GrPrViewModel by lazy {
        ViewModelProvider(this).get(GrPrViewModel::class.java)
    }
    var isConnected = false

    lateinit var br : BroadcastReceiver

    //updates Viewmodel with location data whenever we recieve it from the locationservice
    var locationHandler = object : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            sendToFCM(msg.obj as LatLng)
            grprViewModel.setLocation(msg.obj as LatLng)
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

        val filter = IntentFilter()
        filter.addAction(brTag)
        br = object : BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
                Log.d("RECEIVER", p1?.getStringExtra("payload")!!)
            }

        }
        registerReceiver(br, filter)

        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 123)
        }
    }

    private fun createNotificationChannel() {
        val channel =
            NotificationChannel("default", "Active Convoy", NotificationManager.IMPORTANCE_HIGH)

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    override fun createGroup() {
        Log.d("Create","Button was pressed")
        //lets the app know that the creator of the group is this person
        Helper.user.setCreatorStatus(this)
        Helper.api.createGroup(this, Helper.user.get(this), Helper.user.getSessionKey(this)!!, object: Helper.api.Response {
            override fun processResponse(response: JSONObject) {
                if (Helper.api.isSuccess(response)) {
                    grprViewModel.setGroupId(response.getString("group_id"))
                    grprViewModel.setCreatorStatus(true)
                    Helper.user.saveGroupId(this@MainActivity, grprViewModel.getGroupId().value!!)
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
                            stopLocationService()
                            grprViewModel.setGroupId("")
                            grprViewModel.setCreatorStatus(false)
                            Helper.user.clearGroupId(this@MainActivity)
                            Helper.user.clearCreatorStatus(this@MainActivity)
                            Log.d("ENDGROUP", "Service stopped")
                        } else
                            Toast.makeText(this@MainActivity, Helper.api.getErrorMessage(response), Toast.LENGTH_SHORT).show()
                    }

                }
            )}
            .setNegativeButton("Cancel") { p0, _ -> p0.cancel() }
            .show()
    }

    override fun leaveGroup() {
        AlertDialog.Builder(this).setTitle("Leave Group")
            .setMessage("Are you sure you want to leave the group?")
            .setPositiveButton("Yes"
            ) { _, _ -> Helper.api.leaveGroup(
                this,
                Helper.user.get(this),
                Helper.user.getSessionKey(this)!!,
                grprViewModel.getGroupId().value!!,
                object: Helper.api.Response {
                    override fun processResponse(response: JSONObject) {
                        if (Helper.api.isSuccess(response)) {
                            grprViewModel.setGroupId("")
                            Helper.user.clearGroupId(this@MainActivity)
                            stopLocationService()
                        } else
                            Toast.makeText(this@MainActivity, Helper.api.getErrorMessage(response), Toast.LENGTH_SHORT).show()
                    }

                }
            )}
            .setNegativeButton("Cancel") { p0, _ -> p0.cancel() }
            .show()
    }

    override fun joinGroup() {
        startLocationService()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 123){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                //if permissions were not granted we close the app
                finish()
            }
        }
    }

    private fun startLocationService(){
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE)
        startService(serviceIntent)
    }

    private fun stopLocationService(){
        unbindService(serviceConnection)
        stopService(serviceIntent)
    }

    override fun logout() {
        Helper.user.clearSessionData(this)
        Navigation.findNavController(findViewById(R.id.fragmentContainerView))
            .navigate(R.id.action_dashboardFragment_to_loginFragment)
    }


    private fun sendToFCM(latLng: LatLng) {
        if (!Helper.user.getGroupId(this).isNullOrEmpty()) {
            Helper.api.updateGroup(
                this,
                Helper.user.get(this),
                Helper.user.getSessionKey(this)!!,
                Helper.user.getGroupId(this)!!,
                latLng,
                object : Helper.api.Response {
                    override fun processResponse(response: JSONObject) {
                        if (Helper.api.isSuccess(response)) {
                            Log.v("Update Group", response.toString())
                        } else {
                            Log.d("Update Group", Helper.api.getErrorMessage(response))
                        }
                    }
                })

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(br)
        Log.d("reciever", "killed")
    }
}
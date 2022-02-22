package edu.temple.grpr

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject


class MainActivity : AppCompatActivity(), DashboardFragment.DashboardInterface{

    var serviceIntent : Intent? = null
    val grprViewModel : GrPrViewModel by lazy {
        ViewModelProvider(this).get(GrPrViewModel::class.java)
    }

    //updates Viewmodel with location data whenever we recieve it from the locationservice
    var locationHandler = object : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            grprViewModel.setLocation(msg.obj as LatLng)
        }
    }

    var serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {

            // Provide service with handler
            (iBinder as LocationService.LocationBinder).setHandler(locationHandler)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {}
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


        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 123)
        }



        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE)
    }

    private fun createNotificationChannel() {
        val channel =
            NotificationChannel("default", "Active Convoy", NotificationManager.IMPORTANCE_HIGH)

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    override fun createGroup() {
        Log.d("Create","Button was pressed")
        Helper.api.createGroup(this, Helper.user.get(this), Helper.user.getSessionKey(this)!!, object: Helper.api.Response {
            override fun processResponse(response: JSONObject) {
                if (Helper.api.isSuccess(response)) {
                    grprViewModel.setGroupId(response.getString("group_id"))
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
                            grprViewModel.setGroupId("")
                            Helper.user.clearGroupId(this@MainActivity)
                            stopService(serviceIntent)
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
                            //TODO remove true from joined argument
                            Helper.user.clearGroupId(this@MainActivity)
                            stopService(serviceIntent)
                            Log.d("LEAVE GROUP", "Service stopped")
                        } else
                            Toast.makeText(this@MainActivity, Helper.api.getErrorMessage(response), Toast.LENGTH_SHORT).show()
                    }

                }
            )}
            .setNegativeButton("Cancel") { p0, _ -> p0.cancel() }
            .show()
    }

    override fun joinGroup() {
        startService(serviceIntent)
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
        startService(serviceIntent)
    }

    override fun logout() {
        Helper.user.clearSessionData(this)
        Navigation.findNavController(findViewById(R.id.fragmentContainerView))
            .navigate(R.id.action_dashboardFragment_to_loginFragment)
    }
}
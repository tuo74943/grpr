package edu.temple.grpr

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class MainFragment : Fragment(), OnMapReadyCallback{
    lateinit var logoutButton: Button
    lateinit var joinFab : ExtendedFloatingActionButton
    lateinit var createFab : ExtendedFloatingActionButton
    lateinit var leaveButton: Button
    val locationManager : LocationManager by lazy {
        activity?.getSystemService(LocationManager::class.java) as LocationManager
    }

    lateinit var locationListener: LocationListener

    var previousLocation : Location? = null
    var distanceTraveled = 0f

    lateinit var mapView : MapView

    lateinit var googleMap : GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout =  inflater.inflate(R.layout.fragment_main, container, false)
        logoutButton = layout.findViewById(R.id.logoutButton)
        mapView = layout.findViewById(R.id.mapView)
        leaveButton = layout.findViewById(R.id.leaveButton)
        joinFab = layout.findViewById(R.id.joinFab)
        createFab = layout.findViewById(R.id.createFab)
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView.getMapAsync(this)
        mapView.onCreate(savedInstanceState)

        logoutButton.setOnClickListener {
            (activity as MainInterface).logout()
        }

        leaveButton.setOnClickListener {
            Log.d("leave button", "clicked")
            (activity as MainInterface).leaveButtonPressed()
        }

        joinFab.setOnClickListener{
            Log.d("join fab", "clicked")
            (activity as MainInterface).joinFABPressed()
        }

        createFab.setOnClickListener{
            Log.d("create fab", "clicked")
            (activity as MainInterface).createFABPressed()
        }

        locationListener = LocationListener {
            if (previousLocation != null) {
                distanceTraveled += it.distanceTo(previousLocation)
                 Log.d("distance traveled: ", distanceTraveled.toString())

                val latLng = LatLng(it.latitude, it.longitude)

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
            }
            previousLocation = it
        }
    }


    @SuppressLint("MissingPermission")
    private fun doGPSStuff(){
        if((activity as MainInterface).permissionGranted())
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000.toLong(), 5f, locationListener)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        doGPSStuff()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        locationManager.removeUpdates(locationListener)
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    interface MainInterface {
        fun logout()
        fun permissionGranted() : Boolean
        fun createFABPressed()
        fun joinFABPressed()
        fun leaveButtonPressed()
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
    }
}
package edu.temple.grpr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class MainFragment : Fragment(), OnMapReadyCallback{

    lateinit var logoutButton: Button
    lateinit var joinFab : ExtendedFloatingActionButton
    lateinit var createFab : ExtendedFloatingActionButton
    lateinit var leaveButton: Button
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
            (activity as MainInterface).leaveButtonPressed()
        }

        joinFab.setOnClickListener{
            (activity as MainInterface).joinFABPressed()
        }

        createFab.setOnClickListener{
            (activity as MainInterface).createFABPressed()
        }
    }


    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
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
        fun createFABPressed()
        fun joinFABPressed()
        fun leaveButtonPressed()
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        //TODO add marker here
    }
}
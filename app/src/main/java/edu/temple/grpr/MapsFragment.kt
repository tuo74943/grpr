package edu.temple.grpr

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    lateinit var map: GoogleMap
    var myMarker: Marker? = null

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        // Update location on map whenever ViewModel is updated
        ViewModelProvider(requireActivity()).get(GrPrViewModel::class.java).getLocation()
            ?.observe(requireActivity()) {
                if (myMarker == null) myMarker = map.addMarker(
                    MarkerOptions().position(it)
                ) else myMarker?.setPosition(it)
            }
    }

    fun updateMap(group : Group){
        val list = group.getParticipants()
        for(i in 0 until list.size){
            if(!(list[i].username == Helper.user.get(requireContext()).username))
                map.addMarker(MarkerOptions().title(list[i].username).position(list[i].latLng))
        }
    }
}
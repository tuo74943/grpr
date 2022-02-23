package edu.temple.grpr

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

data class Participant(val username: String, val firstname : String, val lastname : String, val latLng: LatLng){
}

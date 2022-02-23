package edu.temple.grpr

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray

class Group {

    private val pList : MutableList<Participant> by lazy {
        ArrayList()
    }

    fun replaceParticipants(jsonArray: JSONArray){
        pList.clear()
        for(i in 0 until jsonArray.length()){
            val username =jsonArray.getJSONObject(i).getString("username")
            val firstname = jsonArray.getJSONObject(i).getString("firstname")
            val lastname = jsonArray.getJSONObject(i).getString("lastname")
            val latitude = jsonArray.getJSONObject(i).getDouble("latitude")
            val longitude = jsonArray.getJSONObject(i).getDouble("longitude")

            pList.add(Participant(username, firstname, lastname, LatLng(latitude, longitude)))
        }
    }

    fun getParticipants() : MutableList<Participant>{
        return pList
    }

    fun size() = pList.size
}

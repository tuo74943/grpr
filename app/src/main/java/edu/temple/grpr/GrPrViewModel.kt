package edu.temple.grpr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class GrPrViewModel : ViewModel() {

    private val participants by lazy{
        Group()
    }

    private val location by lazy {
        MutableLiveData<LatLng>()
    }

    private val groupId by lazy {
        MutableLiveData<String>()
    }

    private val group by lazy{
        MutableLiveData<Group>()
    }

    fun setGroupId(id: String) {
        groupId.value = id
    }

    fun setLocation(latLng: LatLng) {
        location.value = latLng
    }

    fun getLocation(): LiveData<LatLng> {
        return location
    }

    fun getGroupId(): LiveData<String> {
        return groupId
    }

    fun setGroup(group : Group){
        participants.updateGroup(group)

        //inform observers that we have provided them with a new group
        this.group.value = group
    }

    //livedata to observe
    fun getGroupToObserve() : LiveData<Group>{
        return group
    }

    //actual data
    fun getGroup(): Group{
        return participants
    }
}
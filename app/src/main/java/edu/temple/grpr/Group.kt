package edu.temple.grpr

class Group() {

    private val participants = ArrayList<Participant>()

    val size : Int get() = participants.size

    fun updateGroup(group: Group){
        //remove missing participants
        participants.forEach {
            if (!group.participants.contains(it)) {
                participants.remove(it)
                it.removeMarker()
            }
        }

        group.participants.forEach{
            if (!participants.contains(it)){
                participants.add(it)
            } else{
                participants[participants.indexOf(it)].latLng = it.latLng
            }
        }
    }

    fun getParticipant(index : Int) :Participant{
        return participants[index]
    }

    fun addParticipant(participant: Participant) {
        participants.add(participant)
    }
}

package edu.temple.grpr

import java.io.File
import java.io.Serializable

class AudioMessage (val username : String, val audioFile : File) : Serializable{
    fun deleteFile(){
        audioFile.delete()
    }
}
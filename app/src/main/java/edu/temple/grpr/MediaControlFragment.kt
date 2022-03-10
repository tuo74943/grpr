package edu.temple.grpr

import android.media.MediaDataSource
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MediaControlFragment : Fragment() {

    lateinit var recordButton: ImageButton
    lateinit var playButton :ImageButton
    lateinit var cancelButton : ImageButton
    lateinit var sendButton: Button

    var player: MediaPlayer? = null
    var recorder: MediaRecorder? = null
    var filename : String? = null
    var file : File? = null
    var isRecording = false

    val grPrViewModel by lazy {
        ViewModelProvider(requireActivity()).get(GrPrViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_media_control, container, false)

        recordButton = layout.findViewById(R.id.recordButton)
        playButton = layout.findViewById(R.id.playButton)
        cancelButton = layout.findViewById(R.id.cancelButton)
        sendButton = layout.findViewById(R.id.sendButton)

        recordButton.setOnClickListener{
            if(isRecording){
                //stop recording
                stopRecording()
                recordButton.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.outline_mic_black_36,null))
                isRecording = false
                updateUI()
            }else{
                //start recording
                recordButton.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.circle_36, null))
                isRecording = true
                startRecording()
            }
        }

        playButton.setOnClickListener {
            startPlaying()
        }

        cancelButton.setOnClickListener {
            //deletes file created and resets UI
            file!!.delete()
            resetUI()
        }

        sendButton.setOnClickListener {
            //TODO Send audio file to fcm and place it into recyclerview
            val sdf = SimpleDateFormat("MM/dd HH:mm:ss", Locale.getDefault())
            val currentTime: String = sdf.format(Date())
            grPrViewModel.addMessage(AudioMessage(Helper.user.get(requireContext()).username,
                file!!, currentTime))
            resetUI()
        }

        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resetUI()
    }

    fun resetUI(){
        playButton.visibility = View.INVISIBLE
        cancelButton.visibility = View.INVISIBLE
        sendButton.visibility = View.INVISIBLE
        recordButton.visibility = View.VISIBLE

    }

    fun updateUI(){
        playButton.visibility = View.VISIBLE
        cancelButton.visibility = View.VISIBLE
        sendButton.visibility = View.VISIBLE
        recordButton.visibility = View.INVISIBLE
    }

    private fun startRecording() {
        val formatter = SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.US)
        val date = Date()
        filename = "Recording_" + formatter.format(date) + ".mp3"
        file = File(context?.filesDir, filename!!)
        Log.d("Filename", filename!!)

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(file)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e("LOG TAG", "prepare() failed")
            }
            start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(file!!.path)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("LOG_TAG", "prepare() failed")
            }
        }
    }
}
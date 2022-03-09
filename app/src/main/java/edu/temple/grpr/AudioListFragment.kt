package edu.temple.grpr

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.IOException

class AudioListFragment : Fragment() {

    private var player: MediaPlayer? = null
    lateinit var recyclerView: RecyclerView

    val grPrViewModel by lazy {
        ViewModelProvider(requireActivity()).get(GrPrViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_audio_list, container, false)

        recyclerView = layout.findViewById(R.id.recyclerView)
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = AudioMessAdapter(requireContext(), grPrViewModel.getMessageList(), {audioMessage : AudioMessage -> onClick(audioMessage) })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        grPrViewModel.getListToObserve().observe(requireActivity()){
            Log.d("Observer", "list changed notifying adapter")
            adapter.notifyDataSetChanged()
        }
    }

    fun onClick(audioMessage: AudioMessage){
        Log.d("button was pressed", "button!@!")
        startPlaying(audioMessage.audioFile)
    }

    private fun startPlaying(file : File) {
        player = MediaPlayer().apply {
            try {
                setDataSource(file.path)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("LOG_TAG", "prepare() failed")
            }
        }
    }

}
package edu.temple.grpr

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AudioListFragment : Fragment() {

    lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_audio_list, container, false)

        recyclerView = layout.findViewById(R.id.recyclerView)

        val userList = ArrayList<User>()

        userList.add(User("Bob", null, null))
        userList.add(User("Charle", null, null))
        userList.add(User("Dora", null, null))



        val adapter = AudioMessAdapter(requireContext(), userList, {user:User -> onClick(user) })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return layout
    }

    fun onClick(user: User){
        Log.d("button was pressed", "button!@!")
    }

}
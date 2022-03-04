package edu.temple.grpr

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import org.json.JSONObject

class GroupFragment : Fragment(){

    var joining = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        joining = arguments?.getBoolean("JOIN_ACTION")!!
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout =  inflater.inflate(R.layout.fragment_group, container, false)
        val codeEditText = layout.findViewById<EditText>(R.id.codeEditText)
        val joinGroupButton = layout.findViewById<Button>(R.id.joinGroupButton)
        val leaveGroupButton = layout.findViewById<Button>(R.id.leaveGroupButton)

        codeEditText.visibility = if (joining) View.VISIBLE else View.INVISIBLE
        joinGroupButton.visibility = if (joining) View.VISIBLE else View.INVISIBLE
        leaveGroupButton.visibility = if (joining) View.INVISIBLE else View.VISIBLE


        joinGroupButton.setOnClickListener{
            (activity as GroupControlInterface).joinGroupFlow(codeEditText.text.toString())
            Navigation.findNavController(layout).popBackStack()
        }

        leaveGroupButton.setOnClickListener{
            (activity as GroupControlInterface).leaveGroupFlow(Helper.user.getGroupId(requireContext())!!)
            Navigation.findNavController(layout).popBackStack()
        }
        return layout
    }

    interface GroupControlInterface{
        fun joinGroupFlow(groupId : String)
        fun leaveGroupFlow(groupId: String)
    }
}
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

class GroupFragment : Fragment() {

    lateinit var codeEditText : EditText
    lateinit var joinGroupButton : Button
    lateinit var layout : View
    val grPrViewModel: GrPrViewModel by lazy {
        ViewModelProvider(requireActivity()).get(GrPrViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        layout =  inflater.inflate(R.layout.fragment_group, container, false)
        codeEditText = layout.findViewById(R.id.codeEditText)
        joinGroupButton = layout.findViewById(R.id.joinGroupButton)

        joinGroupButton.setOnClickListener{
            val groupId = codeEditText.text.toString()
            Helper.api.joinGroup(requireContext(), Helper.user.get(requireContext()), Helper.user.getSessionKey(requireContext())!!, groupId, object : Helper.api.Response{
                override fun processResponse(response: JSONObject) {
                    if(Helper.api.isSuccess(response)){
                        grPrViewModel.setGroupId(groupId)
                        goToDashBoard()
                    }
                    else{
                        Toast.makeText(requireContext(),Helper.api.getErrorMessage(response), Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
        return layout
    }

    private fun goToDashBoard(){
        val bundle = bundleOf("joined" to true)
        Navigation.findNavController(layout)
            .navigate(R.id.action_groupFragment_to_dashboardFragment, bundle)
    }
}
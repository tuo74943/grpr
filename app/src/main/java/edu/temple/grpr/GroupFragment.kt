package edu.temple.grpr

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

class GroupFragment : Fragment() {

    lateinit var codeEditText : EditText
    lateinit var joinGroupButton : Button
    lateinit var layout : View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        layout =  inflater.inflate(R.layout.fragment_group, container, false)
        codeEditText = layout.findViewById(R.id.codeEditText)
        joinGroupButton = layout.findViewById(R.id.joinGroupButton)
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
package edu.temple.grpr

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.Navigation
import org.json.JSONObject


class RegisterFragment : Fragment() {

    lateinit var layout : View
    lateinit var usernameEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var firstnameEditText: EditText
    lateinit var lastnameEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_register, container, false)

        usernameEditText = layout.findViewById(R.id.usernameEditText)
        passwordEditText = layout.findViewById(R.id.passwordEditText)
        firstnameEditText = layout.findViewById(R.id.firstnameEditText)
        lastnameEditText = layout.findViewById(R.id.lastnameEditText)


        layout.findViewById<Button>(R.id.registerButton).setOnClickListener {

            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val firstname = firstnameEditText.text.toString()
            val lastname = lastnameEditText.text.toString()

            Helper.api.createAccount(requireContext(), User(username, firstname, lastname)
                , password, object : Helper.api.Response{
                    override fun processResponse(response: JSONObject) {
                        if (Helper.api.isSuccess(response)) {
                            Helper.user.saveSessionData(requireContext(), response.getString("session_key"))
                            Helper.user.saveUser(requireContext(), User(username, firstname, lastname))
                            goToDashboard()
                        } else {
                            Toast.makeText(requireContext(), Helper.api.getErrorMessage(response), Toast.LENGTH_SHORT).show()
                        }
                    }

                })

        }
        return layout
    }

    private fun goToDashboard(){
        Navigation.findNavController(layout)
            .navigate(R.id.action_registerFragment_to_dashboardFragment)
    }
}
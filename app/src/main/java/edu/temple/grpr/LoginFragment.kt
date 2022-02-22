package edu.temple.grpr

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject

class LoginFragment : Fragment() {
    lateinit var usernameEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var layout : View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_login, container, false)

        layout.findViewById<TextView>(R.id.registerText).setOnClickListener {
            Navigation.findNavController(layout)
                .navigate(R.id.action_loginFragment_to_registerFragment)
        }

        usernameEditText = layout.findViewById(R.id.usernameEditText)
        passwordEditText = layout.findViewById(R.id.passwordEditText)

        layout.findViewById<Button>(R.id.loginButton).setOnClickListener {
            Helper.api.login(requireContext(), User(usernameEditText.text.toString(), null, null), passwordEditText.text.toString(), object: Helper.api.Response {
                override fun processResponse(response: JSONObject) {
                    if (Helper.api.isSuccess(response)) {
                        Helper.user.saveSessionData(requireContext(), response.getString("session_key"))
                        Helper.user.saveUser(requireContext(), User(
                            usernameEditText.text.toString(),
                            null,
                            null
                        ))
                        //Save FCM token and update it to the server
                        FirebaseMessaging.getInstance().token.addOnCompleteListener{
                            registerToken(it.result.toString())
                        }
                    } else {
                        Toast.makeText(requireContext(), Helper.api.getErrorMessage(response), Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //if user has a session key then that means the user is already logged in
        Helper.user.getSessionKey(requireContext())?.run {
            goToDashboard()
        }
    }


    private fun goToDashboard(){
        Navigation.findNavController(layout)
            .navigate(R.id.action_loginFragment_to_dashboardFragment)
    }

    private fun registerToken(token : String){
        // Get new FCM registration token
        Log.d("user", Helper.user.get(requireContext()).username)
        Log.d("session", Helper.user.getSessionKey(requireContext())!!)
        Helper.api.updateFCM(requireContext(), Helper.user.get(requireContext()), Helper.user.getSessionKey(requireContext())!!, token, object : Helper.api.Response{
            override fun processResponse(response: JSONObject) {
                if(Helper.api.isSuccess(response)){
                    Log.d("Token", response.toString())
                    Helper.user.saveFCMToken(requireContext(), token)
                }
                else{
                    Helper.api.getErrorMessage(response)
                }
                //if token is the same
                goToDashboard()
            }
        })
    }
}
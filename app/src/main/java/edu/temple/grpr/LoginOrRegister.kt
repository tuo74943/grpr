package edu.temple.grpr

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginOrRegister.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginOrRegister : Fragment() {
    lateinit var loginButton : Button
    lateinit var registerButton: Button
    lateinit var userNameEditText: EditText
    lateinit var passwordEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_login_or_register, container, false)
        loginButton = layout.findViewById(R.id.loginButton)
        registerButton = layout.findViewById(R.id.registerButton)
        userNameEditText = layout.findViewById(R.id.usernameEditText)
        passwordEditText = layout.findViewById(R.id.passwordEditText)
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginButton.setOnClickListener {
            (activity as loginOrRegisterInterface).loginButtonPressed(userNameEditText.text.toString(), passwordEditText.text.toString());
        }

        registerButton.setOnClickListener {
            (activity as loginOrRegisterInterface).registerButtonPressed()
        }
    }

    interface loginOrRegisterInterface{
        fun loginButtonPressed(username : String, password : String)
        fun registerButtonPressed()
    }
}
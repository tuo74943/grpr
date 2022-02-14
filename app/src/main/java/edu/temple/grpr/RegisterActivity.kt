package edu.temple.grpr

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    val usernameEditText by lazy {
        findViewById<EditText>(R.id.usernameEditText)
    }

    val fNameEditText by lazy {
        findViewById<EditText>(R.id.fNameEditText)
    }

    val lNameEditText by lazy {
        findViewById<EditText>(R.id.lNameEditText)
    }

    val passwordEditText by lazy {
        findViewById<EditText>(R.id.passwordEditText)
    }

    val button by lazy {
        findViewById<Button>(R.id.button)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

        button.setOnClickListener {
            val userName = usernameEditText.text.toString()
            val firstName = fNameEditText.text.toString()
            val lastName = lNameEditText.text.toString()
            val password = passwordEditText.text.toString()
            Log.d("Inside fields", userName + " " + firstName + " " + lastName + " " + password)
        }
    }
}
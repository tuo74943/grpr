package edu.temple.grpr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

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
            val userName = usernameEditText.text
            val firstName = fNameEditText.text
            val lastName = lNameEditText.text
            val password = passwordEditText.text
        }

    }
}
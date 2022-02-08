package edu.temple.grpr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

lateinit var registerIntent : Intent

class MainActivity : AppCompatActivity(), LoginOrRegister.loginOrRegisterInterface{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerIntent = Intent(this, RegisterActivity::class.java)

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .add(R.id.container, LoginOrRegister())
                .commit()
        }



    }

    override fun loginButtonPressed() {
        Toast.makeText(this, "login button pressed", Toast.LENGTH_LONG).show()
        Log.d("login button", "user clicked login button")
        //TODO send login request to API
    }

    override fun registerButtonPressed() {
        Toast.makeText(this, "register button pressed", Toast.LENGTH_LONG).show()
        Log.d("resgister button", "user clicked register button")
        startActivity(registerIntent)
    }
}
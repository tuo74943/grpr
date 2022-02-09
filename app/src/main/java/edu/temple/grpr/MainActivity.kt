package edu.temple.grpr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject


class MainActivity : AppCompatActivity(), LoginOrRegister.loginOrRegisterInterface, MainFragment.MainInterface{

    lateinit var registerIntent : Intent

    companion object {
        const val loginAPI = "https://kamorris.com/lab/grpr/account.php"
    }

    val volleyQueue : RequestQueue by lazy {
        Volley.newRequestQueue(this)
    }

    val mPrefs by lazy {
        getPreferences(MODE_PRIVATE)
    }

    lateinit var username : String
    lateinit var sessionKey : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerIntent = Intent(this, RegisterActivity::class.java)

        if(mPrefs.contains("session_key")){
            username = mPrefs.getString("username", "").toString()
            sessionKey = mPrefs.getString("session_key", "").toString()
            Log.d("MainFragment", "username " + username)
            Log.d("MainFragment", "session_key : " + sessionKey)
            supportFragmentManager.beginTransaction()
                .add(R.id.container, MainFragment())
                .commit()
        }
        else{
            supportFragmentManager.beginTransaction()
                .add(R.id.container, LoginOrRegister())
                .commit()
        }




    }

    override fun loginButtonPressed(username : String, password: String) {
        Toast.makeText(this, "login button pressed", Toast.LENGTH_LONG).show()
        Log.d("login button", "user clicked login button")
        loginRequest(username, password)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainFragment())
            .commit()
    }

    override fun registerButtonPressed() {
        Toast.makeText(this, "register button pressed", Toast.LENGTH_LONG).show()
        Log.d("Register button", "user clicked register button")
        startActivity(registerIntent)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, LoginOrRegister())
            .commit()
    }

    override fun logout() {
        //TODO add check to see if logout worked correctly
        logoutRequest()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, LoginOrRegister())
            .commit()
    }

    private fun loginRequest(username: String, password: String){
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            loginAPI,
            Response.Listener { response ->
                Log.i("Response", response.toString())
                val jsonObj = JSONObject(response)
                if(jsonObj.getString("status") == "ERROR"){
                    findViewById<TextView>(R.id.textView6).text = jsonObj.getString("message")
                }
                else{
                    val editor = mPrefs.edit()
                    editor.putString("session_key", jsonObj.getString("session_key"))
                        .putString("username", username)
                        .apply()
                    //TODO can change this to just use the jsonobject?
                    this.username = mPrefs.getString("username", "").toString()
                    sessionKey = mPrefs.getString("session_key", "").toString()
                    Toast.makeText(this,"Login Complete", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Log.i("response error:", "$error")
                error.printStackTrace()
            }){
            override fun getBodyContentType(): String {
                return "application/x-www-form-urlencoded"
            }

            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> =
                    HashMap()
                headers["Content-Type"] = "application/x-www-form-urlencoded"
                headers["Accept"] = "application/json"
                return headers
            }

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> =
                    HashMap()
                params["action"] = "LOGIN"
                params["username"] = username
                params["password"] = password
                return params
            }
        }
        volleyQueue.add(stringRequest)
    }


    private fun logoutRequest(){
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            loginAPI,
            Response.Listener { response ->
                Log.i("Response", response.toString())
                val jsonObj = JSONObject(response)
                if(jsonObj.getString("status") == "ERROR"){
                    Log.d("ERROR", jsonObj.getString("message"))
                }
                else{
                    val editor = mPrefs.edit()
                    editor.clear().apply()
                    Toast.makeText(this,"Logging out", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Log.i("response error:", "$error")
                error.printStackTrace()
            }){
            override fun getBodyContentType(): String {
                return "application/x-www-form-urlencoded"
            }

            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> =
                    HashMap()
                headers["Content-Type"] = "application/x-www-form-urlencoded"
                headers["Accept"] = "application/json"
                return headers
            }

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> =
                    HashMap()
                params["action"] = "LOGOUT"
                params["username"] = username
                params["session_key"] = sessionKey
                Log.d("INFO", username + " " + sessionKey)
                return params
            }
        }
        volleyQueue.add(stringRequest)
    }
}
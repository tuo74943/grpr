package edu.temple.grpr

import android.content.SharedPreferences
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

    val mPrefs by lazy {
        getPreferences(MODE_PRIVATE)
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

            val stringRequest: StringRequest = object : StringRequest(Method.POST,
                MainActivity.loginAPI,
                Response.Listener { response ->
                    val jsonObj = JSONObject(response)
                    if(jsonObj.getString("status") == "ERROR"){
                        findViewById<TextView>(R.id.textView5).text = jsonObj.getString("message")
                    }
                    else{
                        val editor = mPrefs.edit()
                        editor.putString("session_key", jsonObj.getString("session_key"))
                            .putString("username", jsonObj.getString("username"))
                            .putString("firstname", jsonObj.getString("firstname"))
                            .apply()
                        Toast.makeText(this,"Registration Completete", Toast.LENGTH_SHORT).show()
                        finish()
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
                    params["action"] = "REGISTER"
                    params["username"] = userName
                    params["firstname"] = firstName
                    params["lastname"] = lastName
                    params["password"] = password
                    return params
                }
            }


            val queue = Volley.newRequestQueue(this)

            queue.add(stringRequest)
        }

    }
}
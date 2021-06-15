package com.apm2021.rankcity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.*
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL



class LoginActivity : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.primaryColors, true)
        setContentView(R.layout.activity_login)

        setup()
        checkEula()
        session()
    }

    private fun setup() {

        val nameInputText = findViewById<TextInputEditText>(R.id.UserEmailInputText)
        val passInputText = findViewById<TextInputEditText>(R.id.PasswordInputText)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            if (nameInputText.text?.isNotEmpty() == true && passInputText.text?.isNotEmpty() == true) {
                loginCoroutine(nameInputText.text.toString(), passInputText.text.toString())
            } else {
                showAlert("Missing User/Password")
            }
        }

        val registerButton = findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener {
            showRegister()
        }

        val loginButtonGoogle = findViewById<Button>(R.id.loginButtonGoogle)
        loginButtonGoogle.setOnClickListener {
            googleLoginCoroutine(this)
        }
    }

    private fun googleLoginCoroutine(activity: Activity)=runBlocking {
        googleLogin(activity)
    }

    suspend fun googleLogin(activity: Activity)=coroutineScope {
        launch{
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(activity, googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
    }

    private fun loginCoroutine(name: String, pass: String)=runBlocking {
        login(name, pass)
    }

    suspend fun login(name: String, pass: String)=coroutineScope{
        launch {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(
                name,
                pass
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    //val triple = getUser(name); // TODO Retrieve required info here
                    val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                    prefs.putString("email", it.result?.user?.email)
                    prefs.apply()
                    showMain(it.result?.user?.email ?: "")
                } else {
                    showAlert("Contraseña incorrecta")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                                prefs.putString("email", it.result?.user?.email)
                                prefs.apply()
                                showMain(account.email ?: "")
                            } else {
                                showAlert( "Falló la comunicación con Firebase")
                            }
                        }
                }
            } catch(e: ApiException) {
                showAlert("Something Wrong")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val loginLayout = findViewById<LinearLayout>(R.id.loginLayout)
        loginLayout.visibility = View.VISIBLE
    }

    private fun session() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val loginLayout = findViewById<LinearLayout>(R.id.loginLayout)

        if (email != null) {
            loginLayout.visibility = View.INVISIBLE
            showMain(email)
        }
    }

    private fun getUser(userid: String): Triple<Any, Any, Any> {
        val result = URL("http://localhost:5000/users/" + userid).readText()
        val jsonObject = JSONObject(result)
        try {
            return Triple(jsonObject.get("username"), jsonObject.get("name"), jsonObject.get("email"))
        } catch (e: JSONException) {
            return Triple("null","null","null")
        }
    }

    private fun showEditProfile(email: String) {
        val profileEditorIntent = Intent(this, RegisterActivity::class.java).apply {
            putExtra("email", email)
            putExtra("mandatory", true)
        }
        startActivity(profileEditorIntent)
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        exitDialog()
    }

    private fun exitDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¿Estás seguro de que quieres salir de RankCity?")

        // Set up the buttons
        builder.setPositiveButton("Sí") { dialog, i ->
            finish()
        }

        builder.setNegativeButton("No") { dialog, which ->
            dialog.cancel()
        }
        builder.show()
    }

    private fun showMain(email: String) {
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(mainIntent)
    }

    private fun showRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun showAlert(msg: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(msg)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun checkEula() {
        val preferences = applicationContext
            .getSharedPreferences("com.apm2021.rankcity", Context.MODE_PRIVATE)
        val eulaAccepted = preferences.getBoolean("eulaAccepted", false)
        if (!eulaAccepted) {
            val intent = Intent(this, EulaActivity::class.java)
            val bundle = Bundle()
            val eula = R.raw.eula
            bundle.putInt("eula", eula)
            intent.putExtras(bundle)
            startActivityForResult(intent, 1)
        }
    }
}

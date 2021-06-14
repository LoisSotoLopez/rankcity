package com.apm2021.rankcity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.*
import kotlinx.coroutines.*
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.primaryColors, true)
        setContentView(R.layout.activity_login)

        setup()
        resetPreferences()
        checkEula()
    }

    private fun onActivityResult() {
        session()
    }

    private fun setup() {

        val nameInputText = findViewById<TextInputEditText>(R.id.UserEmailInputText)
        val passInputText = findViewById<TextInputEditText>(R.id.PasswordInputText)
        // Pulsar boton login nos lleva al inicio de la app
        val loginButton = findViewById<Button>(R.id.loginButton)

        // set on-click listener
        loginButton.setOnClickListener {
            if (nameInputText.text?.isNotEmpty() == true && passInputText.text?.isNotEmpty() == true) {
                loginCoroutine(nameInputText.text.toString(), passInputText.text.toString())
            } else {
                Toast.makeText(this, "Missing User/Password", Toast.LENGTH_SHORT).show()
            }
        }

        val registerButton = findViewById<Button>(R.id.registerButton)
        // set on-click listener
        registerButton.setOnClickListener {
            if (nameInputText.text?.isNotEmpty() == true && passInputText.text?.isNotEmpty() == true) {
                signInCoroutine(nameInputText.text.toString(),passInputText.text.toString())
            } else {
                Toast.makeText(this, "User/Password missing", Toast.LENGTH_SHORT).show()
            }
        }


        val loginButtonGoogle = findViewById<Button>(R.id.loginButtonGoogle)

        // set on-click listener
        loginButtonGoogle.setOnClickListener {
            googleLoginCoroutine(this)
        }
    }


    private fun signInCoroutine(name: String, pass: String)=runBlocking{
        signIn(name, pass)
    }

    suspend fun signIn(name: String, pass: String)=coroutineScope {
        try {
            launch {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    name,
                    pass).addOnCompleteListener {
                    //if (it.isSuccessful && postUser(name)) {
                    // TODO: Add user entry on backend
                    // review parameters
                    val email = name
                    val username = name.split("@")[0]
                    addUserAPI(username, email, email, true)
                    if (it.isSuccessful) {
                        showEditProfile(it.result?.user?.email ?: "")
                    } else {
                        val errorCode = (it.getException() as FirebaseAuthException).errorCode
                        when (errorCode) {
                            "ERROR_INVALID_EMAIL" -> {
                                showAlert("Ese correo no tiene buena pinta.\n" +
                                        "Escribe algo como esot@lootro.com")
                            }
                            "ERROR_EMAIL_ALREADY_IN_USE" -> {
                                showAlert("Ese correo ya ha sido usado para crear otra cuenta.")
                            }
                            "ERROR_WEAK_PASSWORD" -> {
                                showAlert("Esa contraseña es demasiado débil.")
                            }
                            else ->
                                showAlert("Algo no fue bien")
                        }
                    }
                }
            }
        } catch (e: FirebaseAuthWeakPasswordException) {
            showAlert("Esa contraseña es muy débil.")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            showAlert("Eso que has puesto no parece un correo. Pon algo tipo esto@lo-otro.com")
        } catch (e: FirebaseAuthUserCollisionException) {
            showAlert("Ese correo ya tiene una cuenta asociada")
        } catch (e: java.lang.Exception) {
            showAlert("No se pudo registrar")
        }
    }

    private fun addUserAPI(username: String, name: String, email: String, accept_eula: Boolean) {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
//        val url = "https://rankcity-app.herokuapp.com/users"
        val url = "http://192.168.1.74:5000/users"

        val jsonObject = JSONObject()
        jsonObject.put("username", username)
        jsonObject.put("name", name)
        jsonObject.put("email", email)
        jsonObject.put("accept_eula", accept_eula)

        val jsonRequest = JsonObjectRequest(url, jsonObject, {
                response ->
            val sharedPreferences: SharedPreferences = this.getSharedPreferences("user_data_file", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("userId", response.getString("username"))
            editor.putString("name", response.getString("name"))
            editor.putString("email", response.getString("email"))
            editor.putBoolean("accept_eula", response.getBoolean("accept_eula"))
            editor.apply()
            editor.commit()
        }, {})
        // Add the request to the RequestQueue.
        queue.add(jsonRequest)
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
                    getUserFrom_API(name); // TODO Retrieve required info here
                    showMain(it.result?.user?.email ?: "")
                } else {
                    showAlert("Contraseña incorrecta")
                }
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
                                showMain(account.email ?: "")
                            } else {
                                showAlert( "Falló la comunicación con Firebase")
                            }
                        }
                }
            } catch(e: ApiException) {

            }
        }
    }

    private fun getUserFrom_API(userid: String) {
        val requestQueue = Volley.newRequestQueue(this)
        val userid_aux = userid.split("@")[0]
//        val url = "https://rankcity-app.herokuapp.com/users/$userid_aux"
        val url = "http://192.168.1.74:5000/users/$userid_aux"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
//                println("RESPONSEEEEEEEEEEEEEE"+response)
                val sharedPreferences: SharedPreferences = this.getSharedPreferences("user_data_file", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString("userId", response.getString("username"))
                editor.putString("name", response.getString("name"))
                editor.putString("email", response.getString("email"))
                editor.putBoolean("accept_eula", response.getBoolean("accept_eula"))
                editor.apply()
                editor.commit()
//                println(sharedPreferences.getString("userId", "holahola"))
            },
            { error ->
                // TODO: Handle error
                println("ERROR API CONECCTION")
            }
        )
        requestQueue.add(jsonObjectRequest)
    }

    private fun showAlert(msg: String) {
        //TODO check if AlertDialog is of proper type
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(msg)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showMain(email: String) {
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(mainIntent)
    }

    private fun showEditProfile(email: String) {
        val profileEditorIntent = Intent(this, ProfileEditorActivity::class.java).apply {
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

    // TODO : this function has testing purposes. Delete before prod.
    private fun resetPreferences() {
        var preferences = applicationContext
            .getSharedPreferences("com.apm2021.rankcity", Context.MODE_PRIVATE)
        preferences.edit().remove("email").apply()
    }
}

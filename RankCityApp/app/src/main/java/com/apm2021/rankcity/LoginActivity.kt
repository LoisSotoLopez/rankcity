package com.apm2021.rankcity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.*
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset


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
        // Pulsar boton login nos lleva al inicio de la app
        val loginButton = findViewById<Button>(R.id.loginButton)

        // set on-click listener
        loginButton.setOnClickListener {
            if (nameInputText.text?.isNotEmpty() == true && passInputText.text?.isNotEmpty() == true) {
                loginCoroutine(nameInputText.text.toString(), passInputText.text.toString())
            } else {
                showAlert("Missing User/Password")
            }
        }

        val registerButton = findViewById<Button>(R.id.registerButton)
        // set on-click listener
        registerButton.setOnClickListener {
            if (nameInputText.text?.isNotEmpty() == true && passInputText.text?.isNotEmpty() == true) {
                signInCoroutine(nameInputText.text.toString(),passInputText.text.toString())
            } else {
                showAlert("User/Password missing")
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

    private fun getUser(userid: String): Triple<Any, Any, Any> {
        val result = URL("http://localhost:5000/users/" + userid).readText()
        val jsonObject = JSONObject(result)
        try {
            return Triple(jsonObject.get("username"), jsonObject.get("name"), jsonObject.get("email"))
        } catch (e: JSONException) {
            return Triple("null","null","null")
        }
    }

    private fun postUser(userid: String) : Boolean{
        val conn = URL("http://localhost:5000/users/" + userid).openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.connectTimeout = 300000
        conn.doOutput = true

        val message = " {\n" +
                "                \"username\": " + userid +",\n" +
                "                \"name\": " + userid +",\n" +
                "                \"email\": " + userid +"\n" +
                "            }"
        val postData: ByteArray =
                message.toByteArray(Charset.forName("UTF-8"))

        conn.setRequestProperty("charset", "utf-8")
        conn.setRequestProperty("Content-length", postData.size.toString())
        conn.setRequestProperty("Content-Type", "application/json")

        try {
            val outputStream: DataOutputStream = DataOutputStream(conn.outputStream)
            outputStream.write(postData)
            outputStream.flush()
        } catch (exception: Exception) {

        }

        return (conn.responseCode == HttpURLConnection.HTTP_OK)
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
}

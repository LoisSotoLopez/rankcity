package com.apm2021.rankcity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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
        launch {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                name,
                pass).addOnCompleteListener {
                //if (it.isSuccessful && postUser(name)) { %TODO: Add user entry on backend
                if (it.isSuccessful && true) {
                    showMain(it.result?.user?.email ?: "", ProviderType.BASIC )
                } else {
                    showAlert("No se pudo registrar")
                }
            }
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
                    showMain(it.result?.user?.email ?: "", ProviderType.BASIC)
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
        val provider = prefs.getString("provider", null)
        val loginLayout = findViewById<LinearLayout>(R.id.loginLayout)

        if (email != null && provider != null) {
            loginLayout.visibility = View.INVISIBLE
            showMain(email, ProviderType.valueOf(provider))
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
                                showMain(account.email ?: "", ProviderType.GOOGLE)
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

    private fun showMain(email: String, provider: ProviderType) {
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(mainIntent)
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
        preferences.edit().remove("provider").apply()
    }
}

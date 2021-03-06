package com.apm2021.rankcity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.io.DataOutputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val emailInputText = findViewById<TextInputEditText>(R.id.UserEmailInputText)
        val passInputText = findViewById<TextInputEditText>(R.id.PasswordInputText)

        val nicknameInputText = findViewById<TextInputEditText>(R.id.editNickInputText)
        val hometownInputText = findViewById<TextInputEditText>(R.id.hometownInputText)

        val acceptButton = findViewById<Button>(R.id.acceptRegisterButton)
        acceptButton.setOnClickListener {
            // TODO either add image parameter or select context values from edit_users scope
            if (emailInputText.text?.isNotEmpty() == true && passInputText.text?.isNotEmpty() == true) {
                signInCoroutine(
                    emailInputText.text.toString(),
                    passInputText.text.toString(),
                    nicknameInputText.text.toString(),
                    hometownInputText.text.toString())
            } else {
                showAlert("User/Password missing")
            }
        }

        val cancelButton = findViewById<Button>(R.id.cancelRegisterButton)
        cancelButton.setOnClickListener {
            cancel()
        }

    }

    private fun signInCoroutine(
        email: String,
        pass: String,
        nick: String,
        hometown: String
    )= runBlocking{
        signIn(email, pass, nick, hometown)
    }

    suspend fun signIn(
        email: String,
        pass: String,
        nick: String,
        hometown: String
    )= coroutineScope {
        try {
            launch {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    email,
                    pass).addOnCompleteListener {
                    //if (it.isSuccessful && postUser(name)) {
                    // TODO: Add user entry on backend
                    if (!it.isSuccessful) {
                        val errorCode = (it.getException() as FirebaseAuthException).errorCode
                        when (errorCode) {
                            "ERROR_INVALID_EMAIL" -> {
                                showAlert("Ese correo no tiene buena pinta.\n" +
                                        "Escribe un correo válido.")
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
                    } else {
                        addUserAPI(nick, email, hometown, true)
                        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                        prefs.putString("email", it.result?.user?.email)
                        prefs.apply()
                        showMain(email)
                    }
                }
            }
        } catch (e: FirebaseAuthWeakPasswordException) {
            showAlert("Esa contraseña es muy débil.")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            showAlert("Eso que has puesto no parece un correo. Pon algo similar a esto@lo-otro.com")
        } catch (e: FirebaseAuthUserCollisionException) {
            showAlert("Ese correo ya tiene una cuenta asociada")
        } catch (e: Exception) {
            showAlert("No se pudo registrar")
        }
    }

    private fun addUserAPI(username: String, email: String, town: String, accept_eula: Boolean) {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://rankcity-app.herokuapp.com/users"
//        val url = "http://192.168.1.38:5000/users"

        val jsonObject = JSONObject()
        jsonObject.put("username", username)
//        jsonObject.put("name", name)
        jsonObject.put("email", email)
        jsonObject.put("town", town)
        jsonObject.put("accept_eula", accept_eula)
//        jsonObject.put("image", image)

        val jsonRequest = JsonObjectRequest(url, jsonObject, {
                response ->
            val sharedPreferences: SharedPreferences = this.getSharedPreferences("user_data_file", MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("username", response.getString("username"))
            editor.putString("email", response.getString("email"))
            editor.putString("town", response.getString("town"))
            editor.putBoolean("accept_eula", response.getBoolean("accept_eula"))
//            editor.putBoolean("image", response.getBoolean("image"))
            editor.apply()
            editor.commit()
        }, {})
        // Add the request to the RequestQueue.
        queue.add(jsonRequest)
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        backToLoginDialog()
    }

    private fun backToLoginDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¿Seguro que quieres volver al login?")

        // Set up the buttons
        builder.setPositiveButton("Sí") { dialog, i ->
            showLogin()
        }

        builder.setNegativeButton("No") { dialog, which ->
            dialog.cancel()
        }
        builder.show()
    }

    private fun cancel(){
        backToLoginDialog()

    }

    private fun showAlert(msg: String) {
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

    private fun showLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

}
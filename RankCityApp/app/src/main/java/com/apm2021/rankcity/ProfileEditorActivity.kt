package com.apm2021.rankcity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class ProfileEditorActivity : AppCompatActivity() {

    private val REQUEST_GALLERY = 1001
    private val REQUEST_CAMERA = 1002
    var photo: Uri? = null
    lateinit var imgPhoto: ImageView
    lateinit var cameraButton: FloatingActionButton
    private var dialogOut : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_editor)

        val bundle = intent.extras
        val email = bundle?.getString("email") ?: ""
        var mandatory = bundle?.getBoolean( "mandatory") ?: false


        val nicknameInputText = findViewById<TextInputEditText>(R.id.editNickInputText)
        // TODO asign val from backend

        val hometownInputText = findViewById<TextInputEditText>(R.id.hometownInputText)
        // TODO asign val from backend

        val acceptButton = findViewById<Button>(R.id.acceptProfileEditionButton)
        acceptButton.setOnClickListener {
            // TODO either add image parameter or select context values from edit_users scope
            accept(nicknameInputText, hometownInputText, email,  mandatory)
        }

        val cancelButton = findViewById<Button>(R.id.cancelProfileEditionButton)
        cancelButton.setOnClickListener {
            cancel(nicknameInputText, hometownInputText, email, mandatory)
        }

    }

    private fun cancel(
        nicknameInput: TextInputEditText,
        hometownInput: TextInputEditText,
        email: String,
        mandatory: Boolean){
        if (!mandatory) {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        } else {
            var editNick : String = nicknameInput.text.toString()
            var hometown : String = hometownInput.text.toString()
            if (editNick == null) editNick = email
            if (hometown == null) hometown = ""
            showCancelAlert(
                "¿Estas seguro de que quieres cancelar?\n" +
                        "Se usará tu correo como nombre de usuario.",
                    nicknameInput,
                    hometownInput,
                    email)
        }
    }

    private fun accept(
        nicknameInput: TextInputEditText,
        hometownInputText: TextInputEditText,
        email: String,
        mandatory: Boolean)
    {
        if ( (nicknameInput.text?.isNotEmpty() == true && hometownInputText.text?.isNotEmpty() == true) || !mandatory) {
            // TODO store in backend
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        } else {
            showAlert("Faltan algunos campos")
        }
    }

    private fun showCancelAlert(
        msg: String,
        nicknameInput: TextInputEditText,
        hometownInputText: TextInputEditText,
        email: String,
    ) {
        //TODO check if AlertDialog is of proper type
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(msg)
        builder.setPositiveButton("Aceptar", DialogInterface.OnClickListener {dialog, id ->
            this.accept(nicknameInput, hometownInputText, email, false)
        })
        builder.setNegativeButton("Cancelar", null)
        val dialog: androidx.appcompat.app.AlertDialog = builder.create()
        dialog.show()
    }

    private fun showAlert(msg: String) {
        //TODO check if AlertDialog is of proper type
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(msg)
        builder.setPositiveButton("Aceptar", null)
        val dialog: androidx.appcompat.app.AlertDialog = builder.create()
        dialog.show()
    }

}
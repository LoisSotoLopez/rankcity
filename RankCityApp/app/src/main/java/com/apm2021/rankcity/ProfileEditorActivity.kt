package com.apm2021.rankcity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class ProfileEditorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_profile_editor)

        val bundle = intent.extras
        val email = bundle?.getString("email") ?: ""

        val nicknameInputText = findViewById<TextInputEditText>(R.id.editNickInputText)
        // TODO asign val from backend

        val hometownInputText = findViewById<TextInputEditText>(R.id.hometownInputText)
        // TODO asign val from backend

        val acceptButton = findViewById<Button>(R.id.acceptProfileEditionButton)
        acceptButton.setOnClickListener {
            // TODO either add image parameter or select context values from edit_users scope
            accept(nicknameInputText, hometownInputText, email)
        }

        val cancelButton = findViewById<Button>(R.id.cancelProfileEditionButton)
        cancelButton.setOnClickListener {
            cancel()
        }

    }

    private fun cancel(){
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)

    }

    private fun accept(
        nicknameInput: TextInputEditText,
        hometownInputText: TextInputEditText)
    {
        if ( (nicknameInput.text?.isNotEmpty() == true && hometownInputText.text?.isNotEmpty() == true)) {
            // TODO store in backend
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        } else {
            showAlert("Faltan algunos campos")
        }
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
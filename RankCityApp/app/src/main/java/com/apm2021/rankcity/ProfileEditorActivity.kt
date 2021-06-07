package com.apm2021.rankcity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_editor)

        val cameraButton = findViewById<Button>(R.id.editProfileEditImgButton) as FloatingActionButton
        cameraButton.setOnClickListener {
            dialog()
        }

        val nicknameInputText = findViewById<TextInputEditText>(R.id.editNickInputText)
        // TODO asign val from backend

        val hometownInputText = findViewById<TextInputEditText>(R.id.hometownInputText)
        // TODO asign val from backend

        val acceptButton = findViewById<Button>(R.id.acceptProfileEditionButton)
        acceptButton.setOnClickListener {
            // TODO either add image parameter or select context values from edit_users scope
            edit_user(nicknameInputText, hometownInputText)
        }

        val cancelButton = findViewById<Button>(R.id.cancelProfileEditionButton)
        cancelButton.setOnClickListener {
            cancel(nicknameInputText, hometownInputText)
        }

    }

    private fun cancel(nicknameInput: TextInputEditText, hometownInputText: TextInputEditText){
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
    }

    private fun edit_user(nicknameInput: TextInputEditText, hometownInputText: TextInputEditText){
        if (nicknameInput.text?.isNotEmpty() == true && hometownInputText.text?.isNotEmpty() == true) {
            // TODO handle profile image
            // TODO store in backend
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        } else {
            Toast.makeText(this, "Missing Fields", Toast.LENGTH_SHORT).show()
        }
    }

    //Al pulsar botón para abrir cámara comprobamos permisos
    private fun openCameraClick(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (this.let {
                    ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.CAMERA
                    )
                } == PackageManager.PERMISSION_DENIED || this.let {
                    ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                } == PackageManager.PERMISSION_DENIED){
                val cameraPermits = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(cameraPermits, REQUEST_CAMERA)
            }else{
                openCamera()
            }
        }else{
            openCamera()
        }
    }

    //Abrir camara de móvil
    private fun openCamera(){
        val value = ContentValues()
        value.put(MediaStore.Images.Media.TITLE, "Nueva imagen")
        photo =
            this.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, value)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photo)
        startActivityForResult(cameraIntent, REQUEST_CAMERA)
    }

    //Al pulsar botón para abrir galería comprobamos permisos
    private fun openGalleryClick(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.let {
                    ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                } == PackageManager.PERMISSION_DENIED){
                val archivePermits = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(archivePermits, REQUEST_GALLERY)
            }else{
                showGallery()
            }
        }else{
            showGallery()
        }
    }

    //Abrir galería de fotos del móvil
    private fun showGallery(){
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, REQUEST_GALLERY)

    }

    //Diálogo para elegir si abrir cámara o galería
    private fun dialog() {
        val opciones = arrayOf<CharSequence>("Abrir cámara", "Abrir galería", "Cancelar")
        val alertOpciones = AlertDialog.Builder(this)
        alertOpciones.setTitle("Seleccione una Opción")
        alertOpciones.setItems(
            opciones
        ) { dialogInterface, i ->
            if (opciones[i] == "Abrir cámara") {
                openCameraClick()
            } else {
                if (opciones[i] == "Abrir galería") {
                    openGalleryClick()
                } else {
                    dialogInterface.dismiss()
                }
            }
        }
        alertOpciones.show()
    }

    //Establecer foto de perfil cogiendo la foto de la cámara o de la galería
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_GALLERY){
            imgPhoto.setImageURI(data?.data)
        }
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CAMERA){
            imgPhoto.setImageURI(photo)
        }
    }

    //Comprobar si se aceptan permisos
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_GALLERY ->{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    showGallery()
                }else{
                    Toast.makeText(this, "No se puede abrir la galería", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_CAMERA ->{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openCamera()
                }else{
                    Toast.makeText(this, "No se puede abrir la cámara", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
package com.apm2021.rankcity.ui.profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.apm2021.rankcity.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.lang.reflect.Type
import kotlin.concurrent.thread


class ProfileFragment : Fragment() {

    private val REQUEST_GALLERY = 1001
    private val REQUEST_CAMERA = 1002
    var photo: Uri? = null
    lateinit var imgPhoto: ImageView
    lateinit var cameraButton: FloatingActionButton
    var routes = ArrayList<Route>()
    private var currentThread: Thread? = null
    private val mutex = Mutex()
    private var userid = String()

    suspend fun preloadData() {
        if (routes.isEmpty()) {
            mutex.withLock {
                if (currentThread == null || !currentThread!!.isAlive) {
                    currentThread = thread(start = true) {
                        val sharedPreferences: SharedPreferences? =
                            this.activity?.getSharedPreferences("user_data_file", Context.MODE_PRIVATE)
                        if (sharedPreferences != null) {
                            userid = sharedPreferences.getString("userId","").toString()
                        }
                        getUserRoutesFrom_API(userid)
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_profile, container, false)
        imgPhoto = root.findViewById(R.id.imgPhoto) as ImageView
        cameraButton = root.findViewById<Button>(R.id.cameraButton) as FloatingActionButton
        cameraButton.setOnClickListener {
            dialog()
        }
        return root
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)

        val textView = requireView().findViewById<View>(R.id.ProfileUsername) as TextView
        textView.text = userid
//        val datesList = Datasource_Profile(this).getDatesList()
//        GlobalScope.launch {
//            getUserRoutesFrom_API(userid)
//        }
        val datesList = routes

        val recyclerView = itemView.findViewById<RecyclerView>(R.id.recycler_view)
        //GlobalScope.launch {
        recyclerView.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView
            val datesList = arrayOf<String>("HOLA", "CHAO")
            adapter = ProfileAdapter(datesList)
        }
        //}
    }

    private fun getUserRoutesFrom_API(userid: String) = runBlocking {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "http://192.168.1.74:5000/routes/user/$userid"
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                println("RUTAS"+response)
                try {
                    val gsonBuilder = GsonBuilder()
                    gsonBuilder.registerTypeAdapter(
                        Route::class.java,
                        RouteDeserializer()
                    )
                    routes.addAll(
                        gsonBuilder.create().fromJson(
                            response.toString(),
                            Array<Route>::class.java
                        ))
                    println(routes)
//                    for (i in 0 until response.length()) {
//                        val route: JSONObject = response.getJSONObject(i)
//                        routes.add(route)
//                    }
                } catch (e: Exception) {

                }
            },
            { error ->
                // TODO: Handle error
                println("ERROR API CONECCTION")
            }
        )
        requestQueue.add(jsonArrayRequest)
    }

    class RouteDeserializer : JsonDeserializer<Route> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): Route {
            val route = json as JsonObject

            var id: Int = -1
            if (route.has("level")) {
                id = route["id"].asInt
            }

            var title = ""
            if (route.has("title")) {
                title = route["title"].asString
            }

            var date = ""
            if (route.has("date")) {
                date = route["date"].asString
            }

            var user = ""
            if (route.has("user")) {
                user = route["user"].asString
            }

            var time = ""
            if (route.has("time")) {
                time = route["time"].asString
            }

            var score = 0
            if (route.has("score")) {
                score = route["score"].asInt
            }

            val streets_json = route["streets"].asJsonArray

            val streets = ArrayList<Street>()
            for (street in streets_json) {
                streets.add(
                    Street(
                        street.asJsonObject["name"].asString,
                        street.asJsonObject["score"].asInt
                    )
                )
            }

            return Route(
                id,
                title,
                date,
                time,
                user,
                score,
                streets
            )
        }
    }

    //Al pulsar botón para abrir cámara comprobamos permisos
    private fun openCameraClick(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (context?.let {
                    ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.CAMERA
                    )
                } == PackageManager.PERMISSION_DENIED || context?.let {
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
            activity?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, value)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photo)
        startActivityForResult(cameraIntent, REQUEST_CAMERA)
    }

    //Al pulsar botón para abrir galería comprobamos permisos
    private fun openGalleryClick(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context?.let {
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
        val alertOpciones = AlertDialog.Builder(context)
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
                    Toast.makeText(context, "No se puede abrir la galería", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_CAMERA ->{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openCamera()
                }else{
                    Toast.makeText(context, "No se puede abrir la cámara", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
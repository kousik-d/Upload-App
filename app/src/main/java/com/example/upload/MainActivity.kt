package com.example.upload

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.net.URI
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private var imageuri : Uri? = null
    private var c =1
    private lateinit var imageview: ImageView
    private lateinit var uploadbtn : Button
    private lateinit var select : Button
    private lateinit var progress : ProgressBar
    lateinit var activityResultlauncher : ActivityResultLauncher<Intent>
    lateinit var showupload : Button


    val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    val dbreferance = database.reference.child("myUsers")


    val firebasestorage : FirebaseStorage = FirebaseStorage.getInstance()
    val storagerefrence : StorageReference = firebasestorage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        uploadbtn = findViewById(R.id.uploadbtn)
        imageview = findViewById(R.id.image)
        select = findViewById(R.id.select)
        progress = findViewById(R.id.progressBar)
        showupload = findViewById(R.id.showup)

        registeractivitylauncher();

        select.setOnClickListener {
            selectimage();
        }
        uploadbtn.setOnClickListener {
            uploadimage()
            uploadbtn.isClickable = true
            progress.visibility = View.INVISIBLE

        }
        showupload.setOnClickListener{
            val nextintent = Intent(applicationContext,showuploadsActivity::class.java)
            startActivity(nextintent)
        }
    }

    private fun registeractivitylauncher() {
        activityResultlauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback {

                val resultcode = it.resultCode
                val imagedata = it.data
                if(resultcode == RESULT_OK && imagedata !=null){

                    imageuri = imagedata.data

                    //show image on imageview with picasso

                    imageuri?.let {
                        Picasso.get().load(it).into(imageview)
                    }
                }
        })
    }


    private fun selectimage() {

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }else {

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultlauncher.launch(intent)
        }
    }
    fun addtodatabase(url : String){
       val id : String = dbreferance.push().key.toString()

        val user = Users(url)
        dbreferance.child(id).setValue(user).addOnCompleteListener { task ->

            if(task.isSuccessful){
                Toast.makeText(applicationContext,"Added to Real Time DB",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(applicationContext,"Not Added to Real Time DB",Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode==1 && grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultlauncher.launch(intent)
        }
    }
    fun uploadimage(){
        uploadbtn.isClickable = false
        progress.visibility = View.VISIBLE

        // generate Random image name using UUID

        val imageName = UUID.randomUUID().toString()
        val imagereference = storagerefrence.child("images").child(imageName)

        imageuri?.let {
            imagereference.putFile(it).addOnSuccessListener {

                Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show()

                //downloadable url to download again and save it in real time database

                val myuploadimageref = storagerefrence.child("images").child(imageName)
                myuploadimageref.downloadUrl.addOnSuccessListener {

                    val imageurl = it.toString()

                    addtodatabase(imageurl)

                }.addOnFailureListener {

                    Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    }

}
package com.example.termproject.termproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.example.termproject.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage : FirebaseStorage
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    var selectedPicture: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerLauncher()

        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage
        val intentName = intent.getStringExtra("name")
        val intentEmail = intent.getStringExtra("email")

        if(intentEmail?.isNotEmpty() == true){

            val actionBar = supportActionBar
            actionBar?.setDisplayHomeAsUpEnabled(true)
            actionBar?.setDisplayHomeAsUpEnabled(true)

            firestore.collection("Users").addSnapshotListener{value, error ->
                if(error !=null){
                    Toast.makeText(this,error.localizedMessage,Toast.LENGTH_SHORT).show()

                }else{
                    if(value != null){
                        if(!value.isEmpty){
                            val documents = value.documents
                            for (document in documents){
                                val email = document.get("email") as String
                                if(email == intentEmail){
                                    binding.adsoyadText.setText(document.get("adsoyad") as String)
                                    binding.bolumText.setText(document.get("bolum")as String)
                                    binding.telefonText.setText(document.get("telefon") as String)
                                    binding.sinifText.setText(document.get("sinif") as String)
                                    binding.uzaklKText.setText(document.get("uzaklik") as String)
                                    binding.sureText.setText( document.get("sure") as String)

                                    Picasso.get().load(document.get("profilResmi").toString()).into(binding.imageView2)

                                    binding.profilButton.isVisible = false
                                    binding.host.isClickable = false
                                    binding.none.isClickable = false
                                    binding.guest.isClickable = false
                                    val durum = document.get("durum") as String
                                    if(durum == "guest"){
                                        binding.guest.isChecked = true
                                    }else if(durum == "host"){
                                        binding.host.isChecked = true
                                    }else if(durum == "none"){
                                        binding.none.isChecked = true
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        else if(intentName?.isNotEmpty() == true){
            firestore.collection("Users").addSnapshotListener{value, error ->
                if(error != null){
                    Toast.makeText(this,error.localizedMessage,Toast.LENGTH_SHORT).show()
                }else{
                    if(value != null){
                        if(!value.isEmpty){
                            val documents = value.documents
                            for (document in documents){
                                val name = document.get("adsoyad") as String
                                if(name == intentName){
                                    binding.adsoyadText.setText(name)
                                    binding.adsoyadText.isFocusable = false
                                    binding.bolumText.setText(document.get("bolum")as String)
                                    binding.bolumText.isFocusable = false
                                    binding.telefonText.setText(document.get("telefon") as String)
                                    binding.telefonText.isFocusable = false
                                    binding.sinifText.setText(document.get("sinif") as String)
                                    binding.sinifText.isFocusable = false
                                    binding.uzaklKText.setText(document.get("uzaklik") as String)
                                    binding.uzaklKText.isFocusable = false
                                    binding.sureText.setText( document.get("sure") as String)
                                    binding.sureText.isFocusable = false

                                    Picasso.get().load(document.get("profilResmi").toString()).into(binding.imageView2)

                                    binding.kameraButton.isVisible = false
                                    binding.galeriButton.isVisible = false
                                    binding.durum.isVisible = false
                                    binding.profilButton.isVisible = false

                                }
                            }
                        }
                    }
                }
            }
        }

        binding.galeriButton.setOnClickListener(){
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    111
                )
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intent)


            }
        }
        binding.kameraButton.setOnClickListener(){
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            activityResultLauncher.launch(cameraIntent)
        }

        binding.profilButton.setOnClickListener{
            val adsoyad = binding.adsoyadText.text.toString()
            val bolum = binding.bolumText.text.toString()
            val telefon = binding.telefonText.text.toString()
            val sinif = binding.sinifText.text.toString()
            val uzaklik = binding.uzaklKText.text.toString()
            val durum = binding.durum
            val sure = binding.sureText.text.toString()
            var durumtext = ""

            val reference = storage.reference
            val imageReference = reference.child("ProfilePictures").child(adsoyad)
            if(selectedPicture != null ){
                imageReference.putFile(selectedPicture!!).addOnSuccessListener {
                    imageReference.downloadUrl.addOnSuccessListener {
                        val downloadUrl = it.toString()
                        if(adsoyad.isNotEmpty() && bolum.isNotEmpty() && telefon.isNotEmpty() && sinif.isNotEmpty() && uzaklik.isNotEmpty() && sure.isNotEmpty() && durum.checkedRadioButtonId != -1){
                            if (auth.currentUser != null){
                                if (binding.host.isChecked){
                                    durumtext = "host"
                                }else if (binding.guest.isChecked){
                                    durumtext = "guest"
                                }else{
                                    durumtext = "none"
                                }
                                val map = hashMapOf<String, Any>()
                                map.put("adsoyad",adsoyad)
                                map.put("email",auth.currentUser!!.email!!)
                                map.put("bolum",bolum)
                                map.put("telefon",telefon)
                                map.put("sinif",sinif)
                                map.put("uzaklik",uzaklik)
                                map.put("durum",durumtext)
                                map.put("sure",sure)
                                map.put("profilResmi",downloadUrl)


                                firestore.collection("Users").document(adsoyad).set(map).addOnSuccessListener {
                                    finish()
                                }.addOnFailureListener{
                                    Toast.makeText(this, it.localizedMessage,Toast.LENGTH_SHORT).show()
                                }
                            }
                            val intent = Intent(this,HomePageActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
            else{
                Toast.makeText(this,"Lütfen alanları doldurunuz",Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun registerLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult?.data != null) {
                        selectedPicture = intentFromResult.data
                        selectedPicture?.let {
                            binding.imageView2.setImageURI(it)
                        }
                    }
                }

            }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
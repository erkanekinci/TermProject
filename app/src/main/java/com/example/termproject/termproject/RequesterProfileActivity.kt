package com.example.termproject.termproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.termproject.databinding.ActivityRequesterProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class RequesterProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRequesterProfileBinding
    private lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequesterProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val telefon = binding.telefonText.text.toString()
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        var delim = "@"
        var delim2 = "."
        var list = auth.currentUser?.email?.split(delim)
        var list2 = list?.get(0)?.split(delim2)?.toMutableList()
        list2?.set(0, list2?.get(0)?.plus(" ").plus(list2?.get(1)))

        val intentName = intent.getStringExtra("name")
        db.collection("Users").addSnapshotListener{value, error ->
            if(error != null){
                Toast.makeText(this,error.localizedMessage, Toast.LENGTH_SHORT).show()
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


                            }
                        }
                    }
                }
            }
        }
        binding.acceptButton.setOnClickListener {
            if (list2 != null) {
                db.collection("Requests").addSnapshotListener { value, error ->

                    if (error != null) {
                        Toast.makeText(
                            this,
                            error.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (value != null) {
                            if (!value.isEmpty) {
                                val documents = value.documents
                                for (document in documents) {
                                    if (document.get("requestedTo") as String == list2[0]) {
                                        db.collection("Requests").document(document.id)
                                            .update("status", "accepted")
                                    }


                                }
                            }
                        }
                    }
                }
            }
            val intent = Intent(this,HomePageActivity::class.java)
            startActivity(intent)
        }
            binding.declineButton.setOnClickListener {
                if (list2 != null) {
                    db.collection("Requests").addSnapshotListener { value, error ->

                        if (error != null) {
                            Toast.makeText(
                                this,
                                error.localizedMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            if (value != null) {
                                if (!value.isEmpty) {
                                    val documents = value.documents
                                    for (document in documents) {
                                        if(document.get("requestedTo")as String == list2[0]){
                                            db.collection("Requests").document(document.id).update("status","declined")
                                            break

                                        }

                                    }
                                }
                            }
                        }
                    }

                }
                val intent = Intent(this,HomePageActivity::class.java)
                startActivity(intent)
            }

        binding.wAButton.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data =
                Uri.parse("http://api.whatsapp.com/send?phone=+9$telefon" )
            startActivity(intent)
        }



    }
}
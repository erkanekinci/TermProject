package com.example.termproject.termproject

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.termproject.R
import com.example.termproject.databinding.ActivityFeedBinding
import com.example.termproject.termproject.adapter.FeedRecyclerAdapter
import com.example.termproject.termproject.model.Profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class FeedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var feedAdapter: FeedRecyclerAdapter
    private lateinit var profileArrayList: ArrayList<Profile>
    private lateinit var status : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore
        var delim = "@"
        var delim2 = "."
        var list = auth.currentUser?.email?.split(delim)
        var list2 = list?.get(0)?.split(delim2)?.toMutableList()
        list2?.set(0, list2?.get(0)?.plus(" ").plus(list2?.get(1)))

        profileArrayList = ArrayList<Profile> ()

        if(list2 != null){
            db.collection("Users").document(list2[0]).addSnapshotListener { value, error ->
                if (error != null){
                    Toast.makeText(this,error.localizedMessage,Toast.LENGTH_SHORT).show()

                }else{
                    if (value != null){
                        status = value.get("durum") as String
                    }
                }
            }
        }

        if(intent.getStringExtra("format") == "1"){
            getDataForFormat(list2!!)
        }else{
            getData(list2!!)
        }


        binding.recyclerView.layoutManager=LinearLayoutManager(this)
        feedAdapter = FeedRecyclerAdapter(profileArrayList)
        binding.recyclerView.adapter = feedAdapter

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)




    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getData(x:MutableList<String>){
        db.collection("Users").addSnapshotListener {value, error ->
            if(error != null){
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
            }else{
                if(value != null){
                    if(!value.isEmpty){
                        val documents = value.documents
                        profileArrayList.clear()
                        for(document in documents){
                            val adSoyad = document.get("adsoyad") as String
                            val downloadUrl = document.get("profilResmi") as String
                            if(adSoyad != x[0]){
                                val profile = Profile(adSoyad, downloadUrl)
                                profileArrayList.add(profile)
                            }


                        }
                        feedAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
    private fun getDataForFormat(x:MutableList<String>){
        db.collection("Users").addSnapshotListener {value, error ->
            if(error != null){
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
            }else{
                if(value != null){
                    if(!value.isEmpty){
                        val documents = value.documents
                        profileArrayList.clear()
                        for(document in documents){
                            val adSoyad = document.get("adsoyad") as String
                            val downloadUrl = document.get("profilResmi") as String
                            if(adSoyad != x[0] ){
                                if(status == "host" && document.get("durum")as String == "guest"){
                                    val profile = Profile(adSoyad, downloadUrl)
                                    profileArrayList.add(profile)
                                }else if(status == "guest" && document.get("durum")as String == "host"){
                                    val profile = Profile(adSoyad, downloadUrl)
                                    profileArrayList.add(profile)
                                }

                            }


                        }
                        feedAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }




}
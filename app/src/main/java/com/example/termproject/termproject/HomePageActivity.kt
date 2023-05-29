package com.example.termproject.termproject

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Intent
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.res.integerArrayResource
import com.example.termproject.R
import com.example.termproject.databinding.ActivityHomePageBinding
import com.example.termproject.databinding.AlertDialogBinding
import com.example.termproject.databinding.RecyclerRowBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore

class HomePageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomePageBinding
    private lateinit var dialogBinding: AlertDialogBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db :FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        setContentView(binding.root)
        var delim = "@"
        var delim2 = "."
        var list = auth.currentUser?.email?.split(delim)
        var list2 = list?.get(0)?.split(delim2)?.toMutableList()
        list2?.set(0, list2?.get(0)?.plus(" ").plus(list2?.get(1)))


        if(list2 != null){
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
                                if (document.get("status")as String == "waiting"){
                                    if (document.get("requestedTo") as String == list2[0]) {
                                        showDialog(document.get("requestedBy")as String)
                                        break
                                    }
                                }
                                else if(document.get("requestedBy")as String == list2[0]){
                                    if(document.get("status")as String == "accepted"){
                                        showDialogforResponse(document.get("requestedTo")as String , "kabul edildi",document.id)
                                    }else if(document.get("status")as String == "declined"){
                                        showDialogforResponse(document.get("requestedTo")as String , "reddedildi",document.id)
                                    }
                                }
                            }

                        }
                    }
                }
            }

        }

//






        binding.buttonShowMyProfile.setOnClickListener {
            val intent = Intent(this,ProfileActivity::class.java)
            intent.putExtra("email",auth.currentUser?.email.toString())
            startActivity(intent)
        }

        binding.buttonShowList.setOnClickListener {
            val intent = Intent(this,FeedActivity::class.java)
            intent.putExtra("format","1")
            startActivity(intent)
        }







    }
    private fun showDialog(x: String){
        val builder = AlertDialog.Builder(this)
        val customview = LayoutInflater.from(this).inflate(R.layout.alert_dialog, null)
        builder.setView(customview)
        val dialog = builder.create()
        val text = customview.findViewById<TextView>(R.id.alertText)
        val profileButton = customview.findViewById<Button>(R.id.dialogProfileButton)
        profileButton.setOnClickListener {
            val intent = Intent(this,RequesterProfileActivity::class.java)
            intent.putExtra("name",x)
            startActivity(intent)
        }
        text.setText(x + " adlı kullanıcı size bir teklif gönderdi")
        dialog.show()
    }
    private fun showDialogforResponse(x:String, message:String,documentId: String){
        val builder = AlertDialog.Builder(this)
        val customview = LayoutInflater.from(this).inflate(R.layout.alert_dialog, null)
        builder.setView(customview)
        val dialog = builder.create()
        val text = customview.findViewById<TextView>(R.id.alertText)
        val button = customview.findViewById<Button>(R.id.dialogProfileButton)
        button.setText("Kapat")
        text.setText(x + " adlı kullanıcıya gönderdiğiniz teklif " + message)
        dialog.show()
        button.setOnClickListener {
            dialog.cancel()
            db.collection("Requests").document(documentId).delete()
        }
    }
}
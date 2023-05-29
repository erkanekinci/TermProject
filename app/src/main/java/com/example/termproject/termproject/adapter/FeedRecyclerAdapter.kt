package com.example.termproject.termproject.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.termproject.databinding.RecyclerRowBinding
import com.example.termproject.termproject.ProfileActivity
import com.example.termproject.termproject.model.Profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


private lateinit var firestore: FirebaseFirestore
private lateinit var auth: FirebaseAuth
class FeedRecyclerAdapter(private val postList: ArrayList<Profile>) :

    RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder>() {
    class PostHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostHolder(binding)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.binding.name.text = postList.get(position).name
        Picasso.get().load(postList.get(position).downloadUrl)
            .into(holder.binding.photo)
        holder.binding.showProfile.setOnClickListener{
            val intent = Intent(holder.itemView.context, ProfileActivity::class.java)
            intent.putExtra("name",holder.binding.name.text.toString())
            holder.itemView.context.startActivity(intent)
        }
        holder.binding.requestButton.setOnClickListener {
            firestore = FirebaseFirestore.getInstance()
            auth = FirebaseAuth.getInstance()
            var delim = "@"
            var delim2 = "."
            var list = auth.currentUser?.email?.split(delim)
            var list2 = list?.get(0)?.split(delim2)?.toMutableList()
            list2?.set(0, list2?.get(0)?.plus(" ").plus(list2?.get(1)))
            val map = hashMapOf<String, Any>()
            if (list2 != null) {
                map.put("requestedBy",list2.get(0))
                map.put("requestedTo",holder.binding.name.text.toString())
                map.put("status","waiting")
            }



            firestore.collection("Requests").add(map).addOnSuccessListener {
                Toast.makeText(holder.itemView.context,"Teklif Gönderildi",Toast.LENGTH_SHORT).show()
            }


        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}
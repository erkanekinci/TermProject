package com.example.termproject.termproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.termproject.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()


        binding.signupButton.setOnClickListener {
            val email = binding.emailText.text.toString()
            val password = binding.passwordText.text.toString()
            val delim = "@"
            val emailCheckList = email.split(delim)
            if(email.isNotEmpty() && password.isNotEmpty()){
                if(emailCheckList[1] == "std.yildiz.edu.tr"){

                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(){
                        if (it.isSuccessful){
                            firebaseAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener(){
                                if (it.isSuccessful){
                                    Toast.makeText(this,"Email verification has sent. Please verify your email",Toast.LENGTH_SHORT).show()
                                }

                            }
                            val intent = Intent(this, ProfileActivity::class.java)
                            startActivity(intent)


                        }else{
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

                        }
                    }

                }else{
                    Toast.makeText(this, "Email must be a @std.yildiz.edu.tr mail", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Credentials must be filled", Toast.LENGTH_SHORT).show()
            }

        }
    }
}
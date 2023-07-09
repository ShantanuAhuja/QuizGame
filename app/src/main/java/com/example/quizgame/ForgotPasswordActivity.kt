package com.example.quizgame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.quizgame.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var forgotPasswordActivityBinding:ActivityForgotPasswordBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgotPasswordActivityBinding=ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view=forgotPasswordActivityBinding.root
        setContentView(view)
        title = "Forget Password";
        forgotPasswordActivityBinding.buttonForgotPassword.setOnClickListener {
            val email=forgotPasswordActivityBinding.editTextForgotEmail.text.toString()
            startForgotPasswordProcedure(email)
        }
    }

    private fun startForgotPasswordProcedure(email:String){
        if(email.isEmpty()){
            Toast.makeText(applicationContext,"Email Field is Empty", Toast.LENGTH_SHORT).show()
        }
        else{
            forgetPasswordWithFirebase(email)
        }
    }
    private fun forgetPasswordWithFirebase(email: String){
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
                task->
            if(task.isSuccessful){
                Toast.makeText(applicationContext,"We Sent a Password Reset Mail to Email Address",Toast.LENGTH_SHORT).show()
                finish()
            }
            else{
                Toast.makeText(applicationContext,task.exception?.localizedMessage,Toast.LENGTH_SHORT).show()
            }
        }
    }
}
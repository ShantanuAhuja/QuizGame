package com.example.quizgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.quizgame.databinding.ActivityLoginBinding
import com.example.quizgame.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupActivity : AppCompatActivity() {
    private lateinit var signUpActivityBinding : ActivitySignupBinding
    private val auth:FirebaseAuth=FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signUpActivityBinding=ActivitySignupBinding.inflate(layoutInflater)
        val view=signUpActivityBinding.root
        setContentView(view)
        title = "Sign Up";

        signUpActivityBinding.buttonSignUp.setOnClickListener {
            val email=signUpActivityBinding.editTextSignUpEmail.text.toString()
            val password=signUpActivityBinding.editTextSignUpPassword.text.toString()
            startSignupProcedure(email,password);
        }
    }

    private fun startSignupProcedure(email:String,password: String){
        if(email.isEmpty() && password.isEmpty()){
            Toast.makeText(applicationContext,"Email & Password Field is Empty",Toast.LENGTH_SHORT).show()
        }
        else if(email.isEmpty()) {
            Toast.makeText(applicationContext,"Email Field is Empty",Toast.LENGTH_SHORT).show()
        }
        else if(password.isEmpty()){
            Toast.makeText(applicationContext,"Password Field is Empty",Toast.LENGTH_SHORT).show()
        }
        else{
            signUpWithFirebase(email,password)
        }
    }

    private fun signUpWithFirebase(email :String,password:String){

        signUpActivityBinding.buttonSignUp.isClickable=false
        signUpActivityBinding.progressBarSignUp.visibility=View.VISIBLE

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            task->
            if(task.isSuccessful){
                Toast.makeText(applicationContext,"Your Account is Created",Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
                finish()
            }
            else{
                Toast.makeText(applicationContext,task.exception?.localizedMessage,Toast.LENGTH_SHORT).show()
            }
            signUpActivityBinding.buttonSignUp.isClickable=true
            signUpActivityBinding.progressBarSignUp.visibility=View.INVISIBLE
        }
    }
}
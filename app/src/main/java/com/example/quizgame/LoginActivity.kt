package com.example.quizgame

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.InspectableProperty
import com.example.quizgame.databinding.ActivityForgotPasswordBinding
import com.example.quizgame.databinding.ActivityLoginBinding
import com.example.quizgame.databinding.ActivityWelcomeBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var loginActivityBinding : ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleActivityResultLauncher:ActivityResultLauncher<Intent>
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginActivityBinding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginActivityBinding.root)
        title = "Sign In";
        // Modifying Google SignIn Button
        val textOfGoogleButton=loginActivityBinding.buttonGoogleSignIn.getChildAt(0) as TextView
        textOfGoogleButton.text="Continue With Google"
        textOfGoogleButton.setTextColor(Color.BLACK)
        textOfGoogleButton.textSize=18F
        registerActivityForGoogleSignIn()

        loginActivityBinding.buttonSignIn.setOnClickListener{

              val email = loginActivityBinding.editTextLoginEmail.text.toString()
              val password = loginActivityBinding.editTextLoginPassword.text.toString()
              startLoginProcedure(email, password)
        }

        loginActivityBinding.buttonGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }

        loginActivityBinding.textViewSignUp.setOnClickListener {
            val intent=Intent(this@LoginActivity,SignupActivity::class.java)
            startActivity(intent)
        }

        loginActivityBinding.textViewForgotPassword.setOnClickListener {
            val intent=Intent(this@LoginActivity,ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val user=auth.currentUser
        if(user !=null){
            Toast.makeText(applicationContext,"Welcome to Quizinga",Toast.LENGTH_SHORT).show()
            val intent=Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun startLoginProcedure(email:String,password:String){

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
            loginWithFirebase(email,password)
        }
    }


    private fun loginWithFirebase(email :String,password:String){

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                task->
            if(task.isSuccessful){
                Toast.makeText(applicationContext,"Successfully Signed In",Toast.LENGTH_SHORT).show()
                val intent=Intent(this@LoginActivity,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(applicationContext,task.exception?.localizedMessage,Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithGoogle(){
        val gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1090381855746-2k73qctu235eo3un7i5t4n18758b0g6g.apps.googleusercontent.com")
            .requestEmail().build()
        googleSignInClient= GoogleSignIn.getClient(this,gso)
        launchGoogleSignInIntent()
    }

    private fun launchGoogleSignInIntent(){
        val signInIntent=googleSignInClient.signInIntent
        googleActivityResultLauncher.launch(signInIntent)
    }
    private fun registerActivityForGoogleSignIn()
    {
       googleActivityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
           ActivityResultCallback {
               result ->
               val resultCode=result.resultCode
               val data=result.data
               if(resultCode== RESULT_OK && data !=null){
                   val task:Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
                   firebaseSignInWithGoogle(task)
               }
           })
    }

    private fun firebaseSignInWithGoogle(task:Task<GoogleSignInAccount>){
        try{
            val account:GoogleSignInAccount= task.getResult(ApiException::class.java)
            Toast.makeText(applicationContext,"Welcome To Quiz game",Toast.LENGTH_SHORT).show()
            val intent=Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
            firebaseGoogleAccount(account)
        }catch(e:ApiException){
            Toast.makeText(applicationContext,e.localizedMessage,Toast.LENGTH_SHORT).show()
        }
    }
    private fun firebaseGoogleAccount(account: GoogleSignInAccount){
        val authCredential=GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(authCredential).addOnCompleteListener {
            task->
            if(task.isSuccessful){
                val user=auth.currentUser
                Toast.makeText(applicationContext,user?.displayName,Toast.LENGTH_SHORT).show()
            }
            else{
            }
        }
    }
}
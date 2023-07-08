package com.example.quizgame

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.quizgame.databinding.ActivityLoginBinding
import com.example.quizgame.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var mainActivityBinding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding= ActivityMainBinding.inflate(layoutInflater)
        val view=mainActivityBinding.root
        setContentView(view)

        //adding options in spinner view
        val categories: MutableList<String> = ArrayList()
        categories.add("Easy")
        categories.add("Medium")
        categories.add("Hard")
        val dataAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, categories)
        dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
       mainActivityBinding.spinnerDifficultyLevel.adapter=dataAdapter


        mainActivityBinding.buttonStartQuiz.setOnClickListener {
            val intent=Intent(this@MainActivity,QuizActivity::class.java)
            val selectedLevel=mainActivityBinding.spinnerDifficultyLevel.selectedItem.toString()
            intent.putExtra("DifficultyLevel",selectedLevel)
            startActivity(intent)
            finish()
        }

        mainActivityBinding.buttonAddQuestions.setOnClickListener {
            val intent=Intent(this@MainActivity,AddQuestionsActivity::class.java)
            startActivity(intent)
            finish()
        }

        mainActivityBinding.buttonSignOut.setOnClickListener {

            // email password signOut
            FirebaseAuth.getInstance().signOut()

            // google activity signOut
            val gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
            val googleSignInClient=GoogleSignIn.getClient(this,gso)
            googleSignInClient.signOut().addOnCompleteListener {
                task->
                if(task.isSuccessful){
                    Toast.makeText(applicationContext,"Signed Out", Toast.LENGTH_SHORT).show()
                }
            }

            //After Successful signOut
            val intent=Intent(this@MainActivity,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
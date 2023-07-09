package com.example.quizgame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.quizgame.databinding.ActivityResultBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ResultActivity : AppCompatActivity() {
    private lateinit var resultBinding:ActivityResultBinding
    private val database=FirebaseDatabase.getInstance()
    private val databaseReference=database.reference.child("userScores")
    private val auth=FirebaseAuth.getInstance()
    private val user=auth.currentUser
    private lateinit var difficultyLevel:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultBinding=ActivityResultBinding.inflate(layoutInflater)
        val view=resultBinding.root
        setContentView(view)
        title = " Game Results";
        difficultyLevel= intent.getStringExtra("DifficultyLevel").toString()

        databaseReference.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                user?.let {
                    val userId=it.uid
                    val correctAns=snapshot.child(difficultyLevel).child(userId).child("correct").value
                    val wrongAns=snapshot.child(difficultyLevel).child(userId).child("wrong").value
                    val score= snapshot.child(difficultyLevel).child(userId).child("score").value
                    resultBinding.textViewCorrectAnswer.text=correctAns.toString()
                    resultBinding.textViewWrongAnswer.text=wrongAns.toString()
                    resultBinding.textViewScore.text=score.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,"Sorry, Result Not Displayed",Toast.LENGTH_SHORT).show()
            }

        })

        resultBinding.buttonPlayAgain.setOnClickListener {
           val intent=Intent(this@ResultActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        resultBinding.buttonCompareResults.setOnClickListener {
            val intent=Intent(this@ResultActivity,CompareResultsActivity::class.java)
            intent.putExtra("DifficultyLevel",difficultyLevel)
            startActivity(intent)
        }

        resultBinding.buttonShareResult.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "I have scored " +resultBinding.textViewCorrectAnswer.text.toString() + " out of 5 Marks in Quiz Game")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
        resultBinding.buttonExitGame.setOnClickListener {
            finish()
        }
    }
}



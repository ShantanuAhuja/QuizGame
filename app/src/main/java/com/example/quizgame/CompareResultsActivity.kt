package com.example.quizgame

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.quizgame.databinding.ActivityCompareResultsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CompareResultsActivity : AppCompatActivity() {
    private lateinit var compareResultsActivityBinding:ActivityCompareResultsBinding
    private val database= FirebaseDatabase.getInstance()
    private val databaseReference=database.reference.child("userScores")
    private val auth= FirebaseAuth.getInstance()
    private val user=auth.currentUser
    private lateinit var difficultyLevel:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        compareResultsActivityBinding=ActivityCompareResultsBinding.inflate(layoutInflater)
        setContentView(compareResultsActivityBinding.root)
        title = "Compare Results";
        difficultyLevel= intent.getStringExtra("DifficultyLevel").toString()
        getAllScores()
    }

    private fun getAllScores(){
        databaseReference.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                user?.let {
                    val playerScore=snapshot.child(difficultyLevel).child(user.uid).child("score").value
                    val playerCorrect=snapshot.child(difficultyLevel).child(user.uid).child("correct").value
                    val playerId= user.uid
                    var rank=snapshot.child(difficultyLevel).childrenCount
                    var total=snapshot.child(difficultyLevel).childrenCount
                    for(documents in snapshot.child(difficultyLevel).children){
                        if(playerId !=documents.key.toString()){
                            if(playerScore.toString().toInt() > documents.child("score").value.toString().toInt()){
                                rank--;
                            }
                            else if(playerScore.toString().toInt() == documents.child("score").value.toString().toInt() &&
                                    playerCorrect.toString().toInt() > documents.child("correct").value.toString().toInt()){
                                rank--;
                            }
                         }
                      }
                    compareResultsActivityBinding.textViewRank.text="You Have Secured "+ rank.toString()+" out of "+total.toString()+
                            " rank in the game and played "+difficultyLevel+" difficulty level."
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


}
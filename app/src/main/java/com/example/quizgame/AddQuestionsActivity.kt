package com.example.quizgame

import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizgame.databinding.ActivityAddQuestionsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AddQuestionsActivity : AppCompatActivity() {
    private lateinit var addQuestionsActivityBinding:ActivityAddQuestionsBinding
    private val database=FirebaseDatabase.getInstance()
    private var level=""
    private var databaseReference=database.reference.child("questions")
    private val auth= FirebaseAuth.getInstance()
    private val user=auth.currentUser
    private var question=""
    private var optionA=""
    private var optionB=""
    private var optionC=""
    private var optionD=""
    private var correctAns=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addQuestionsActivityBinding= ActivityAddQuestionsBinding.inflate(layoutInflater)
        setContentView(addQuestionsActivityBinding.root)
        title = "Add Questions";
        // Spinner view for difficulty level
        val categories: MutableList<String> = ArrayList()
        categories.add("Easy")
        categories.add("Medium")
        categories.add("Hard")
        val dataAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, categories)
        dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        addQuestionsActivityBinding.spinner.adapter=dataAdapter

        addQuestionsActivityBinding.buttonAddQuestion.setOnClickListener {
            var checkField:Boolean=verify()
            if(!checkField) Toast.makeText(applicationContext,"Fields Are Empty",Toast.LENGTH_SHORT).show()
            else addQuestionsToFirebase()
        }
    }

    private fun verify():Boolean{
        question=addQuestionsActivityBinding.editTextq.text.toString()
        optionA=addQuestionsActivityBinding.editTextA.text.toString()
        optionB=addQuestionsActivityBinding.editTextB.text.toString()
        optionC=addQuestionsActivityBinding.editTextC.text.toString()
        optionD=addQuestionsActivityBinding.editTextD.text.toString()
        correctAns=addQuestionsActivityBinding.editTextAns.text.toString()
        level=addQuestionsActivityBinding.spinner.selectedItem.toString()
        if(question.isEmpty() || optionA.isEmpty() || optionB.isEmpty() || optionC.isEmpty() || optionD.isEmpty() || correctAns.isEmpty()){
            return false
        }
        databaseReference=databaseReference.child(level)
        return true;
    }
    private fun addQuestionsToFirebase(){
        var count=0;
        var start=1;
        user?.let{
            databaseReference.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    while(start<2){
                        databaseReference.child((snapshot.childrenCount.toInt()).toString()).child("q").setValue(question)
                        databaseReference.child((snapshot.childrenCount.toInt()).toString()).child("A").setValue(optionA)
                        databaseReference.child((snapshot.childrenCount.toInt()).toString()).child("B").setValue(optionB)
                        databaseReference.child((snapshot.childrenCount.toInt()).toString()).child("C").setValue(optionC)
                        databaseReference.child((snapshot.childrenCount.toInt()).toString()).child("D").setValue(optionD)
                        databaseReference.child((snapshot.childrenCount.toInt()).toString()).child("Ans").setValue(correctAns).addOnCompleteListener {
                            Toast.makeText(applicationContext,"Question Added to Database", Toast.LENGTH_SHORT).show()
                        }
                        count++
                        start++;
                    }
                    val intent=Intent(this@AddQuestionsActivity,MainActivity::class.java)
                    if(count<1)Toast.makeText(applicationContext,"Question Added to the list", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                    finish()

                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext,"Sorry, Question Not Added to the list", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
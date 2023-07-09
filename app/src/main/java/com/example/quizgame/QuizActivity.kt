package com.example.quizgame

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.quizgame.databinding.ActivityQuizBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizActivity : AppCompatActivity() {
    private lateinit var quizActivityBinding: ActivityQuizBinding
    private val database=FirebaseDatabase.getInstance()
    private val databaseReference=database.reference.child("questions")
    private val scoreReference=database.reference.child("userScores")
    private val auth=FirebaseAuth.getInstance()
    private val user=auth.currentUser
    private var correctAns="";
    private var questionTotal=0;
    private var currentQuestionNumber=0;
    private var questionCount=0;
    private var currentCorrect=0;
    private var currentWrong=0;
    private var userAns=""
    private val totalTime=30000L
    private var timerContinue=false
    private var leftTime=totalTime
    private lateinit var timer:CountDownTimer
    private lateinit var intent: Intent
    private lateinit var difficultyLevel: String
    //Generate Random Numbers
    private lateinit var set:HashSet<Int>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Game Started";
        set= HashSet<Int>()
        quizActivityBinding=ActivityQuizBinding.inflate(layoutInflater)
        val view=quizActivityBinding.root
        setContentView(view)
        intent=getIntent()
        difficultyLevel= intent.getStringExtra("DifficultyLevel").toString()
        extractQuestionsFromDatabaseGameLogic()


        quizActivityBinding.buttonNext.setOnClickListener {
            resetTimer()
            extractQuestionsFromDatabaseGameLogic()
        }

        quizActivityBinding.buttonFInish.setOnClickListener {
            saveScoreInFirebase()
        }
       // user selects option A
        quizActivityBinding.textViewA.setOnClickListener {
                userAns="A"
                pauseTimer()
                if(userAns==correctAns){
                    quizActivityBinding.textViewA.setBackgroundColor(Color.GREEN)
                    currentCorrect++;
                    quizActivityBinding.textViewCorrect.text=currentCorrect.toString()
                }
                else{
                    quizActivityBinding.textViewA.setBackgroundColor(Color.RED)
                    currentWrong++;
                    quizActivityBinding.textViewWrong.text=currentWrong.toString()
                    showCorrectAnswer()
                }
                disableAllOptions()
        }

        // user selects option B
        quizActivityBinding.textViewB.setOnClickListener {
                userAns="B"
                pauseTimer()
                if(userAns==correctAns){
                    quizActivityBinding.textViewB.setBackgroundColor(Color.GREEN)
                    currentCorrect++;
                    quizActivityBinding.textViewCorrect.text=currentCorrect.toString()
                }
                else{
                    quizActivityBinding.textViewB.setBackgroundColor(Color.RED)
                    currentWrong++;
                    quizActivityBinding.textViewWrong.text=currentWrong.toString()
                    showCorrectAnswer()
                }
                disableAllOptions()
        }

        // user selects option C
        quizActivityBinding.textViewC.setOnClickListener {
                userAns="C"
                pauseTimer()
                if(userAns==correctAns){
                    quizActivityBinding.textViewC.setBackgroundColor(Color.GREEN)
                    currentCorrect++;
                    quizActivityBinding.textViewCorrect.text=currentCorrect.toString()
                }
                else{
                    quizActivityBinding.textViewC.setBackgroundColor(Color.RED)
                    currentWrong++;
                    quizActivityBinding.textViewWrong.text=currentWrong.toString()
                    showCorrectAnswer()
                }
                disableAllOptions()
        }

        // user selects option D
        quizActivityBinding.textViewD.setOnClickListener {
                userAns="D"
                pauseTimer()
                if(userAns==correctAns){
                    quizActivityBinding.textViewD.setBackgroundColor(Color.GREEN)
                    currentCorrect++;
                    quizActivityBinding.textViewCorrect.text=currentCorrect.toString()
                }
                else{
                    quizActivityBinding.textViewD.setBackgroundColor(Color.RED)
                    currentWrong++;
                    quizActivityBinding.textViewWrong.text=currentWrong.toString()
                    showCorrectAnswer()
                }
                disableAllOptions()
            }
       }

        private fun extractQuestionsFromDatabaseGameLogic(){

                restoreValues()

                databaseReference.addValueEventListener(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        questionTotal=snapshot.child(difficultyLevel).childrenCount.toInt()
                        var askedQuestion=5;
                        if(questionTotal<5) askedQuestion=questionTotal
                        if(questionCount<askedQuestion){
                            var randomNum = (0 until questionTotal).random()
                            while(set.contains(randomNum)) {
                                randomNum = (0 until questionTotal).random()
                            }
                            Log.i("random",randomNum.toString())
                            currentQuestionNumber=randomNum
                            set.add(randomNum)
                            correctAns=snapshot.child(difficultyLevel).child(currentQuestionNumber.toString()).child("Ans").value.toString()
                            var snapshotReference=snapshot.child(difficultyLevel).child(currentQuestionNumber.toString())
                               quizActivityBinding.textViewQuestion.text=snapshotReference.child("q").value.toString()
                               quizActivityBinding.textViewA.text=snapshotReference.child("A").value.toString()
                               quizActivityBinding.textViewB.text=snapshotReference.child("B").value.toString()
                               quizActivityBinding.textViewC.text=snapshotReference.child("C").value.toString()
                               quizActivityBinding.textViewD.text=snapshotReference.child("D").value.toString()
                               onStartVisibility()
                               startTimerLogic()
                        }
                        else{
                                val dialogMessage=AlertDialog.Builder(this@QuizActivity)
                                dialogMessage.setTitle("Quiz Game")
                                dialogMessage.setMessage("Congratulations!!!\nYou have Answer all the Questions.Do you want to see the Result")
                                dialogMessage.setCancelable(false)
                                dialogMessage.setPositiveButton("See Result"){ _, _ ->
                                    saveScoreInFirebase()
                                }
                                dialogMessage.setNegativeButton("PlayAgain"){_,_->
                                    val intent=Intent(this@QuizActivity,MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                dialogMessage.create().show()
                            }
                        questionCount++;
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(applicationContext,error.message,Toast.LENGTH_SHORT).show()
                    }
                })
            }



    private fun showCorrectAnswer(){
        when(correctAns){
            "A"->quizActivityBinding.textViewA.setBackgroundColor(Color.GREEN)
            "B"->quizActivityBinding.textViewB.setBackgroundColor(Color.GREEN)
            "C"->quizActivityBinding.textViewC.setBackgroundColor(Color.GREEN)
            "D"->quizActivityBinding.textViewD.setBackgroundColor(Color.GREEN)

        }
    }

    private fun disableAllOptions(){
        quizActivityBinding.textViewA.isClickable=false
        quizActivityBinding.textViewB.isClickable=false
        quizActivityBinding.textViewC.isClickable=false
        quizActivityBinding.textViewD.isClickable=false
    }
    private fun onStartVisibility(){
        quizActivityBinding.progressBarQuiz.visibility= View.INVISIBLE
        quizActivityBinding.linearLayoutQuestion.visibility=View.VISIBLE
        quizActivityBinding.linearLayoutInfo.visibility=View.VISIBLE
        quizActivityBinding.linearLayoutButton.visibility=View.VISIBLE
    }

    private fun restoreValues(){
        quizActivityBinding.textViewA.setBackgroundColor(Color.WHITE)
        quizActivityBinding.textViewB.setBackgroundColor(Color.WHITE)
        quizActivityBinding.textViewC.setBackgroundColor(Color.WHITE)
        quizActivityBinding.textViewD.setBackgroundColor(Color.WHITE)

        quizActivityBinding.textViewA.isClickable=true
        quizActivityBinding.textViewB.isClickable=true
        quizActivityBinding.textViewC.isClickable=true
        quizActivityBinding.textViewD.isClickable=true
    }

    private fun startTimerLogic(){
            timer=object:CountDownTimer(leftTime,1000){
                override fun onTick(remainingTime: Long) {
                    leftTime=remainingTime
                    CoroutineScope(Dispatchers.Main).launch {
                        updateCountDownText()
                    }
                }

                override fun onFinish() {

                        resetTimer()
                        updateCountDownText()
                        quizActivityBinding.textViewQuestion.text="Time isUp !, Move On to Next Questions"
                        timerContinue=false
                        disableAllOptions()
                }
            }.start()
            timerContinue=true
    }

    private  fun updateCountDownText(){
        val remainingTime:Int=(leftTime/1000).toInt()
        quizActivityBinding.textViewTime.text=remainingTime.toString()

    }
    private fun pauseTimer(){
        timer.cancel()
        timerContinue=false
    }
    private fun resetTimer(){
        pauseTimer()
        leftTime=totalTime
        updateCountDownText()


    }
    private fun saveScoreInFirebase(){
        user?.let{
            val userID=it.uid
            scoreReference.child(difficultyLevel).child(userID).child("correct").setValue(currentCorrect)
            scoreReference.child(difficultyLevel).child(userID).child("wrong").setValue(currentWrong)
            scoreReference.child(difficultyLevel).child(userID).child("score").setValue((currentCorrect*4)-currentWrong).addOnCompleteListener {
                Toast.makeText(applicationContext,"Scores are Saved Successfully",Toast.LENGTH_SHORT).show()
                val intent= Intent(this@QuizActivity,ResultActivity::class.java)
                intent.putExtra("DifficultyLevel",difficultyLevel)
                startActivity(intent)
                finish()
            }
        }
    }
}
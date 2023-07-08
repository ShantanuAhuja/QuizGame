package com.example.quizgame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import com.example.quizgame.databinding.ActivityWelcomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WelcomeActivity : AppCompatActivity() {
    private lateinit var welcomeActivityBinding :ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        welcomeActivityBinding=ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(welcomeActivityBinding.root)

        //Play Animation on starting the game
        val alphaAnimation=AnimationUtils.loadAnimation(applicationContext,R.anim.welcome_anim)
        welcomeActivityBinding.textViewSplash.startAnimation(alphaAnimation)
             val handler=Handler(Looper.getMainLooper())
             handler.postDelayed({
               val intent= Intent(this@WelcomeActivity,LoginActivity::class.java)
               startActivity(intent)
               finish()
            }, 5000)
        }

}
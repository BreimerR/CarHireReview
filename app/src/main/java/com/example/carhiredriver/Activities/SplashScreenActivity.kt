package com.example.carhiredriver.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.airbnb.lottie.LottieAnimationView
import com.example.carhiredriver.R
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import kotlin.concurrent.schedule

class SplashScreenActivity : AppCompatActivity() {
    //variable initialization
    lateinit var carAnimationView: LottieAnimationView
    //firebase
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        auth = FirebaseAuth.getInstance()

        carAnimationView = findViewById(R.id.car_animation)
        carAnimationView.playAnimation()
        screenChoice()

    }

    private fun screenChoice() {
        val userExist = auth.currentUser
        if (userExist!=null){
            Timer().schedule(3000){
                moveToMainScreen()
            }
        } else {
            Timer().schedule(3000){
                moveToSignInScreen()
            }

        }
    }

    private fun moveToSignInScreen() {
        val signInIntent = Intent(this,LoginActivity::class.java)
        startActivity(signInIntent)
        finish()
    }

    private fun moveToMainScreen() {
        val mainIntent = Intent(this,MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }
}
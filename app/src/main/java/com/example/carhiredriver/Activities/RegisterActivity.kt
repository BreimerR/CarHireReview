package com.example.carhiredriver.Activities

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RelativeLayout
import com.example.carhiredriver.Models.UserModel
import com.example.carhiredriver.R
import com.example.carhiredriver.Utils.SnackBarUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView

class RegisterActivity : AppCompatActivity() {
    //variable initialization
    lateinit var signUpRelativeLayout : RelativeLayout
    lateinit var registerBack : CircleImageView
    lateinit var phoneNumberSignUp : EditText
    lateinit var emailSignUp : EditText
    lateinit var nameSignUp : EditText
    lateinit var passwordSignUp : EditText
    lateinit var confirmPasswordSignUp : EditText
    lateinit var showPasswordSignUp : CheckBox
    lateinit var signUpButton : Button
    //firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("owners")

        signUpRelativeLayout = findViewById(R.id.sign_up_relative_layout)
        registerBack = findViewById(R.id.sign_up_back)
        registerBack.setOnClickListener {backToSignIn()}
        phoneNumberSignUp = findViewById(R.id.phone_number_sign_up)
        emailSignUp = findViewById(R.id.email_sign_up)
        nameSignUp = findViewById(R.id.username_sign_up)
        passwordSignUp = findViewById(R.id.password_sign_up)
        confirmPasswordSignUp = findViewById(R.id.confirm_password_sign_up)
        showPasswordSignUp = findViewById(R.id.show_password_sign_up)
        showPasswordSignUp.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                passwordSignUp.transformationMethod = null
                confirmPasswordSignUp.transformationMethod = null
            } else{
                passwordSignUp.transformationMethod = PasswordTransformationMethod()
                confirmPasswordSignUp.transformationMethod = PasswordTransformationMethod()

            }
        }
        signUpButton = findViewById(R.id.sign_up_button)
        signUpButton.setOnClickListener {validateData()}

    }

    private fun backToSignIn() {
        val backToSignInIntent = Intent(this,LoginActivity::class.java)
        startActivity(backToSignInIntent)
        finish()
    }

    private fun validateData() {
        val phoneNumber = phoneNumberSignUp.text.toString()
        val email = emailSignUp.text.toString()
        val name = nameSignUp.text.toString()
        val password = passwordSignUp.text.toString()
        val confirmPassword = confirmPasswordSignUp.text.toString()
        if (phoneNumber.isEmpty()){
            phoneNumberSignUp.error = "Phone Number Required"
            phoneNumberSignUp.requestFocus()
            return
        }
        if (email.isEmpty()){
            emailSignUp.error = "Email Required"
            emailSignUp.requestFocus()
            return
        }
        if (name.isEmpty()){
            nameSignUp.error = "Name Required"
            nameSignUp.requestFocus()
            return
        }
        if (password.isEmpty()){
            passwordSignUp.error = "Password Required"
            passwordSignUp.requestFocus()
            return
        }
        if (confirmPassword.isEmpty()){
            confirmPasswordSignUp.error = "Confirm Password Required"
            confirmPasswordSignUp.requestFocus()
            return
        }
        signUpUser()
    }

    private fun signUpUser() {
        auth.createUserWithEmailAndPassword(emailSignUp.text.toString(),passwordSignUp.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful){
                    SnackBarUtil().showSnackBar(this,"Sign Up Successful",signUpRelativeLayout)
                    sendDataToDB()
                    saveUSerProfile()
                    backToSignIn()
                }
                else {
                    Log.e(TAG, "signUpUser: ${task.exception}")
                }
            }
    }

    private fun saveUSerProfile() {
        val user = auth.currentUser
        val userProfile = userProfileChangeRequest {
            displayName = nameSignUp.text.toString()

        }
        user!!.updateProfile(userProfile).addOnCompleteListener { task ->
            if (task.isSuccessful){
                Log.e(TAG, "saveUSerProfile: Username updated" )
            }
        }
        user!!.updateEmail(emailSignUp.text.toString()).addOnCompleteListener { task ->
            if (task.isSuccessful){
                Log.e(TAG, "saveUSerProfile: Email updated" )
            }
        }

    }

    private fun sendDataToDB() {
        val model = UserModel(phoneNumberSignUp.text.toString(),nameSignUp.text.toString(),emailSignUp.text.toString())
        val uid = auth.uid
        reference.child(uid!!).setValue(model)
    }

    override fun onBackPressed() {
        backToSignIn()
    }
}
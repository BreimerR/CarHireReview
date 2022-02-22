package com.example.carhiredriver.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.carhiredriver.R
import com.example.carhiredriver.Utils.SnackBarUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.log

class LoginActivity : AppCompatActivity() {
    //variable initialization
    lateinit var signInRelativeLayout: RelativeLayout
    lateinit var loginBack : CircleImageView
    lateinit var emailSignIn : EditText
    lateinit var passwordSignIn : EditText
    lateinit var showPasswordSignIn : CheckBox
    lateinit var signInButton : Button
    lateinit var forgotPasswordText : TextView
    lateinit var registerText : TextView
    //firebase
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        signInRelativeLayout = findViewById(R.id.sign_in_relative_layout)
        loginBack = findViewById(R.id.sign_in_back)
        loginBack.setOnClickListener {finish()}
        emailSignIn = findViewById(R.id.email_sign_in)
        passwordSignIn = findViewById(R.id.password_sign_in)
        showPasswordSignIn = findViewById(R.id.show_password_sign_in)
        showPasswordSignIn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                passwordSignIn.transformationMethod = null
            } else{
                passwordSignIn.transformationMethod = PasswordTransformationMethod()
            }
        }
        signInButton = findViewById(R.id.sign_in_button)
        signInButton.setOnClickListener {validateData()}
        registerText = findViewById(R.id.create_account)
        registerText.setOnClickListener {switchToRegister()}
        forgotPasswordText = findViewById(R.id.forgot_password)
        forgotPasswordText.setOnClickListener {showBottomSheetDialog()}

    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.bottom_sheet_fragment,
            findViewById<RelativeLayout>(R.id.bottom_sheet),
        )
        bottomSheetView.findViewById<Button>(R.id.forgot_password_button).setOnClickListener {
            showResetLinkDialog()
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun showResetLinkDialog() {
        val resetLinkDialog : AlertDialog = MaterialAlertDialogBuilder(this,
            R.style.RoundedMaterialDialog)
            .setView(R.layout.password_reset_dialog)
            .setCancelable(false)
            .show()
        resetLinkDialog.findViewById<ImageView>(R.id.reset_link_close)?.setOnClickListener {
            resetLinkDialog.dismiss()
        }
    }

    private fun switchToRegister() {
        val registerIntent = Intent(this,RegisterActivity::class.java)
        startActivity(registerIntent)
        finish()
    }

    private fun validateData() {
        val email = emailSignIn.text.toString()
        val password = passwordSignIn.text.toString()
        if (email.isEmpty()){
            emailSignIn.error = "Email Required"
            emailSignIn.requestFocus()
            return
        }
        if (password.isEmpty()){
            passwordSignIn.error = "Password Required"
            passwordSignIn.requestFocus()
            return
        }
        signInUser()
    }

    private fun signInUser() {
        auth.signInWithEmailAndPassword(emailSignIn.text.toString(),passwordSignIn.text.toString())
            .addOnCompleteListener (this){ task ->
                if (task.isSuccessful){
                    SnackBarUtil().showSnackBar(this,"Sign In Successful",signInRelativeLayout)
                    Timer().schedule(3000){
                        switchToMain()
                    }
                }
            }
    }

    private fun switchToMain() {
        val mainIntent = Intent(this,MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }


}
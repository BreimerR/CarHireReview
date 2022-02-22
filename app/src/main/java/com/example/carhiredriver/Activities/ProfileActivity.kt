package com.example.carhiredriver.Activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.inflate
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.carhiredriver.R
import com.example.carhiredriver.Utils.CustomProgressDialog
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text
import java.util.*
import android.view.LayoutInflater as LayoutInflater
import kotlin.concurrent.schedule

class ProfileActivity : AppCompatActivity() {
    //variable initialization
    lateinit var profileBack : CircleImageView
    lateinit var profileImageView: CircleImageView
    lateinit var profileNameTextView: TextView
    lateinit var profileNumberTextView: TextView
    lateinit var profileEmailTextView: TextView
    lateinit var signOutButton : Button
    lateinit var editNameImageView: ImageView
    lateinit var editNumberImageView: ImageView
    //uri
    lateinit var profileImageUri : Uri
    //firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var storageReference: StorageReference
    //resultLauncher
    private lateinit var startProfileImageResult : ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("owners")

        profileBack = findViewById(R.id.profile_back)
        profileBack.setOnClickListener {backToMain()}
        editNameImageView = findViewById(R.id.edit_name)
        editNumberImageView = findViewById(R.id.edit_number)
        editNameImageView.setOnClickListener {editNameDialog()}
        editNumberImageView.setOnClickListener {editNumberDialog()}
        profileImageView = findViewById(R.id.profile_page_image)
        profileImageView.setOnClickListener {imageChooser()}
        profileNameTextView = findViewById(R.id.name_text)
        profileEmailTextView = findViewById(R.id.email_text)
        profileNumberTextView = findViewById(R.id.number_text)
        signOutButton = findViewById(R.id.sign_out_button)
        signOutButton.setOnClickListener {signOutUser()}
        profileImageResult()
        getUserData()
    }

    @SuppressLint("InflateParams")
    private fun editNumberDialog() {

        val numberDialogView = LayoutInflater.from(this).inflate(R.layout.change_number_dialog,null)
        val numberBuilder = AlertDialog.Builder(this)
            .setView(numberDialogView)
        val numberAlertDialog = numberBuilder.show()
        val numberET = numberDialogView.findViewById<EditText>(R.id.number_ed)
        val numberCancelTV = numberDialogView.findViewById<TextView>(R.id.number_cancel)
        numberCancelTV.setOnClickListener {numberAlertDialog.dismiss()}
        val numberSaveTV = numberDialogView.findViewById<TextView>(R.id.number_save)
        numberSaveTV.setOnClickListener {
            val numberString = numberET.text.toString()
            val numberDialog = CustomProgressDialog().setProgressDialog(this,"Updating number")
            numberDialog.show()
            reference.child(auth.uid!!).child("phone").setValue(numberString).addOnCompleteListener {
                Timer().schedule(2000){
                    numberDialog.dismiss()
                    numberAlertDialog.dismiss()}
                }
            }

    }

    @SuppressLint("InflateParams")
    private fun editNameDialog() {
        val nameDialogView = LayoutInflater.from(this).inflate(R.layout.change_name_dialog,null)
        val nameBuilder = AlertDialog.Builder(this)
            .setView(nameDialogView)
        val nameAlertDialog = nameBuilder.show()
        val nameET = nameDialogView.findViewById<EditText>(R.id.name_ed)
        val nameCancelTV = nameDialogView.findViewById<TextView>(R.id.name_cancel)
        nameCancelTV.setOnClickListener {nameAlertDialog.dismiss()}
        val nameSaveTV = nameDialogView.findViewById<TextView>(R.id.name_save)
        nameSaveTV.setOnClickListener {
            val nameString = nameET.text.toString()
            val nameDialog = CustomProgressDialog().setProgressDialog(this,"Updating Name")
            nameDialog.show()
            reference.child(auth.uid!!).child("name").setValue(nameString).addOnCompleteListener {
                Timer().schedule(2000){
                    nameDialog.dismiss()
                    nameAlertDialog.dismiss()}
                }
            }

    }

    private fun profileImageResult() {
        startProfileImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
                profileImageUri = fileUri
                if (profileImageUri != null){
                    profileImageView.setImageURI(profileImageUri)
                    uploadProfileImageToDB()
                }

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun uploadProfileImageToDB() {
        val loadNewProfileImageDialog = CustomProgressDialog().setProgressDialog(this,"Updating...")
        loadNewProfileImageDialog.show()
        storageReference = FirebaseStorage.getInstance().getReference("owners/${auth.uid}/profileimage")
        storageReference.putFile(profileImageUri)
            .addOnSuccessListener {
                val profileImageResult = it.metadata!!.reference!!.downloadUrl
                profileImageResult.addOnSuccessListener { it ->
                    val profileImageLink = it.toString()
                    reference.child(auth.uid!!).child("profileurl").setValue(profileImageLink)
                    loadNewProfileImageDialog.dismiss()
                }
            }.addOnFailureListener { exception ->
                Log.e(TAG, "uploadProfileImageToDB: $exception", )
            }
    }

    private fun getUserData() {
        reference.child(auth.uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").value
                    val email = snapshot.child("email").value
                    val number = snapshot.child("phone").value
                    val profileImage = snapshot.child("profileurl").value.toString()
                    Glide.with(this@ProfileActivity).load(profileImage).into(profileImageView)
                    profileNameTextView.text = name as CharSequence?
                    profileEmailTextView.text = email as CharSequence?
                    profileNumberTextView.text = number as CharSequence?
                }
                else{
                    profileImageView.setImageResource(R.drawable.ic_baseline_person_24)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun signOutUser() {
        auth.currentUser?.let {
            auth.signOut()
        }
        val logoutIntent = Intent(this, LoginActivity::class.java)
        startActivity(logoutIntent)
        finish()
    }

    private fun imageChooser() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent { intent ->
                startProfileImageResult.launch(intent)
            }
    }

    override fun onBackPressed() {
        backToMain()
    }

    private fun backToMain() {
        val mainIntent = Intent(this,MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }
}
package com.example.carhiredriver.Activities

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.carhiredriver.Models.VehicleModel
import com.example.carhiredriver.R
import com.example.carhiredriver.Utils.CustomProgressDialog
import com.example.carhiredriver.Utils.SnackBarUtil
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.concurrent.schedule

class AddVehicleActivity : AppCompatActivity() {
    //variable initialization
    lateinit var addVehicleRelativeLayout: RelativeLayout
    lateinit var addVehicleBack : CircleImageView
    lateinit var carTypeEdittext : TextInputLayout
    lateinit var carRegistrationEdittext : TextInputLayout
    lateinit var carCcEdittext : TextInputLayout
    lateinit var carPriceEdittext : TextInputLayout
    lateinit var addCar : Button
    lateinit var mFrontUri : Uri
    lateinit var mBackUri : Uri
    lateinit var mLeftSideUri : Uri
    lateinit var mRightSideUri : Uri
    lateinit var imageFrontView: ImageView
    lateinit var imageBackView : ImageView
    lateinit var imageLeftView : ImageView
    lateinit var imageRightView : ImageView
    var frontImageLink : String = null.toString()
    var backImageLink : String = null.toString()
    var leftImageLink : String = null.toString()
    var rightImageLink : String = null.toString()
    //firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var storageReference: StorageReference
    //resultLauncher
    private lateinit var startFrontImageResult : ActivityResultLauncher<Intent>
    private lateinit var startBackImageResult : ActivityResultLauncher<Intent>
    private lateinit var startLeftImageResult : ActivityResultLauncher<Intent>
    private lateinit var startRightImageResult : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_vehicle)


        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("cars")

        addVehicleRelativeLayout = findViewById(R.id.add_vehicle_relative_layout)
        addVehicleBack = findViewById(R.id.add_vehicle_back)
        addVehicleBack.setOnClickListener {backToMain()}
        addCar = findViewById(R.id.add_car)
        carTypeEdittext = findViewById(R.id.car_type)
        carRegistrationEdittext = findViewById(R.id.car_registration)
        carCcEdittext = findViewById(R.id.car_cc)
        carPriceEdittext =findViewById(R.id.car_price)
        imageFrontView = findViewById(R.id.image_front_id)
        imageBackView = findViewById(R.id.image_back_id)
        imageLeftView = findViewById(R.id.image_left_id)
        imageRightView = findViewById(R.id.image_right_id)
        frontImageResult()
        backImageResult()
        leftImageResult()
        rightImageResult()
        imageFrontView.setOnClickListener {imageFrontChooser()}
        imageBackView.setOnClickListener {imageBackChooser()}
        imageLeftView.setOnClickListener {imageLeftChooser()}
        imageRightView.setOnClickListener {imageRightChooser()}
        addCar.setOnClickListener {validateData()}
    }

    private fun imageFrontChooser() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent { intent ->
                startFrontImageResult.launch(intent)
            }

    }

    private fun imageBackChooser() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent { intent ->
                startBackImageResult.launch(intent)
            }

    }

    private fun imageRightChooser() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent { intent ->
                startRightImageResult.launch(intent)
            }
    }

    private fun imageLeftChooser() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent { intent ->
                startLeftImageResult.launch(intent)
            }
    }

    private fun frontImageResult() {
        startFrontImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
                mFrontUri = fileUri
                if (mFrontUri != null){
                    imageFrontView.setImageResource(R.drawable.tick)
                    uploadFrontImageToDB()
                }

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun backImageResult() {
        startBackImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
                mBackUri = fileUri
                if (mBackUri != null){
                    imageBackView.setImageResource(R.drawable.tick)
                    uploadBackImageToDB()
                }

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun rightImageResult() {
        startRightImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
                mRightSideUri = fileUri
                if (mRightSideUri != null){
                    imageRightView.setImageResource(R.drawable.tick)
                    uploadRightImageToDB()
                }

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun leftImageResult() {
        startLeftImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
                mLeftSideUri = fileUri
                if (mLeftSideUri != null){
                    imageLeftView.setImageResource(R.drawable.tick)
                   uploadLeftImageToDB()
                }

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    }



    private fun uploadFrontImageToDB() {
        val uploadFrontDialog = CustomProgressDialog().setProgressDialog(this,"Uploading")
        uploadFrontDialog.show()
            storageReference = FirebaseStorage.getInstance().getReference("owners/${auth.uid}/cars/${carRegistrationEdittext.editText?.text.toString()}")
            storageReference.child("front").putFile(mFrontUri)
                .addOnSuccessListener { it ->
                    val frontResult = it.metadata!!.reference!!.downloadUrl
                    frontResult.addOnSuccessListener {
                        frontImageLink = it.toString()
                        Log.e(TAG, "uploadFrontImageToDB: $frontImageLink", )
                        uploadFrontDialog.dismiss()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "uploadImageToDB: $exception")
                }

    }

    private fun uploadBackImageToDB() {
        val uploadBackDialog = CustomProgressDialog().setProgressDialog(this,"Uploading")
        uploadBackDialog.show()
        storageReference = FirebaseStorage.getInstance().getReference("owners/${auth.uid}/cars/${carRegistrationEdittext.editText?.text.toString()}")
        storageReference.child("back").putFile(mBackUri)
            .addOnSuccessListener {
                val backResult = it.metadata!!.reference!!.downloadUrl
                backResult.addOnSuccessListener { it ->
                    backImageLink = it.toString()
                    uploadBackDialog.dismiss()
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "uploadImageToDB: $exception")
            }

    }

    private fun uploadLeftImageToDB() {
        val uploadLeftDialog = CustomProgressDialog().setProgressDialog(this,"Uploading")
        uploadLeftDialog.show()
        storageReference = FirebaseStorage.getInstance().getReference("owners/${auth.uid}/cars/${carRegistrationEdittext.editText?.text.toString()}")
        storageReference.child("left").putFile(mLeftSideUri)
            .addOnSuccessListener {
                val leftResult = it.metadata!!.reference!!.downloadUrl
                leftResult.addOnSuccessListener { it ->
                    leftImageLink = it.toString()
                    uploadLeftDialog.dismiss()
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "uploadImageToDB: $exception")
            }

    }

    private fun uploadRightImageToDB() {
        val uploadRightDialog = CustomProgressDialog().setProgressDialog(this,"Uploading")
        uploadRightDialog.show()
        storageReference = FirebaseStorage.getInstance().getReference("owners/${auth.uid}/cars/${carRegistrationEdittext.editText?.text.toString()}")
        storageReference.child("right").putFile(mRightSideUri)
            .addOnSuccessListener {
                val rightResult = it.metadata!!.reference!!.downloadUrl
                rightResult.addOnSuccessListener { it ->
                    rightImageLink = it.toString()
                    uploadRightDialog.dismiss()
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "uploadImageToDB: $exception")
            }

    }

    private fun validateData() {
        val model = carTypeEdittext.editText?.text.toString()
        val registration = carTypeEdittext.editText?.text.toString()
        val cc = carTypeEdittext.editText?.text.toString()
        val price = carTypeEdittext.editText?.text.toString()

       /* when {
            model.isEmpty() -> {
                carTypeEdittext.error = "Model Required"
                carTypeEdittext.requestFocus()
            }
            registration.isEmpty() -> {
                carRegistrationEdittext.error = "Registration No Required"
                carRegistrationEdittext.requestFocus()
            }
            cc.isEmpty() -> {
                carCcEdittext.error = "Cc Required"
                carCcEdittext.requestFocus()
            }
            price.isEmpty() -> {
                carPriceEdittext.error = "Price Required"
                carPriceEdittext.requestFocus()
            }
            frontImageLink.isEmpty()-> {
                SnackBarUtil().showSnackBar(this,"Please add front view image",addVehicleRelativeLayout)
            }
            backImageLink.isEmpty()-> {
                SnackBarUtil().showSnackBar(this,"Please add back view image",addVehicleRelativeLayout)
            }
            leftImageLink.isEmpty()-> {
                SnackBarUtil().showSnackBar(this,"Please add left view image",addVehicleRelativeLayout)
            }
            rightImageLink.isEmpty()-> {
                SnackBarUtil().showSnackBar(this,"Please add right view image",addVehicleRelativeLayout)
            }
        }*/
        if(model.isEmpty()){
            carTypeEdittext.error = "Model Required"
            carTypeEdittext.requestFocus()
            //return
        }
        if(registration.isEmpty()){
            carRegistrationEdittext.error = "Registration Required"
            carRegistrationEdittext.requestFocus()
            //return
        }
        if(cc.isEmpty()){
            carCcEdittext.error = "CC Required"
            carCcEdittext.requestFocus()
            //return
        }
        if(price.isEmpty()){
            carPriceEdittext.error = "Price Required"
            carPriceEdittext.requestFocus()
           // return
        }
        addVehicle()

    }


    private fun addVehicle() {
        val addDialog = CustomProgressDialog().setProgressDialog(this,"Adding Vehicle Details")
        val uid = auth.uid
        if (frontImageLink == "null"){
            SnackBarUtil().showSnackBar(this,"Please add front view image",addVehicleRelativeLayout)
            return
        }
        if (backImageLink == "null"){
            SnackBarUtil().showSnackBar(this,"Please add back view image",addVehicleRelativeLayout)
            return
        }
        if (leftImageLink == "null"){
            SnackBarUtil().showSnackBar(this,"Please add left view image",addVehicleRelativeLayout)
            return
        }
        if (rightImageLink == "null"){
            SnackBarUtil().showSnackBar(this,"Please add right view image",addVehicleRelativeLayout)
            return
        }
        else if(frontImageLink != "null" && backImageLink != "null" && leftImageLink != "null" && rightImageLink != "null"){
            addDialog.show()
            val vehicleModel = VehicleModel(carTypeEdittext.editText?.text.toString(),
                carRegistrationEdittext.editText?.text.toString(),
                carCcEdittext.editText?.text.toString(),
                carPriceEdittext.editText?.text.toString(),frontImageLink,backImageLink,leftImageLink,rightImageLink)
            reference.child(uid!!).push()
                .setValue(vehicleModel).addOnCompleteListener {
                    Timer().schedule(2000){
                        addDialog.dismiss()
                        backToMain()
                    }
                }
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
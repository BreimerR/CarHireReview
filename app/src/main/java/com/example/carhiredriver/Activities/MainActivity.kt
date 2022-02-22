package com.example.carhiredriver.Activities

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.carhiredriver.Adapters.VehicleAdapter
import com.example.carhiredriver.Models.MyVehiclesModel
import com.example.carhiredriver.R
import com.example.carhiredriver.Utils.CustomProgressDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    //variable initialization
    lateinit var userProfileImage : CircleImageView
    lateinit var recyclerView: RecyclerView
    lateinit var addVehicle : FloatingActionButton
    //arraylists
    lateinit var vehicleArrayList: ArrayList<MyVehiclesModel>
    //firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var profileReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        database =  FirebaseDatabase.getInstance()
        reference = database.getReference("cars")

        //Toast.makeText(this,"You are ${auth.currentUser?.displayName}",Toast.LENGTH_LONG).show()

        userProfileImage = findViewById(R.id.main_page_profile_image)
        userProfileImage.setOnClickListener {switchToProfile()}
        recyclerView = findViewById(R.id.cars_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        addVehicle = findViewById(R.id.add_vehicle)
        addVehicle.setOnClickListener {switchToAddVehicle()}
        vehicleArrayList = arrayListOf<MyVehiclesModel>()
        getVehiclesData()
        getUSerProfilePhoto()
    }

    private fun getUSerProfilePhoto() {
        val mainDialog =  CustomProgressDialog().setProgressDialog(this,"Loading")
        mainDialog.show()
        profileReference = database.getReference("owners")
        profileReference.child(auth.uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val profileImage = snapshot.child("profileurl").value.toString()
                    if (!this@MainActivity.isFinishing) {
                        Glide.with(this@MainActivity)
                            .load(profileImage)
                            .thumbnail(0.05f)
                            .into(userProfileImage)
                    }
                }
                else{
                    userProfileImage.setImageResource(R.drawable.ic_baseline_person_24)
                }
                mainDialog.dismiss()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun getVehiclesData() {
        val mainDialog =  CustomProgressDialog().setProgressDialog(this,"Loading")
        mainDialog.show()
        val uid = auth.uid
        reference.child(uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (postSnapshot in snapshot.children){
                        val model = postSnapshot.child("model").value.toString()
                        val registration = postSnapshot.child("registration").value.toString()
                        val cc = postSnapshot.child("cc").value.toString()
                        val price = postSnapshot.child("price").value.toString()
                        val pricePerDay = "Ksh. $price /day"
                        
                        val vehicleInfo = MyVehiclesModel(model,registration,cc,pricePerDay)
                        vehicleArrayList.add(vehicleInfo)
                        recyclerView.adapter = VehicleAdapter(vehicleArrayList)
                    }
                }
                mainDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

   

    private fun switchToAddVehicle() {
        val addVehicleIntent = Intent(this,AddVehicleActivity::class.java)
        startActivity(addVehicleIntent)
        finish()
    }

    private fun switchToProfile() {
        val profileIntent = Intent(this,ProfileActivity::class.java)
        startActivity(profileIntent)
        finish()
    }
}
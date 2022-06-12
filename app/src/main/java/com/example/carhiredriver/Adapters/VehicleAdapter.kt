package com.example.carhiredriver.Adapters
/**
 * TODO
 * Don't ise capital letters in your package names
 * naming is very important to readability
 * and using such will make collaboration a problem in the future
 * example
 *
 * import com.example.carhiredriver.Something
 * import com.example.carhiredriver.SomethingElse
 *
 * can you distinguish which of this is a class which isn't?
 *
 *
 **/

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.example.carhiredriver.Models.MyVehiclesModel
import com.example.carhiredriver.R

class VehicleAdapter(private val myVehiclesInfo : ArrayList<MyVehiclesModel>) : RecyclerView.Adapter<VehicleAdapter.MyVehiclesViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVehiclesViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.vehicle_info,parent,false)
       return MyVehiclesViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyVehiclesViewHolder, position: Int) {
        val currentVehicle = myVehiclesInfo[position]
        holder.vehicleType.text = currentVehicle.vehicleType
        holder.vehicleRegistration.text = currentVehicle.vehicleRegistration
        holder.vehicleCC.text = currentVehicle.vehicleCC
        holder.vehiclePrice.text = currentVehicle.vehiclePrice
    }

    override fun getItemCount(): Int {
        return myVehiclesInfo.size
    }

    class MyVehiclesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val vehicleType : TextView = itemView.findViewById(R.id.card_view_car_model_text)
        val vehicleRegistration : TextView = itemView.findViewById(R.id.card_view_car_registration_text)
        val vehicleCC : TextView = itemView.findViewById(R.id.card_view_car_cc_text)
        val vehiclePrice : TextView = itemView.findViewById(R.id.card_view_car_price_text)

    }


}
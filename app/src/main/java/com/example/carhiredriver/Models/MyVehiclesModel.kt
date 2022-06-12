package com.example.carhiredriver.Models

/**
 * TODO
 * data classes can't be models
 * Avoid this.
 * In MVC or MVVM a model does more than just hold data it's
 * the main data processor and that's the main use case
 * and not to just present a set of data but a collection of data
 * to be validated and processed i.e
 *
 *
 * data class Vehicle(
 *  val type: String,
 *  ...
 * )
 * object VehiclesModel{
 *      fun validate(vehicle: Vehicle){
 *
 *      }
 * }
 **/
data class MyVehiclesModel(
    val vehicleType: String? = null,
    val vehicleRegistration: String? = null,
    val vehicleCC: String? = null,
    val vehiclePrice: String? = null
)

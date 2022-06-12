package com.example.carhiredriver.Utils

import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.carhiredriver.R
import com.google.android.material.snackbar.Snackbar

class SnackBarUtil {

    /**
     * Try to understand what null safety means in kotlin
     * view: View will never be null and can never be set to null
     * view: View? can be set to null and can be null at one point or the other
     **/
    fun showSnackBar(context : Context, message : String, view : View) {
        if (view != null && context != null){
            val snackBar : Snackbar = Snackbar.make(view,message, Snackbar.LENGTH_LONG)
            val snackView : View = snackBar.view
            snackView.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
            val snackText : TextView = snackView.findViewById(com.google.android.material.R.id.snackbar_text)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                snackText.textAlignment = View.TEXT_ALIGNMENT_CENTER
            }else{
                snackText.gravity = Gravity.CENTER_HORIZONTAL
            }
            snackBar.show()
        }
    }
}
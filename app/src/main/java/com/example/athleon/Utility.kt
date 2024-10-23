package com.example.athleon

import android.animation.ObjectAnimator
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat

object Utility {
// funcion convertir texto en segundos

    fun getSecFromWatch(watch:String):Int{

        var secs=0
        var w:String= watch
        if(w.length ==5) w="00:"+w //convierte a 00.00.00

        //00:00:00
        secs+= w.subSequence(0,2).toString().toInt() *3600
        secs+= w.subSequence(3,5).toString().toInt() *60
        secs+= w.subSequence(6,8).toString().toInt()

        return secs

    }

//funciones de animaccion y cambios de atributos
    fun setHeightLinearLayout(ly: LinearLayout, value: Int){
       val params:LinearLayout.LayoutParams = ly.layoutParams as LinearLayout.LayoutParams
        params.height= value
        ly.layoutParams= params
    }

    fun animateViewofInt(v:View, atr:String, value:Int, time:Long){
        ObjectAnimator.ofInt(v, atr, value).apply {
            duration = time
            start()
        }
    }

    fun animateViewofFloat(v:View, atr:String, value:Float, time:Long){
        ObjectAnimator.ofFloat(v, atr, value).apply {
            duration = time
            start()
        }
    }
}
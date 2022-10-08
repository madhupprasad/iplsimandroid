package com.example.viewmodeldemo

import android.content.Context
import android.widget.Toast

fun nameValidator(name : String, context : Context?) : Boolean{
    if(name.isEmpty()){
        Toast.makeText(context, "Type your name", Toast.LENGTH_SHORT).show()
        return false
    }
    if(name.length > 10){
        Toast.makeText(context, "Type less than 10 chars", Toast.LENGTH_SHORT).show()
        return false
    }
    return true
}

fun numValidator(num : String, context : Context?) : Boolean{

    if(num == ""){
        Toast.makeText(context, "Type number of players", Toast.LENGTH_SHORT).show()
        return false
    }else{
        val num = num.toInt()
        if(num > 8){
            Toast.makeText(context, "8 players max", Toast.LENGTH_SHORT).show()
            return false
        }
        if(num < 2){
            Toast.makeText(context, "2 players min", Toast.LENGTH_SHORT).show()
            return false
        }
    }
    return true
}
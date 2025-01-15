package com.shlee.inappkeyboard

import com.google.firebase.database.Exclude

data class UserData(val Name: String? = "", val Mileage: String? = ""){

    @Exclude
    fun getMap(): Map<String, Any?>{
        return mapOf(
            "Phone" to Name
        )
    }
}

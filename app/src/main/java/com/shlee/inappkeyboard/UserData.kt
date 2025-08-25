package com.shlee.inappkeyboard

import com.google.firebase.database.Exclude

data class UserData(val Update: String? = "", val Mileage: String? = ""){

    @Exclude
    fun getMap(): Map<String, Any?>{
        return mapOf(
            "Update" to Update
        )
    }
}

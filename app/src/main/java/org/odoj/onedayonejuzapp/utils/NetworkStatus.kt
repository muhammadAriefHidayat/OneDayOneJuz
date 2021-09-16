package org.odoj.onedayonejuzapp.utils

import com.google.firebase.auth.FirebaseAuth

class NetworkStatus(val status:String) {
    val user :FirebaseAuth? = null

    val mAuth = FirebaseAuth.getInstance().uid
}
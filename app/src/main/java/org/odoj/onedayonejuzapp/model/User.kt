package org.odoj.onedayonejuzapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val displayName: String, val status :String,val network:String, val image:String, val jenisKelamin: String,
           val provinsi:String,val uid:String,var grup:String):Parcelable{
    constructor():this("","","","","","","","")
}


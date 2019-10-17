package org.odoj.onedayonejuzapp.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val displayName: String, val status :String,val image:String, val jenisKelamin: String,val provinsi:String
           ,val uid:String):Parcelable{
    constructor():this("","","","","","")
}

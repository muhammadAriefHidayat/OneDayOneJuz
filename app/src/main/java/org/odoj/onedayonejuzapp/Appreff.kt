package org.odoj.onedayonejuzapp

import android.content.Context
import android.content.SharedPreferences

object Appreff {

    private const val NAME = "SharePrefApp"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    private val EMAIL = Pair("email", "")
    private val NAMA = Pair("nama", "")
    private val UID = Pair("uid", "")
    private val PASSWORD = Pair("password", "")
    private val FOTO = Pair("foto", "")
    private val GRUP = Pair("grup", "")
    private val PROVINSI = Pair("provinsi","")
    private val JENISKELAMIN = Pair("jeniskelamin","")

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    //an inline function to put variable and save it
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var password: String
        get() = preferences.getString(PASSWORD.first, PASSWORD.second) ?: ""
        set(value) = preferences.edit(){
            it.putString(PASSWORD.first, value)
        }

    var nama: String
        get() = preferences.getString(NAMA.first, NAMA.second) ?: ""
        set(value) = preferences.edit(){
            it.putString(NAMA .first, value)
        }

    var uid: String
        get() = preferences.getString(UID.first, UID.second) ?: ""
        set(value) = preferences.edit(){
            it.putString(UID.first, value)
        }

    var email: String
        get() = preferences.getString(EMAIL.first, EMAIL.second) ?: ""
        set(value) = preferences.edit(){
            it.putString(EMAIL .first, value)
        }


    var foto: String
        get() = preferences.getString(FOTO.first, FOTO.second) ?: ""
        set(value) = preferences.edit(){
            it.putString(FOTO .first, value)
        }

    var grup: String
        get() = preferences.getString(GRUP.first, GRUP.second) ?: ""
        set(value) = preferences.edit(){
            it.putString(GRUP.first, value)
        }

    var provinsi: String
        get() = preferences.getString(PROVINSI.first, PROVINSI.second) ?: ""
        set(value) = preferences.edit(){
            it.putString(PROVINSI.first, value)
        }

    var jeniskelamin: String
        get() = preferences.getString(JENISKELAMIN.first, JENISKELAMIN.second) ?: ""
        set(value) = preferences.edit(){
            it.putString(JENISKELAMIN.first, value)
        }
}
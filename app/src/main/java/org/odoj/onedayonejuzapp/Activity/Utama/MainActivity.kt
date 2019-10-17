package org.odoj.onedayonejuzapp.Activity.Utama

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import org.odoj.onedayonejuzapp.Activity.DaftarLogin.DaftarActivity
import org.odoj.onedayonejuzapp.Activity.DaftarLogin.LoginActivity
import org.odoj.onedayonejuzapp.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    /**
     * fungsi daftarWllcome untuk melakukan pindah activity dari MainActivy ke DaftarActivity dengan delay 1100 millisecond
     * */
    fun daftarWellcome (view: View){
        Handler().postDelayed({
            val intentdaftar = Intent(this, DaftarActivity::class.java)
            startActivity(intentdaftar)
        },1100)
    }

    /**
     * Fungsi loginWllcome untuk melakukan pindah activity dari MainActivy ke DaftarActivity dengan delay 1100 millisecond
     * */
    fun loginWellcome (view: View){
        Handler().postDelayed({
            val inten =Intent(this, LoginActivity::class.java)
            startActivity(inten)
        },1100)
    }
}

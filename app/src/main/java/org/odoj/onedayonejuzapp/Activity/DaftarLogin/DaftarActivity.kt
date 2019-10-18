package org.odoj.onedayonejuzapp.Activity.DaftarLogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_daftar.*
import org.odoj.onedayonejuzapp.Activity.Utama.DashBoard
import org.odoj.onedayonejuzapp.R


class DaftarActivity : AppCompatActivity() {

    var mAuth : FirebaseAuth? = null
    var mDatabase: DatabaseReference? = null
    var mDataUser: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar)

        daftarSpinner.visibility = View.INVISIBLE

        mAuth = FirebaseAuth.getInstance()

        daftarBtnDaftar.setOnClickListener {
            val displayName = userEditTextDaftar.text.toString().trim()
            val email = emailEditTextDaftar.text.toString().trim()
            val password = passwordEditTextDaftar.text.toString().trim()
            val provinsi = provinsiEditTextDaftar.text.toString().trim()
            val idRadio  = radiogrup_Daftar.checkedRadioButtonId

            if (idRadio != -1){
                val jenisKelamin = findViewById<RadioButton>(idRadio).text.toString()

                if (TextUtils.isEmpty(displayName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(provinsi)){
                    Toast("Form tidak boleh kosong")
                }else{
                    CreatAkun(email, password, displayName, provinsi, jenisKelamin)
                    DaftarSpinner(true)
                }
            } else{
                Toast("Jenis Kelamin Tidak Boleh Kosong")
            }
        }
    }


    /**
     * membuat fungsi CreatAkun menggunakan Firebase
     */
    fun CreatAkun (email: String, password: String, displayName: String, provinsi:String, jenisKelamin:String){
        mAuth!!.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                task: Task<AuthResult> ->
                if (task.isSuccessful){
                    val curentuser= mAuth!!.currentUser
                    val userid = curentuser!!.uid

                    mDataUser = FirebaseDatabase.getInstance().reference
                        .child("dataUser").child(userid)

                    mDatabase = FirebaseDatabase.getInstance().reference
                        .child("User").child(provinsi).child(jenisKelamin).child(userid)

                    val userObject = HashMap<String,String>()
                    userObject.put("displayName",displayName)
                    userObject.put("status","membumikan alquran melangitkan dunia")
                    userObject.put("image","default")
                    userObject.put("provinsi", provinsi)
                    userObject.put("jenisKelamin",jenisKelamin)
                    userObject.put("uid",userid)

                    mDataUser!!.setValue(userObject)

                    mDatabase!!.setValue(userObject).addOnCompleteListener {
                        task: Task<Void> ->
                        DaftarSpinner(false)
                          if (task.isSuccessful){
                              val dashBoardIntent = Intent(this, DashBoard::class.java)
                              dashBoardIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                              startActivity(dashBoardIntent)

                          }else{
                              DaftarSpinner(false)
                              Toast.makeText(this,"User Not Created",Toast.LENGTH_LONG)
                                  .show()
                          }
                    }

                }else{
                    DaftarSpinner(false)
                    Toast.makeText(this,"eror", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun loginOnclickLogin (view: View){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun DaftarSpinner(enable : Boolean){
        if (enable){
            daftarSpinner.visibility = View.VISIBLE
        }else{
            daftarSpinner.visibility = View.INVISIBLE
        }
        daftarBtnDaftar.isEnabled = !enable
        loginTxtDaftar.isClickable = !enable
    }

    fun Toast(text:String){
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show()
    }
}

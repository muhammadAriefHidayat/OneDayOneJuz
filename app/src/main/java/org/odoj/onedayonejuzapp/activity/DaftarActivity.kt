package org.odoj.onedayonejuzapp.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_daftar.*
import kotlinx.android.synthetic.main.list_row_new_message.view.*
import org.odoj.onedayonejuzapp.Appreff
import org.odoj.onedayonejuzapp.R
import org.odoj.onedayonejuzapp.utils.Utils
import org.odoj.onedayonejuzapp.utils.Utils.isEmailValid


class DaftarActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    private var mDataUser: DatabaseReference? = null
    lateinit var provinsi: Array<String>
    var klik = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar)

        daftarSpinner.visibility = View.INVISIBLE

        mAuth = FirebaseAuth.getInstance()

        provinsi = resources.getStringArray(R.array.Provinsi)

        provinsiBTN_Daftar.setOnClickListener {
            getPulau()
        }

        daftarBtnDaftar.setOnClickListener {
            klik += 1
            daftarAkun()
        }
    }

    private fun getPulau() {
        AlertDialog.Builder(this)
            .setSingleChoiceItems(provinsi, 0, null)
            .setPositiveButton("CHOOSE") { dialog, _ ->

                val selectedPosition = (dialog as AlertDialog).listView.checkedItemPosition

                provinsiBTN_Daftar.text = provinsi[selectedPosition]

                dialog.dismiss()
            }
            .show()
    }


    private fun daftarAkun() {
        val displayName = userEditTextDaftar.text.toString().trim()
        val email = emailEditTextDaftar.text.toString().trim()
        val password = passwordEditTextDaftar.text.toString().trim()
        val provinsi = provinsiBTN_Daftar.text.toString().trim()
        val idRadio = radiogrup_Daftar.checkedRadioButtonId

        when {
            TextUtils.isEmpty(displayName) -> {
                Utils.Peringatan("Nama tidak Boleh Kosong", this)
                klik = 0
            }
            TextUtils.isEmpty(email) -> {
                Utils.Peringatan("Email tidak Boleh Kosong", this)
                klik = 0
            }
            !email.isEmailValid() -> {
                Utils.Peringatan("Email Tidak Valid", this)
                klik = 0
            }
            TextUtils.isEmpty(password) -> {
                Utils.Peringatan("Passwrod Tidak Boleh kosong", this)
                klik = 0
            }
            password.count() <= 6 -> {
                Utils.Peringatan("Passwrod Harus lebih dari 6", this)
                klik = 0
            }
            TextUtils.isEmpty(provinsi) -> {
                Utils.Peringatan("Provinsi Tidak Boleh kosong", this)
                klik = 0
            }
            idRadio == -1 -> {
                Utils.Peringatan("Jenis Kelamin Tidak Boleh kosong", this)
                klik = 0
            }
            else -> {
                if (klik == 0){
                    val jenisKelamin = findViewById<RadioButton>(idRadio).text.toString()
                    CreatAkun(email, password, displayName, provinsi, jenisKelamin)
                    Utils.Progressbar(daftarSpinner, true)
                }
            }
        }
    }


    private fun CreatAkun(
        email: String,
        password: String,
        displayName: String,
        provinsi: String,
        jenisKelamin: String
    ) {
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {

                    //pembuatan login
                    var curentuser = mAuth!!.currentUser
                    var userid = curentuser!!.uid

                    mDataUser = FirebaseDatabase.getInstance().reference
                        .child("dataUser").child(userid)

                    mDatabase = FirebaseDatabase.getInstance().reference
                        .child("User").child(jenisKelamin).child(provinsi).child(userid)

                    val userObject = HashMap<String, String>()
                    userObject["displayName"] = displayName
                    userObject["status"] = "membumikan alquran melangitkan dunia"
                    userObject["image"] = "default"
                    userObject["provinsi"] = provinsi
                    userObject["jenisKelamin"] = jenisKelamin
                    userObject["uid"] = userid

                    val userGrup = HashMap<String, String>()
                    userGrup["uid"] = userid

                    Appreff.email = email
                    Appreff.nama = displayName
                    Appreff.provinsi = provinsi
                    Appreff.jeniskelamin = jenisKelamin
                    Appreff.uid = userid

                    mDatabase!!.setValue(userGrup)
                    mDataUser!!.setValue(userObject).addOnCompleteListener { task: Task<Void> ->
                        Utils.Peringatan("user berhasil dibuat", this)
                        if (task.isSuccessful) {
                            var tambahkankegrup = 0
                            var semuaanggota: Long = 0
                            val reference2 = FirebaseDatabase.getInstance()
                                .getReference("anggotaGrup/${jenisKelamin}/Odojer${provinsi}")
                            reference2.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    Utils.Peringatan("Mencari Grup Kosong", this@DaftarActivity)
                                    snapshot.children.forEach { grup ->
                                        semuaanggota += 1

                                        if (grup.childrenCount < 3 && tambahkankegrup == 0) {
                                            tambahkankegrup += 1
                                            val refgrup =
                                                FirebaseDatabase.getInstance().reference.child("anggotaGrup/$jenisKelamin/Odojer${provinsi}")
                                                    .child(grup.key.toString()).child(userid)
                                            refgrup.child("uid").setValue(userid)
                                                .addOnCompleteListener { newgrup->
                                                    if (newgrup.isSuccessful) {
                                                        val refUser =
                                                            FirebaseDatabase.getInstance().reference.child(
                                                                "dataUser"
                                                            ).child(userid)
                                                        refUser.child("grup")
                                                            .setValue(grup.key.toString()).addOnCompleteListener {getgrup->
                                                                if (getgrup.isSuccessful){
                                                                    Appreff.grup = grup.key.toString()
                                                                    dashboard()
                                                                }else{
                                                                    dashboard()
                                                                }
                                                            }
                                                    }
                                                }
                                        } else if (semuaanggota == snapshot.childrenCount && tambahkankegrup == 0) {
                                            Utils.Peringatan("Tidak ada grup kosong", this@DaftarActivity)
                                            dashboard()
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            })
                        } else {
                            klik = 0
                            DaftarSpinner(false)
                            Toast.makeText(this, "User Not Created", Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                } else {
                    Log.w("createuser", "createUserWithEmail:failure", task.exception)
                    DaftarSpinner(false)
                    klik = 0
                    Toast.makeText(this, "Email telah digunakan", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun dashboard(){
        val dashBoardIntent = Intent(this@DaftarActivity, DashBoard::class.java)
        dashBoardIntent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(dashBoardIntent)
    }

    fun loginOnclickLogin(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun DaftarSpinner(enable: Boolean) {
        if (enable) {
            daftarSpinner.visibility = View.VISIBLE
        } else {
            daftarSpinner.visibility = View.INVISIBLE
        }
    }
}
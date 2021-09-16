package org.odoj.onedayonejuzapp.activity


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*
import org.odoj.onedayonejuzapp.Appreff
import org.odoj.onedayonejuzapp.R
import org.odoj.onedayonejuzapp.model.User
import org.odoj.onedayonejuzapp.utils.Utils
import org.odoj.onedayonejuzapp.utils.Utils.Peringatan


class LoginActivity : AppCompatActivity() {

    var user: FirebaseUser? = null
    var mAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    var login = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        LoginBgnLogin.setOnClickListener {
            Utils.Progressbar(LoginSpinner,true)
            login += 1
            val email = emailLoginLogin.text.toString().trim()
            val password = passwordLoginLogin.text.toString().trim()
            when {
                TextUtils.isEmpty(email) -> {
                    Peringatan("Email tidak boleh kosong", this)
                    login = 0
                    Utils.Progressbar(LoginSpinner,false)
                }
                TextUtils.isEmpty(password) -> {
                    Peringatan("Password tidak boleh kosong", this)
                    login = 0
                    Utils.Progressbar(LoginSpinner,false)
                }
                login <= 1 -> {
                    Login(email, password)
                }
            }
        }
    }

    fun Login(email: String, password: String) {
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    val uid = FirebaseAuth.getInstance().currentUser!!.uid
                    getdataUser(uid)
                } else {
                    Utils.Progressbar(LoginSpinner, false)
                    Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
                    login = 0
                }
            }
    }

    fun getdataUser(uid: String) {
        val userHasmap = HashMap<String, User>()
        val dataUser = FirebaseDatabase.getInstance().getReference("dataUser/$uid")
        dataUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Utils.Progressbar(LoginSpinner, false)
                if (snapshot.value.toString() == "null") {
                    FirebaseAuth.getInstance().signOut()
                    Peringatan("harap login dengan akun User", this@LoginActivity)
                    Utils.Progressbar(LoginSpinner,false)
                } else {
                    val user = snapshot.getValue(User::class.java) ?: return
                    Log.d("sukses", user.provinsi)
                    userHasmap[snapshot.key!!] = user
                    Appreff.email = user.displayName
                    Appreff.jeniskelamin = user.jenisKelamin
                    Appreff.provinsi = user.provinsi
                    Appreff.nama = user.displayName
                    Appreff.grup = user.grup
                    Appreff.uid  = user.uid
                    Log.d("sukses", Appreff.provinsi)

                    Utils.Progressbar(LoginSpinner, false)
                    val dashBoardIntent = Intent(this@LoginActivity, DashBoard::class.java)
                    dashBoardIntent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(dashBoardIntent)
                    Utils.Progressbar(LoginSpinner,false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                login = 0
            }

        })
    }

    fun masuk(view: View) {
        startActivity(Intent(this, DaftarActivity::class.java))
    }


}

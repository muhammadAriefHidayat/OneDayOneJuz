package org.odoj.onedayonejuzapp.Activity.DaftarLogin


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import org.odoj.onedayonejuzapp.Activity.Utama.DashBoard
import org.odoj.onedayonejuzapp.R


class LoginActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        LoginSpinner.visibility = View.INVISIBLE

        LoginBgnLogin.setOnClickListener {
            val email = emailLoginLogin.text.toString().trim()
            val password = passwordLoginLogin.text.toString().trim()
            if (!TextUtils.isEmpty(email) || (!TextUtils.isEmpty(password))){
                loginSpinner(true)
                Login(email, password)
            }else{
                Toast.makeText(this,"Harap di isi dengan benar",Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    fun Login(email:String,password:String){
        mAuth!!.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            task: Task<AuthResult> ->
            if (task.isSuccessful){
                loginSpinner(false)
                    val dashBoardIntent = Intent(this, DashBoard::class.java)
                    dashBoardIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(dashBoardIntent)

                }else{
                loginSpinner(false)
                Toast.makeText(this,"Login Gagal",Toast.LENGTH_LONG).
                    show()
            }
        }
    }

    fun masuk(view: View){
        startActivity(Intent(this, DaftarActivity::class.java))
    }


    fun loginSpinner(enable : Boolean){
        if(enable){
            LoginSpinner.visibility = View.VISIBLE
        }else{
            LoginSpinner.visibility = View.INVISIBLE
        }
        LoginBgnLogin.isEnabled = !enable
    }
}

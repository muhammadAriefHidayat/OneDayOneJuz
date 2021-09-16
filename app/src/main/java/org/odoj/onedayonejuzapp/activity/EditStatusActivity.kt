package org.odoj.onedayonejuzapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_edit_status.*
import org.odoj.onedayonejuzapp.R

class EditStatusActivity : AppCompatActivity() {
    var mCurrentUser: FirebaseUser? = null
    var mDatabse :DatabaseReference? = null
    var toolbar :Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_status)

        toolbar = findViewById(R.id.toolbarEditStatus)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        updateStatusSpinner.visibility = View.INVISIBLE

        if (intent.extras != null){
            val oldstatus = intent.extras?.get("status")
            editStatusTxtEdit.setText(oldstatus.toString())
        }
        if(intent.extras == null){
            editStatusTxtEdit.setText("Masukkan Status...")
        }

        updataStatusBtn.setOnClickListener {
            UpdateSpinner(true)
            UpdateStatus()
        }
    }

    fun UpdateSpinner(enable: Boolean){
        if (enable){
            updateStatusSpinner.visibility = View.VISIBLE
        }else{
            updateStatusSpinner.visibility = View.INVISIBLE
        }
        updataStatusBtn.isEnabled = !enable

    }

    fun UpdateStatus(){
        mCurrentUser = FirebaseAuth.getInstance().currentUser
        val userId = mCurrentUser!!.uid

        mDatabse = FirebaseDatabase.getInstance().reference
            .child("dataUser")
            .child(userId)

        val status = editStatusTxtEdit.text.toString().trim()

        mDatabse!!.child("status").setValue(status).addOnCompleteListener {
                task: Task<Void> ->
            UpdateSpinner(false)
            if (task.isSuccessful){
                Toast.makeText(this, "Status update successfully",Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(this, ProfilActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this, "Status Not Updatated",Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}

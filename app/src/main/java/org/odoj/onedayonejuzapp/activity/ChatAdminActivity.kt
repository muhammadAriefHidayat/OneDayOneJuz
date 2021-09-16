package org.odoj.onedayonejuzapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_admin.*
import kotlinx.android.synthetic.main.activity_new_message.progressBarPesanBaru
import org.odoj.onedayonejuzapp.Appreff
import org.odoj.onedayonejuzapp.Extra
import org.odoj.onedayonejuzapp.R
import org.odoj.onedayonejuzapp.adapter.AdminItemAdapter
import org.odoj.onedayonejuzapp.adapter.UserItemAdapter
import org.odoj.onedayonejuzapp.model.User

class ChatAdminActivity : AppCompatActivity() {
    var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_admin)

        toolbar = findViewById(R.id.toolbar_ChatAdmin)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getAdmin(Appreff.provinsi,Appreff.jeniskelamin)
    }


    fun getAdmin(provinsi:String, jenisKelamin:String){
        val ref = FirebaseDatabase.getInstance().getReference("/adminGrup/$jenisKelamin/$provinsi")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            val adapter = GroupAdapter<ViewHolder>()

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                Log.d("hasil",dataSnapshot.toString())
                dataSnapshot.children.forEach{
                    val user = it.getValue(User::class.java)
                    Log.d("Data",it.toString())
                    if (user != null){
                        adapter.add(AdminItemAdapter(user,this@ChatAdminActivity))
                        progressBarPesanBaru.visibility = View.INVISIBLE
                    }else{
                        Toast.makeText(this@ChatAdminActivity,"Tidak Ada Admin", Toast.LENGTH_SHORT).show()
                        progressBarPesanBaru.visibility = View.INVISIBLE
                    }
                    recycleview_ChatAdmin.adapter = adapter
                }

            }
            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }
}
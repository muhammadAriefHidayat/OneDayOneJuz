package org.odoj.onedayonejuzapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import org.odoj.onedayonejuzapp.Appreff
import org.odoj.onedayonejuzapp.Extra
import org.odoj.onedayonejuzapp.R
import org.odoj.onedayonejuzapp.adapter.UserItemAdapter
import org.odoj.onedayonejuzapp.model.User

class NewMessageActivity : AppCompatActivity() {

    var toolbar: Toolbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        toolbar = findViewById(R.id.toolbar_ChatAdmin)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getDadaUser()
    }


    fun fetchUser() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val datauser = FirebaseDatabase.getInstance().getReference("/dataUser/").child(userId)
        datauser.addValueEventListener(object: ValueEventListener{
             override fun onDataChange(dataSnapshot: DataSnapshot) {
                 val provinsi = dataSnapshot.child("provinsi").value.toString()
                 val jeniskelamin = dataSnapshot.child("jenisKelamin").value.toString()

//                 getDadaUser(provinsi,jeniskelamin)

            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })


    }

    fun newPesanProgresbar(enable: Boolean){
        if (enable){
            progressBarPesanBaru.visibility = View.VISIBLE
        }else{
            progressBarPesanBaru.visibility = View.INVISIBLE
        }

    }


    fun getDadaUser(){
        val ref = FirebaseDatabase.getInstance().getReference("/User/${Appreff.jeniskelamin}/${Appreff.provinsi}")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            val adapter = GroupAdapter<ViewHolder>()

            override fun onDataChange(dataSnapshot:DataSnapshot) {
                dataSnapshot.children.forEach{
                    val user = it.getValue(User::class.java)
                    Log.d("Data",it.toString())
                    if (user!= null){
                        adapter.add(UserItemAdapter(user,this@NewMessageActivity))
                        newPesanProgresbar(false)
                    }else{
                        Toast.makeText(this@NewMessageActivity,"kontak kosong",Toast.LENGTH_SHORT).show()
                        newPesanProgresbar(false)
                    }
                    adapter.setOnItemClickListener { item , view ->
                        val useritem = item as UserItemAdapter
                        val intent = Intent(view.context, ChatLogActivity::class.java)
                        intent.putExtra(Extra,useritem.user)
                        startActivity(intent)
                    }
                    recycleViewNewMessage.adapter = adapter
                }

            }
            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }
}

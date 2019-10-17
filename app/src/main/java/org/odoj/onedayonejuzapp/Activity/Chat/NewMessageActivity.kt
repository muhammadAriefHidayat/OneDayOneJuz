package org.odoj.onedayonejuzapp.Activity.Chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import org.odoj.onedayonejuzapp.Adapter.UserItemAdapter
import org.odoj.onedayonejuzapp.Model.User
import org.odoj.onedayonejuzapp.R

class NewMessageActivity : AppCompatActivity() {

    var toolbar: Toolbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        toolbar = findViewById(R.id.toolbar_newMessage)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fetchUser()
    }

    companion object {
        val key_user = "User"
    }

    fun fetchUser() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val datauser = FirebaseDatabase.getInstance().getReference("/dataUser/").child(userId)
        datauser.addValueEventListener(object: ValueEventListener{
             override fun onDataChange(dataSnapshot: DataSnapshot) {
                 val provinsi = dataSnapshot.child("provinsi").value.toString()
                 val jeniskelamin = dataSnapshot.child("jenisKelamin").value.toString()

                 getDadaUser(provinsi,jeniskelamin)

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


    fun getDadaUser(privinsi:String, jenisKelamin:String){
        val ref = FirebaseDatabase.getInstance().getReference("/User/$privinsi/$jenisKelamin")
                ref.addListenerForSingleValueEvent(object : ValueEventListener{
            val adapter = GroupAdapter<ViewHolder>()

            override fun onDataChange(dataSnapshot:DataSnapshot) {
                dataSnapshot.children.forEach{
                    val user = it.getValue(User::class.java)
                    Log.d("Data",it.toString())
                    if (user!= null){
                        adapter.add(UserItemAdapter(user))
                        newPesanProgresbar(false)
                    }else{
                        Toast.makeText(this@NewMessageActivity,"kontak kosong",Toast.LENGTH_SHORT).show()
                        newPesanProgresbar(false)
                    }
                    adapter.setOnItemClickListener { item , view ->
                        val useritem = item as UserItemAdapter
                        val intent = Intent(view.context, ChatLogActivity::class.java)
                        intent.putExtra(key_user,useritem.user)
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

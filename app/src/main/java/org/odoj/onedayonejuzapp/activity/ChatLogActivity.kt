package org.odoj.onedayonejuzapp.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import org.odoj.onedayonejuzapp.Extra
import org.odoj.onedayonejuzapp.R
import org.odoj.onedayonejuzapp.adapter.ChatItemFrom
import org.odoj.onedayonejuzapp.adapter.ChatItemTo
import org.odoj.onedayonejuzapp.model.Message
import org.odoj.onedayonejuzapp.model.User
import org.odoj.onedayonejuzapp.utils.Notifikasi

class ChatLogActivity : AppCompatActivity() {
    private var mcurrentUser: User? = null //variable untuk menyimpan user saat ini
    private var toolbar: Toolbar? = null // variable untuk menyimpan toolbar
    private val adapter = GroupAdapter<ViewHolder>() //variable untuk menyimpan adapter
    private var touser: User? = null
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        //set toolbar
        toolbar = findViewById(R.id.toolbar_LogChat)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        touser = intent.getParcelableExtra(Extra)
        supportActionBar?.title = touser?.displayName

        val uid = currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/dataUser/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                mcurrentUser = p0.getValue(User::class.java)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        listerforMeessage()

        button_send_message.setOnClickListener {
            Log.d("chat", "you send message")
            perfomSendMessage()
        }
    }

    private fun listerforMeessage() {
        val fromId = currentUser?.uid
        val touser = intent.getParcelableExtra<User>(Extra)
        val toId = touser!!.uid
        var aku = 0
        var kamu = 0
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val message = dataSnapshot.getValue(Message::class.java)
                Log.d("pesan", message.toString())
                if (message != null) {
                    if (message.fromId == fromId && aku == 0) {
                        aku += 1
                        kamu = 0
                        adapter.add(ChatItemFrom(message, mcurrentUser!!,true))
                    } else if (message.fromId == fromId && aku > 0) {
                        adapter.add(ChatItemFrom(message, mcurrentUser!!,false))
                    } else if (message.fromId != fromId && kamu == 0) {
                        kamu += 1
                        aku = 0
                        adapter.add(ChatItemTo(message, touser,true))
                    }else{
                        adapter.add(ChatItemTo(message, touser,false))
                    }
                }

                //notifikasi
//                Notifikasi(applicationContext).createNotificatiion()
                recycleViewChatLog.adapter = adapter
                recycleViewChatLog.layoutManager = LinearLayoutManager(this@ChatLogActivity)

                recycleViewChatLog.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })
    }

    private fun perfomSendMessage() {
        val text = editText_chatlog.text.toString()
        val fromId = currentUser?.uid
        val user = intent.getParcelableExtra<User>(Extra)
        val toId = user!!.uid

        if (fromId == null) return
        val Toreference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        val Fromreference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val chatMessage =
            Message(Fromreference.key!!, text, toId, fromId, System.currentTimeMillis() / 1000)

        editText_chatlog.text.clear()
        Fromreference.setValue(chatMessage)
            .addOnSuccessListener {
                recycleViewChatLog.scrollToPosition(adapter.itemCount - 1)
                Log.d("chat", "message has been successfully saved id is ${Fromreference.key}")
            }
        Toreference.setValue(chatMessage)


        val lastMessageToRef =
            FirebaseDatabase.getInstance().getReference("/laster-message/$toId/$fromId")
        lastMessageToRef.setValue(chatMessage)

        val lastMessageFromRef =
            FirebaseDatabase.getInstance().getReference("/laster-message/$fromId/$toId")
        lastMessageFromRef.setValue(chatMessage)
    }

    fun cekStatus(networkStatus: String) {

    }
}
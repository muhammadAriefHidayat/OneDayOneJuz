package org.odoj.onedayonejuzapp.adapter

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.list_last_message.view.*
import org.odoj.onedayonejuzapp.R
import org.odoj.onedayonejuzapp.model.Message
import org.odoj.onedayonejuzapp.model.User
import java.text.SimpleDateFormat
import java.util.*

class LastMessageAdapter(val message: Message) : Item<ViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.list_last_message
    }

    var partnerUser: User? = null
    var partnerAdmin: User? = null

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val sdf = SimpleDateFormat("dd/MM/yy")
        val netDate = Date(message.timeStamp)
        val date =sdf.format(netDate)


        viewHolder.itemView.message_lastestMessage.text = message.text
        viewHolder.itemView.time_textview.text = date.toString()

        val chatPartnerId: String
        if (message.toId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = message.fromId
        } else {
            chatPartnerId = message.toId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/dataUser/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                Log.d("chat", p0.value.toString())
                if (p0.value.toString() == "null") {
                    val refAdmin =
                        FirebaseDatabase.getInstance().getReference("/dataAdmin/$chatPartnerId")
                    refAdmin.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            Log.d("chat", snapshot.value.toString())
                            partnerAdmin = snapshot.getValue(User::class.java)
                            viewHolder.itemView.username_lastestMessage.text =
                                partnerAdmin?.displayName
                            Picasso.get().load(partnerAdmin?.image)
                                .placeholder(R.drawable.usersetting)
                                .into(viewHolder.itemView.userImage_lastestMessage)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("chat", "chat canceled3")
                        }

                    })
                }
                partnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.username_lastestMessage.text = partnerUser?.displayName
                Picasso.get().load(partnerUser?.image).placeholder(R.drawable.usersetting)
                    .into(viewHolder.itemView.userImage_lastestMessage)
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("chat", "chat canceled")
            }

        })

    }
}
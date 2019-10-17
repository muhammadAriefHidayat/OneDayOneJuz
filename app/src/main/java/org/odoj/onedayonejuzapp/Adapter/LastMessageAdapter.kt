package org.odoj.onedayonejuzapp.Adapter

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.list_last_message.view.*
import org.odoj.onedayonejuzapp.Model.Message
import org.odoj.onedayonejuzapp.Model.User
import org.odoj.onedayonejuzapp.R

class LastMessageAdapter(val message:Message):Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.list_last_message
    }

    var partnerUser : User? = null
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.message_lastestMessage.text= message.text
        val chatPartnerId : String
        if (message.toId == FirebaseAuth.getInstance().uid){
            chatPartnerId = message.fromId
        }
        else {
            chatPartnerId = message.toId
        }

        val ref =  FirebaseDatabase.getInstance().getReference("/dataUser/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                partnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.username_lastestMessage.text = partnerUser?.displayName
                Picasso.get().load(partnerUser?.image).placeholder(R.drawable.usersetting).into(viewHolder.itemView.userImage_lastestMessage)
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

    }
}
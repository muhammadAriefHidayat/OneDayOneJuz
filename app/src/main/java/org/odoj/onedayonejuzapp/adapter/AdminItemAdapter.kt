package org.odoj.onedayonejuzapp.adapter


import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.list_row_new_message.view.*
import org.odoj.onedayonejuzapp.Extra
import org.odoj.onedayonejuzapp.R
import org.odoj.onedayonejuzapp.activity.ChatLogActivity
import org.odoj.onedayonejuzapp.model.User


class AdminItemAdapter(val user: User, val context: Context) : Item<ViewHolder>() {
    var mDatabase : DatabaseReference? = null

    override fun bind(viewHolder: ViewHolder, position: Int) {
        Log.d("adapterTambah", user.uid)
        val dataAdmin = FirebaseDatabase.getInstance().getReference("dataAdmin/${user.uid}")
        dataAdmin.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val datauser = p0.getValue(User::class.java) ?: return
                Log.d("adapterTambah", datauser.displayName)
                viewHolder.itemView.statusGrup.text = datauser.status
                viewHolder.itemView.userName.text = datauser.displayName
                Picasso.get().load(datauser.image).placeholder(R.drawable.usersetting)
                    .into(viewHolder.itemView.newMessage_imageview)

                viewHolder.itemView.setOnClickListener {
                    val intent = Intent(context, ChatLogActivity::class.java)
                    intent.putExtra(Extra,datauser)
                    context.startActivity(intent)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }

        })

    }

    override fun getLayout(): Int {
        return R.layout.list_row_new_message
    }
}
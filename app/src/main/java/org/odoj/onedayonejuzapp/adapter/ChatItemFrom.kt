package org.odoj.onedayonejuzapp.adapter

import android.view.View
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.list_chat_from.view.*
import org.odoj.onedayonejuzapp.R
import org.odoj.onedayonejuzapp.model.Message
import org.odoj.onedayonejuzapp.model.User
import java.text.SimpleDateFormat
import java.util.*

class ChatItemFrom(val message: Message, val user: User, val pertama: Boolean) :
    Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        val sdf = SimpleDateFormat("hh:mm")
        val netDate = Date(message.timeStamp)
        val jam = sdf.format(netDate)

        viewHolder.itemView.timechat.text = jam.toString()
        viewHolder.itemView.message_chatlog_from.text = message.text

        if (pertama) {
            val uriImage = user.image
            val targetImage = viewHolder.itemView.image_chatlog_ToItem
            Picasso.get().load(uriImage).placeholder(R.drawable.usersetting).into(targetImage)
        } else {
            viewHolder.itemView.image_chatlog_ToItem.visibility = View.INVISIBLE
        }
    }

    fun cekTanggal() {

    }

    override fun getLayout(): Int {

        return if (pertama){
            R.layout.list_chat_from
        }else{
            R.layout.list_chat_from2
        }

    }
}


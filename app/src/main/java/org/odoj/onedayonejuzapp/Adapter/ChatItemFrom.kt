package org.odoj.onedayonejuzapp.Adapter

import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.list_chat_from.view.*
import org.odoj.onedayonejuzapp.Model.Message
import org.odoj.onedayonejuzapp.Model.User
import org.odoj.onedayonejuzapp.R

class ChatItemFrom(val message: Message, val user: User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.message_chatlog_from.text = message.text
        val uriImage = user.image
        val targetImage = viewHolder.itemView.image_chatlog_ToItem

        Picasso.get().load(uriImage).placeholder(R.drawable.usersetting).into(targetImage)


    }
    override fun getLayout(): Int {
        return R.layout.list_chat_from
    }
}


package org.odoj.onedayonejuzapp.adapter

import android.view.View
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.list_chat_to.view.*
import org.odoj.onedayonejuzapp.R
import org.odoj.onedayonejuzapp.model.Message
import org.odoj.onedayonejuzapp.model.User
import java.text.SimpleDateFormat
import java.util.*

class ChatItemTo(val message: Message, val user: User,val pertama:Boolean): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {

        val sdf = SimpleDateFormat("hh:mm")
        val netDate = Date(message.timeStamp)
        val jam =sdf.format(netDate)

        viewHolder.itemView.text_chatTo.text = message.text
        viewHolder.itemView.timechatto.text = jam.toString()

        if (pertama){
            val uriImage = user.image
            val targetImage = viewHolder.itemView.image_chatlog_ToItem
            Picasso.get().load(uriImage).placeholder(R.drawable.usersetting).into(targetImage)
        }else{
            viewHolder.itemView.image_chatlog_ToItem.visibility = View.INVISIBLE
        }


    }
    override fun getLayout(): Int {
        return if (pertama){
            R.layout.list_chat_to
        }else{
            R.layout.list_chat_to2
        }

    }
}
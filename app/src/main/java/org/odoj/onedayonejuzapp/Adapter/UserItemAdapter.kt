package org.odoj.onedayonejuzapp.Adapter


import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.list_row_new_message.view.*
import org.odoj.onedayonejuzapp.Model.User
import org.odoj.onedayonejuzapp.R


class UserItemAdapter(val user: User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.statusUsernameNewMessage.text = user.status
        viewHolder.itemView.userNameNewMessage.text = user.displayName
        Picasso.get().load(user.image).placeholder(R.drawable.usersetting).into(viewHolder.itemView.newMessage_imageview)
    }
    override fun getLayout(): Int {
        return R.layout.list_row_new_message
    }

}
package org.odoj.onedayonejuzapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import org.odoj.onedayonejuzapp.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_chats.*
import org.odoj.onedayonejuzapp.Activity.Chat.ChatLogActivity
import org.odoj.onedayonejuzapp.Activity.Chat.NewMessageActivity
import org.odoj.onedayonejuzapp.Activity.Setting.SettingActivity
import org.odoj.onedayonejuzapp.Activity.Utama.AboutActivity
import org.odoj.onedayonejuzapp.Adapter.LastMessageAdapter
import org.odoj.onedayonejuzapp.Model.Message


class ChatsFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chats,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar_FragmenCha.visibility = View.INVISIBLE

        val linearLayoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,false)
        recyclerView_lastestMessage.adapter = adapter
        recyclerView_lastestMessage.layoutManager = linearLayoutManager
        recyclerView_lastestMessage.addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
        getlastmessage()

        adapter.setOnItemClickListener { item, view ->
            val row = item as LastMessageAdapter

            val intent = Intent(context, ChatLogActivity::class.java)
            intent.putExtra(NewMessageActivity.key_user, row.partnerUser)
            startActivity(intent)
        }
    }

    val adapter = GroupAdapter<ViewHolder>()

    private val lastMessageMap = HashMap<String,Message>()

    private fun refresRecycleViewMessage(){
        adapter.clear()
        lastMessageMap.values.forEach{
            adapter.add(LastMessageAdapter(it))
        }
    }


    private fun getlastmessage() {
        val fromId = FirebaseAuth.getInstance().uid
        val refrence = FirebaseDatabase.getInstance().getReference("/laster-message/$fromId")

        refrence.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(Message::class.java)?: return
                lastMessageMap[p0.key!!] = chatMessage
                Log.d("message",chatMessage.toString())
                refresRecycleViewMessage()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(Message::class.java)?: return
                lastMessageMap[p0.key!!] = chatMessage
                refresRecycleViewMessage()
            }

            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}

        })

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater){
        inflater.inflate(R.menu.main_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
            if(item.itemId == R.id.settingId){
                startActivity(Intent(context, SettingActivity::class.java))
            }

            if(item.itemId == R.id.aboutId){
                startActivity(Intent(context, AboutActivity::class.java))
            }

            if(item.itemId == R.id.kontakMenu){
                startActivity((Intent(context, NewMessageActivity::class.java)))
            }
        return true
    }


}

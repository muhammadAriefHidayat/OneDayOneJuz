package org.odoj.onedayonejuzapp.fragments

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_chats.*
import org.odoj.onedayonejuzapp.Appreff
import org.odoj.onedayonejuzapp.Extra
import org.odoj.onedayonejuzapp.R
import org.odoj.onedayonejuzapp.activity.ChatLogActivity
import org.odoj.onedayonejuzapp.activity.NewMessageActivity
import org.odoj.onedayonejuzapp.activity.ProfilActivity
import org.odoj.onedayonejuzapp.activity.AboutActivity
import org.odoj.onedayonejuzapp.adapter.LastMessageAdapter
import org.odoj.onedayonejuzapp.model.Message


class ChatsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }

    private lateinit var searchView: SearchView
    private var componentName: ComponentName? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onViewCreated(view, savedInstanceState)
        progressBar_FragmenCha.visibility = View.VISIBLE


        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView_lastestMessage.adapter = adapter
        recyclerView_lastestMessage.layoutManager = linearLayoutManager
        recyclerView_lastestMessage.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        getlastmessage()

        adapter.setOnItemClickListener { item, view ->
            val row = item as LastMessageAdapter
            val intent = Intent(context, ChatLogActivity::class.java)
            if (row.partnerUser?.uid.isNullOrEmpty()){
                intent.putExtra(Extra, row.partnerAdmin)
            }else{
                intent.putExtra(Extra, row.partnerUser)
            }
            startActivity(intent)
        }
        newMessage_FAB.setOnClickListener {
            startActivity((Intent(context, NewMessageActivity::class.java)))
        }
    }

    val adapter = GroupAdapter<ViewHolder>()

    private val lastMessageMap = HashMap<String, Message>()

    private fun refresRecycleViewMessage() {
        adapter.clear()
        progressBar_FragmenCha.visibility = View.INVISIBLE
        lastMessageMap.values.forEach {
            if (it.toId != it.fromId){
                adapter.add(LastMessageAdapter(it))
            }
        }
    }


    private fun getlastmessage() {
        val fromId = FirebaseAuth.getInstance().uid

        val referenceUID = fromId?.let {
            FirebaseDatabase.getInstance().getReference("/laster-message")
                .child(Appreff.uid)
        }
        referenceUID?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                progressBar_FragmenCha.visibility = View.INVISIBLE
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.value == null) {
                    Log.d("pesan",p0.value.toString())
                    layout2.visibility = View.VISIBLE
                    layout1.visibility = View.GONE
                    progressBar_FragmenCha.visibility = View.INVISIBLE
                } else {
                    Log.d("pesan2",p0.value.toString())
                    layout1.visibility = View.VISIBLE
                    layout2.visibility = View.GONE
                    progressBar_FragmenCha.visibility = View.INVISIBLE
                }
            }
        })


        Log.d("chatFromid", fromId.toString())
        val refrence =
            FirebaseDatabase.getInstance().getReference("/laster-message").child(fromId!!)
        refrence.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                Log.d("chatFromid", p0.value.toString())
                val chatMessage = p0.getValue(Message::class.java) ?: return
                lastMessageMap[p0.key!!] = chatMessage
                refresRecycleViewMessage()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(Message::class.java) ?: return
                lastMessageMap[p0.key!!] = chatMessage
                refresRecycleViewMessage()
            }

            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}

        })

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
//        val searchManager = context?.getSystemService(SEARCH_SERVICE) as SearchManager
//        searchView = menu.findItem(R.id.searchMessage).actionView as SearchView
//
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
//        searchView.maxWidth = Integer.MAX_VALUE
//        searchView.queryHint = "Cari Pesan"
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                return true
//            }
//        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if (item.itemId == R.id.settingId) {
            startActivity(Intent(context, ProfilActivity::class.java))
        }

        if (item.itemId == R.id.aboutId) {
            startActivity(Intent(context, AboutActivity::class.java))
        }
        return true
    }


}

package org.odoj.onedayonejuzapp.Adapter

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.list_setoran.view.*
import org.odoj.onedayonejuzapp.Model.Laporan
import org.odoj.onedayonejuzapp.R

class SetoranAdapter(val laporan:Laporan, val tanggal :String):Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.tanggalsetoran_ListSetoran.text = tanggal
        viewHolder.itemView.nomorJus_ListSetoran.text = laporan.setorJus
        Log.d("tanggalSetoranAdapter",tanggal)
        Log.d("laporanSetoranAdapter",laporan.toString())

        val uid = laporan.uid
        val ref = FirebaseDatabase.getInstance().getReference("dataUser/$uid")
        ref.addValueEventListener(object :ValueEventListener{

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val Nama = dataSnapshot.child("displayName").value.toString()
                val gambar = dataSnapshot.child("image").value.toString()
                viewHolder.itemView.namaUser_ListSetoran.text = Nama
                val uriImage = gambar
                val imageTarget = viewHolder.itemView.imageUser_ListSetoran
                Picasso.get().load(uriImage).placeholder(R.drawable.usersetting).into(imageTarget)
                Log.d("datasnapshotAdapter",dataSnapshot.toString())

            }

        })

    }

    override fun getLayout(): Int {
        return R.layout.list_setoran
    }
}
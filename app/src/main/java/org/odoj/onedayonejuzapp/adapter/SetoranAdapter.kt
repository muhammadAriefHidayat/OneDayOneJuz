package org.odoj.onedayonejuzapp.adapter

import android.util.Log
import android.widget.Filter
import android.widget.Filterable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.list_setoran.view.*
import org.odoj.onedayonejuzapp.R
import org.odoj.onedayonejuzapp.model.Laporan

class SetoranAdapter(val laporan:Laporan, val tanggal :String):Item<ViewHolder>(),Filterable {


    private var mData = ArrayList<Laporan>()


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
                Log.d("SetoranAdaptergetUser",dataSnapshot.toString())

            }

        })

    }

    override fun getLayout(): Int {
        return R.layout.list_setoran
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString().toLowerCase()
                if (charString.isEmpty()) {
                    mData
                } else {
                    val filteredList = ArrayList<Laporan>()

                    for (row in mData) {
                        if (row.tanggal.toLowerCase().contains(charSequence)) {
                            filteredList.add(row)
                        }
                    }

                    mData = filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = mData
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                mData = filterResults.values as ArrayList<Laporan>
            }
        }
    }
}
package org.odoj.onedayonejuzapp.fragments


import android.app.DatePickerDialog
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_setoran.*
import org.odoj.onedayonejuzapp.Appreff
import org.odoj.onedayonejuzapp.R
import org.odoj.onedayonejuzapp.activity.AboutActivity
import org.odoj.onedayonejuzapp.activity.ChatAdminActivity
import org.odoj.onedayonejuzapp.activity.ProfilActivity
import org.odoj.onedayonejuzapp.adapter.SetoranAdapter
import org.odoj.onedayonejuzapp.model.Laporan
import org.odoj.onedayonejuzapp.model.User
import org.odoj.onedayonejuzapp.utils.Utils
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class SetoranFragment : Fragment() {
    private lateinit var mRef: DatabaseReference
    private val adapter = GroupAdapter<ViewHolder>()
    private lateinit var searchView: SearchView
    private var componentName: ComponentName? = null
    private lateinit var jus: Array<String>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_setoran, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onViewCreated(view, savedInstanceState)

        if (Appreff.grup.isEmpty()) {
            Log.d("grupuser0", "grup kosong")
            val dataUser = FirebaseDatabase.getInstance().getReference("dataUser/${Appreff.uid}")
            dataUser.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java) ?: return
                    Log.d("grupuser1", user.grup)
                    Log.d("grupuser5", user.toString())
                    if (user.grup.isNotEmpty()) {
                        punyaGrup(true)
                        Appreff.grup = user.grup
                        Utils.Progressbar(progressFragmentSetoran, false)
                    } else {
                        Log.d("grupuser2", "gak punya grup")
                        punyaGrup(false)
                        Utils.Progressbar(progressFragmentSetoran, false)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        } else {
            Log.d("grupuser3", "punya grup")
            punyaGrup(true)
            Utils.Progressbar(progressFragmentSetoran, false)
            jus = resources.getStringArray(R.array.Jus)
            val linearLayoutManagerSetoran =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            recycleView_FragmenGrup.layoutManager = linearLayoutManagerSetoran
            recycleView_FragmenGrup.adapter = adapter
        }

        chatAdmin_btn.setOnClickListener {
            val intent = Intent(context, ChatAdminActivity::class.java)
            startActivity(intent)
        }

        getTanggalLaporan()

        btn_setorTilawah.setOnClickListener {
            Utils.Progressbar(progressFragmentSetoran, false)
            builder()
        }

    }


    private fun punyaGrup(boolean: Boolean) {
        if (boolean) {
            relativLayoutData.visibility = View.VISIBLE
            relativeLayoutKosong.visibility = View.GONE
        } else {
            relativeLayoutKosong.visibility = View.VISIBLE
            relativLayoutData.visibility = View.GONE
        }
    }


    private fun builder() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setSingleChoiceItems(jus, 0, null)
            .setPositiveButton("Setor") { dialog, _ ->

                val selectedPosition = (dialog as AlertDialog).listView.checkedItemPosition

                val setoran = jus[selectedPosition]

                setValueLaporan(setoran)

                adapter.clear()

                dialog.dismiss()

                Utils.Progressbar(progressFragmentSetoran, true)

            }.setNegativeButton("Kembali") { dialog, which ->
                dialog.cancel()
            }.show()
    }


    private fun setValueLaporan(setoran: String) {
        val calendar = Calendar.getInstance()
        val currentDate = DateFormat.getDateInstance().format(calendar.time)
        val dateFormatInput = SimpleDateFormat("dd MMM yyyy")
        val hasil = dateFormatInput.parse(currentDate)
        val dateFormatOutput = SimpleDateFormat("yyyy/MM/dd")
        val date = dateFormatOutput.format(hasil)

        val refuser = FirebaseDatabase.getInstance().reference.
        child("Statistik Laporan").child(Appreff.uid).child(date).push()

        mRef = FirebaseDatabase.getInstance().reference.child("Laporan")
            .child(Appreff.grup).child(date).push()
        val laporan = Laporan(date, Appreff.uid, setoran)
        mRef.setValue(laporan)
            .addOnCompleteListener {
                refuser.setValue(Laporan(date, Appreff.uid, setoran))
                Utils.Progressbar(progressFragmentSetoran, false)
                if (it.isSuccessful) {
                    Toast.makeText(context, "anda berhasil menyetor", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Maaf", Toast.LENGTH_LONG).show()
                }
            }
    }


    private fun getTanggalLaporan() {
        val progresbar = requireView().findViewById<ProgressBar>(R.id.progressFragmentSetoran)
        val mref = FirebaseDatabase.getInstance().getReference("Laporan")
            .child(Appreff.grup)
        mref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Utils.Progressbar(progresbar, false)
            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    Log.d("SETORFRAGMENT", it.key.toString())
                    val tanggal = it.key.toString()
                    getValueSetoran(tanggal)
                }
            }
        })
    }


    private fun getValueSetoran(tanggal: String) {
        adapter.notifyDataSetChanged()
        adapter.clear()
        val mref = FirebaseDatabase.getInstance().getReference("Laporan")
            .child(Appreff.grup).child(tanggal)
        mref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Utils.Progressbar(progressFragmentSetoran, false)
            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val Laporan = it.getValue(Laporan::class.java)
                    Log.d("SETORFRAGMENT", tanggal)
                    adapter.add(SetoranAdapter(Laporan!!, tanggal))
                    Utils.Progressbar(view!!.findViewById(R.id.progressFragmentSetoran), false)
                }
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.setoran_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if (item.itemId == R.id.pilihTanggal_menu) {
            datetimeBuilder()
        }
        if (item.itemId == R.id.settingId) {
            startActivity(Intent(context, ProfilActivity::class.java))
        }

        if (item.itemId == R.id.aboutId) {
            startActivity(Intent(context, AboutActivity::class.java))
        }
        return true
    }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    fun datetimeBuilder() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { view, mYear, mMonth, mDay ->
                val tanggal = ("$mDay ${mMonth + 1} $mYear")
                Log.d("SETORFRAGMENT", tanggal)
                val dateFormatInput = SimpleDateFormat("dd MM yyyy")
                val hasil = dateFormatInput.parse(tanggal)
                val dateFormatOutput = SimpleDateFormat("yyyy-MM-dd")
                val date = dateFormatOutput.format(hasil)
                Log.d("SETORFRAGMENT", date)
                adapter.clear()
                getTanggalLaporan()
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}

//    val serachManager = context?.getSystemService(SEARCH_SERVICE) as SearchManager
//    searchView = menu.findItem(R.id.search_id).actionView as SearchView
//    searchView.setSearchableInfo(serachManager.getSearchableInfo(componentName))
//    searchView.maxWidth = Integer.MAX_VALUE
//
//    searchView.queryHint = "Masukan Kata"
//
//    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//        override fun onQueryTextSubmit(query: String?): Boolean {
//            Toast.makeText(context, query, Toast.LENGTH_SHORT).show()
//            return true
//        }
//
//        override fun onQueryTextChange(newText: String?): Boolean {
//            return false
//        }
//    })



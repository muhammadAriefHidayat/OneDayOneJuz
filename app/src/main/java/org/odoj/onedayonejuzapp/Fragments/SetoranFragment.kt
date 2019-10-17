package org.odoj.onedayonejuzapp.Fragments


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_grup.*
import kotlinx.android.synthetic.main.setor_tilawah_dialog.view.*
import org.odoj.onedayonejuzapp.Activity.Setting.SettingActivity
import org.odoj.onedayonejuzapp.Activity.Utama.AboutActivity
import org.odoj.onedayonejuzapp.Adapter.SetoranAdapter
import org.odoj.onedayonejuzapp.Model.Laporan
import org.odoj.onedayonejuzapp.Model.User
import org.odoj.onedayonejuzapp.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap



/**
 * A simple [Fragment] subclass.
 *
 */
class SetoranFragment : Fragment() {

    lateinit var judul:TextView
    lateinit var contoh:TextView
    lateinit var mRef :DatabaseReference
    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_grup,container,false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onViewCreated(view, savedInstanceState)

        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)

        recycleView_FragmenGrup.layoutManager = linearLayoutManager
        recycleView_FragmenGrup.adapter = adapter
        val tanggal = "a"

        getSetoran(tanggal)
        btn_setorTilawah.setOnClickListener {
            builder()

        }

    }

    private fun builder(){
        val builder =  AlertDialog.Builder(context)
        val dialogview = layoutInflater.inflate(R.layout.setor_tilawah_dialog,null)
        judul = dialogview.judul
        contoh = dialogview.contoh

        builder.setView(dialogview)
            .setPositiveButton("Setor"){ dialog, which ->
                val setoran = dialogview.findViewById<EditText>(R.id.editTxt_masukkanJuz).text.toString()
                setor(setoran)
                dialog.dismiss()
            }.setNegativeButton("Kembali"){dialog, which ->
                dialog.cancel()
            }.show()
    }

    val userHasmap = HashMap<String, User>()

    private fun getSetoran(tanggal:String?){
        var date= tanggal
        val a = "a"
        if(date == a){
            val calendar = Calendar.getInstance()
            val currentDate = DateFormat.getDateInstance().format(calendar.time)
            date = currentDate

        }
        val uId= FirebaseAuth.getInstance().currentUser?.uid
        val dataUser = FirebaseDatabase.getInstance().getReference("dataUser/$uId")
        dataUser.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)?:return
                userHasmap[dataSnapshot.key!!] = user
                Log.d("userlaporan",dataSnapshot.toString())
                date?.let { getVaueLaporan(user, it) }
                Log.d("datelaporan",date.toString())
            }
        })
    }

    private fun getVaueLaporan(user: User, date:String) {
        val provinsi = user.provinsi
        val jenisKelamin = user.jenisKelamin

        Log.d("tanggallaporan",date)

        val mref = FirebaseDatabase.getInstance().getReference("Laporan")
            .child(provinsi).child(jenisKelamin).child(date)

        mref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val laporan = p0.getValue(Laporan::class.java)
                    Log.d("Laporanlapor", it.toString())
                    Log.d("tgllaporan", date)
                    adapter.add(SetoranAdapter(laporan!!, date))
                }
            }
        })
    }

    private fun setor(setoran:String) {
        val calendar = Calendar.getInstance()
        val currentDate = DateFormat.getDateInstance().format(calendar.time)

        val uId= FirebaseAuth.getInstance().currentUser?.uid
        val dataUser = FirebaseDatabase.getInstance().getReference("dataUser/$uId")
        dataUser.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val provinsi = dataSnapshot.child("provinsi").value.toString()
                val jenisKelamin = dataSnapshot.child("jenisKelamin").value.toString()
                val uid = uId.toString()
                val setor= setoran

                setValueLaporan(provinsi,jenisKelamin,uid, currentDate, setor)

            }
            override fun onCancelled(p0: DatabaseError){}
        })
    }


    private fun setValueLaporan(provinsi:String, jenisKelamin:String, uid:String, currentDate:String, setoran:String){
        mRef = FirebaseDatabase.getInstance().reference.child("Laporan")
            .child(provinsi).child(jenisKelamin).child(currentDate).child(uid)
        val laporan = Laporan(currentDate,uid,setoran)
        mRef.setValue(laporan)
            .addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(context,"anda berhasil menyetor", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(context,"Maaf", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.setoran_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if (item.itemId == R.id.pilihTanggal_menu){
         datetimeBuilder()
        }
        if(item.itemId == R.id.settingId){
            startActivity(Intent(context, SettingActivity::class.java))
        }

        if(item.itemId == R.id.aboutId){
            startActivity(Intent(context, AboutActivity::class.java))
        }
        return true
    }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    fun datetimeBuilder(){
        val datePickerDialog = DatePickerDialog(context!!,DatePickerDialog.OnDateSetListener{ view, mYear, mMonth, mDay ->
            val tanggal = ("$mDay-${mMonth+1}-$mYear")
            val dateFormatInput = SimpleDateFormat("dd-MM-yyyy")
            val hasil = dateFormatInput.parse(tanggal)
            val dateFormatOutput = SimpleDateFormat("dd MMM yyyy")
            val date = dateFormatOutput.format(hasil)
            Log.d("tanggal", date)

            getSetoran(date)
        },year,month,day)
        datePickerDialog.show()
    }

}
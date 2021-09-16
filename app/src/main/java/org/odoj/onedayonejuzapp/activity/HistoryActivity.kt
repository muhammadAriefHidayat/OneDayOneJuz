package org.odoj.onedayonejuzapp.activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.*
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_history.*
import org.odoj.onedayonejuzapp.Appreff
import org.odoj.onedayonejuzapp.R
import org.odoj.onedayonejuzapp.adapter.HistoryAdapter
import org.odoj.onedayonejuzapp.model.Laporan
import org.odoj.onedayonejuzapp.utils.Utils
import java.util.*
import kotlin.collections.ArrayList


class HistoryActivity : AppCompatActivity() {

    private val adapter = GroupAdapter<ViewHolder>()
    private val adapterpresensi = GroupAdapter<ViewHolder>()
    var mDatabase: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setBarChartValues()

        val linearLayoutManagerSetoran =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyleviuewHistory.layoutManager = linearLayoutManagerSetoran
        recyleviuewHistory.adapter = adapter

        var tahun = Calendar.getInstance().get(Calendar.YEAR);
        getData(tahun)
    }

    fun getData(tahun: Int) {
        val xValues = ArrayList<String>()
        xValues.add("Jan")
        xValues.add("Feb")
        xValues.add("Mar")
        xValues.add("Apr")
        xValues.add("Mei")
        xValues.add("Jun")
        xValues.add("Jul")
        xValues.add("Agus")
        xValues.add("Sept")
        xValues.add("Okt")
        xValues.add("Nov")
        xValues.add("Des")

        val barValue = ArrayList<BarEntry>()


        var satubulan = 0
        var totalLaporan = 0
        var bulanLaporan = 0
        val databasepress =
            FirebaseDatabase.getInstance().reference.child("Statistik Laporan/${Appreff.uid}/$tahun")
        databasepress.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val monthLaporan = snapshot.childrenCount.toInt()
                snapshot.children.forEach { bulan ->
                    bulanLaporan += 1
                    val month = bulan.key.toString()
                    var totalhari = bulan.childrenCount
                    bulan.children.forEach { date ->
                        satubulan += 1
                        totalLaporan += date.childrenCount.toInt()
                        var tanggal = date.key.toString()
                        if (satubulan == totalhari.toInt()) {
                            for (x in 0..1) {
                                for (y in 0..9) {
                                    Log.d("xy", "$x$y")
                                    if ("$x$y" !== "00" && "$x$y" == month && x == 0) {
                                        barValue.add(BarEntry(totalLaporan.toFloat(), y - 1))

                                    } else if ("$x$y" !== "00" && "$x$y" == month && x == 1) {
                                        var index = "$x$y".toInt()

                                        barValue.add(BarEntry(totalLaporan.toFloat(), index - 1))
                                    } else if ("$x$y" == "13") {
                                        totalLaporan = 0
                                        satubulan = 0
                                        break
                                    }
                                }
                            }
                            if (monthLaporan == bulanLaporan) {
                                var barDataset = BarDataSet(barValue, "History tilawah")
                                barDataset.color = resources.getColor(R.color.md_green_600)
                                var data = BarData(xValues, barDataset)

                                barChart.data = data
                                barChart.animateXY(3000, 3000)
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun setBarChartValues() {
        val tahun = Calendar.getInstance().get(Calendar.YEAR);
        val datahistory =
            FirebaseDatabase.getInstance().reference.child("Statistik Laporan/${Appreff.uid}/$tahun")
        datahistory.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    var bulan = it.key.toString()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun getPresensiSetoran(tahun: String, bulan: String) {
        adapterpresensi.notifyDataSetChanged()
        adapterpresensi.clear()
        adapter.notifyDataSetChanged()
        adapter.clear()

        val mref = FirebaseDatabase.getInstance().getReference("Statistik Laporan/${Appreff.uid}")
            .child("$tahun/$bulan")
        mref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Utils.Progressbar(progressbarhistory, false)
            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.d("LaporanHistory3", p0.toString())
                p0.children.forEach {
                    Log.d("LaporanHistory4", it.toString())
                    var tanggal = it.key.toString()

                    it.children.forEach { ay ->
                        val Laporan = ay.getValue(Laporan::class.java)
                        adapter.add(HistoryAdapter(Laporan!!))
                        Utils.Progressbar(findViewById(R.id.progressbarhistory), false)
                    }
                }
            }
        })
    }

//    presensi
//val calendar: Calendar = Calendar.getInstance()
    //get max day in this month
//    calendar.set(tahun.toInt(),bulan.toInt(),1)
//    val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

//    var angka = ""
//    var hari = tanggal.count()
//    angka = if (hari == 1) {
//        tanggal[1].toString()
//    } else {
//        tanggal
//    }
//
//    for (x in 1..maxDay) {
//        if (angka == x.toString()) {
//            Log.d("tilawah","sudah Laporan")
////                                adapterpresensi.add(PresensiTilawahAdapter(Laporan,true))
//        }else{
//            Log.d("tilawah","tidak laporan")
////                                adapterpresensi.add(PresensiTilawahAdapter(Laporan,true))
//        }
//    }

    private fun createDialogWithoutDateField(): DatePickerDialog? {
        val dpd = DatePickerDialog(this, null, 2014, 1, 24)
        try {
            val datePickerDialogFields = dpd.javaClass.declaredFields
            for (datePickerDialogField in datePickerDialogFields) {
                if (datePickerDialogField.name == "mDatePicker") {
                    datePickerDialogField.isAccessible = true
                    val datePicker = datePickerDialogField[dpd] as DatePicker
                    val datePickerFields = datePickerDialogField.type.declaredFields
                    for (datePickerField in datePickerFields) {
                        Log.i("test", datePickerField.name)
                        if ("mDaySpinner" == datePickerField.name) {
                            datePickerField.isAccessible = true
                            val dayPicker = datePickerField[datePicker]
                            (dayPicker as View).setVisibility(View.GONE)
                        }
                    }
                }
            }
        } catch (ex: Exception) {
        }
        return dpd
    }

    fun pilihbulan(view: View) {
        createDialogWithoutDateField()!!.show();
    }

}
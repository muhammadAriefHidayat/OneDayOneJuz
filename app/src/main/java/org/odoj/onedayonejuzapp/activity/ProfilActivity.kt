package org.odoj.onedayonejuzapp.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_profil.*
import org.odoj.onedayonejuzapp.Appreff
import org.odoj.onedayonejuzapp.R
import org.odoj.onedayonejuzapp.adapter.HistoryAdapter
import org.odoj.onedayonejuzapp.adapter.SetoranAdapter
import org.odoj.onedayonejuzapp.model.Laporan
import org.odoj.onedayonejuzapp.utils.Utils

class ProfilActivity : AppCompatActivity() {

    var mDatabase: DatabaseReference? = null
    var mCurrentUser: FirebaseUser? = null
    var mStorageRef: StorageReference? = null
    var toolbar_setting: Toolbar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        mCurrentUser = FirebaseAuth.getInstance().currentUser
        mStorageRef = FirebaseStorage.getInstance().reference

        initToolbar()

        nameTextView.text = Appreff.nama
        emailTextView.text = Appreff.email

        if (Appreff.foto != "") {
            Glide.with(this)
                .load(Appreff.foto)
                .placeholder(R.drawable.baseline_image_black_24)
                .into(profileImageView);
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if (item.itemId == R.id.logout) {
            alertDialog() //memanggil fungsi Alert Dialog
        }
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data!!.data

            CropImage.activity(imageUri)
                .setAspectRatio(1, 1)
                .start(this)
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)

            if (resultCode == Activity.RESULT_OK) {

//                SettingSpinner(true)
                val resultUri = result.uri
                val userId = mCurrentUser!!.uid

                val filePath = mStorageRef!!.child("chat_profil_images")
                    .child("$userId.jpg")

                /**
                 * mengupload file berupa URI image dan memanggil fungsi savetofirebase
                 * */
                filePath.putFile(resultUri).addOnSuccessListener {
                    filePath.downloadUrl.addOnSuccessListener {
                        Log.d("SettingActivity", "$it")
                        savetofirebase("$it")
                    }
                }
            }
        }
    }


    private fun savetofirebase(image: String) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().reference.child("dataUser").child((userId))
        ref.child("image").setValue(image).addOnSuccessListener {
//            SettingSpinner(false) //set Spinner False
            Log.d("SettingActivity", "alhamdulilaah")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_logout, menu) //inflate atau menggabungkan menu
        return true
    }


    private fun alertDialog() {
        val alertLogout = AlertDialog.Builder(this)
        alertLogout.setTitle("Logout")
        alertLogout.setMessage("Anda Yakin Ingin Keluar?")
        alertLogout.setPositiveButton("Keluar") { dialog: DialogInterface?, which: Int ->
            Appreff.email = ""
            Appreff.jeniskelamin = ""
            Appreff.provinsi = ""
            Appreff.nama = ""
            Appreff.grup = ""
            Appreff.uid = ""
            FirebaseAuth.getInstance().signOut() //signout User
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        alertLogout.setNegativeButton("Tidak", null)
        alertLogout.show() //Show alertDialog
    }


    private fun initToolbar() {
        toolbar_setting = findViewById(R.id.toolbar_setting)
        toolbar_setting?.setNavigationIcon(R.drawable.ic_arrow_back)
        if (toolbar_setting?.navigationIcon != null) {
            toolbar_setting?.navigationIcon?.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.white
                ), PorterDuff.Mode.SRC_ATOP
            )
        }
        toolbar_setting?.title = "Profil"

        try {
            toolbar_setting?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        } catch (e: Exception) {
            Log.e("TEAMPS", "Can't set color.")
        }
        setSupportActionBar(toolbar_setting)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

}

package org.odoj.onedayonejuzapp.Activity.Setting

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_setting.*
import org.odoj.onedayonejuzapp.Activity.Utama.MainActivity
import org.odoj.onedayonejuzapp.R
import java.io.File


/**
 *
 * @author Muhammad arief Hidayat
 *
 *
 * Class Setting Activiti digunakan untuk melakukan edit user yang didalamnya dapat mengubah Status dan Photo pada databse
 * firabse kemudian menampilkannya kedalam layout
 * Class Setting Activity juga dapat melakukan Logout User.
 * library yang digunakan pada Class settingActivity adalah Firebase Databse, firebase Storage dan firebase Authentifikasi
 * terdapat parameter
 * */
class SettingActivity : AppCompatActivity() {
        /**
     *
     * deklarasi type variable
     * */
    var mDatabase : DatabaseReference? = null
    var mCurrentUser : FirebaseUser? = null
    var mStorageRef : StorageReference? = null
    var toolbar : Toolbar? =null

    /**
     * oncreate ini untuk menampilakn interface layout dan memanggil fungsi lain pada saat activity dibuat.
     * Dalam fungsi ini menampilkan toolbar, foto berupa image, button EditPicture dan juga editStatus yang tersambung
     * pada databse firebase
     * */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        toolbar = findViewById(R.id.toolbar_setting)  //get toolbar setting
        setSupportActionBar(toolbar) //setToolbar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true) //setDisplay home button atau back button

        mCurrentUser = FirebaseAuth.getInstance().currentUser // set variable mCurrentUser dengan FirebaseAuth
        mStorageRef = FirebaseStorage.getInstance().reference //set variable mStorageRef degan firebaseStograge

        val userId = mCurrentUser!!.uid // set variable UserId dengan uid current user

        mDatabase = FirebaseDatabase.getInstance().reference
            .child("dataUser")
            .child(userId)

        mDatabase!!.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val displayName = dataSnapshot.child("displayName").value //get value display name
                val userImage = dataSnapshot.child("image").value.toString() //getUser Image Value
                val userStatus = dataSnapshot.child("status").value //getUserStatusValue

                nameSettingTxt.text = displayName.toString()
                statusUserSetting.text = userStatus.toString()

                Picasso.get()
                    .load(userImage)
                    .placeholder(R.drawable.usersetting)
                    .into(userSetting)

                SettingSpinner(false)
            }

            override fun onCancelled(databaseErrorSnapshot: DatabaseError) {}
        })

        buttonEditStatus.setOnClickListener {
            val intentEditStatus = Intent(this, EditStatusActivity::class.java)
            intentEditStatus.putExtra("status",statusUserSetting.text.toString().trim())
            startActivity(intentEditStatus)
        }

        /**
         * Button Change Picture Profil berguna untuk megambil foto pada galery memory android
         * */
        buttonChangePicturProfil.setOnClickListener {
            val intentGalery = Intent(Intent.ACTION_PICK)
            intentGalery.type = "image/*"

            startActivityForResult(Intent.createChooser(intentGalery,"Pilih foto"),0)
        }

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK){
            val imageUri: Uri? = data!!.data

            CropImage.activity(imageUri)
                .setAspectRatio(1,1)
                .start(this)
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
             val result = CropImage.getActivityResult(data)

            if (resultCode == Activity.RESULT_OK) {

                SettingSpinner(true)
                val resultUri = result.uri
                val userId = mCurrentUser!!.uid
                val thumbfile = File(resultUri.path)


                /**
                 * creat directory for image pada database Firebase menggunakan Library Firebase Storage tengan direktori
                 * chat_profil_image/userid.jpg
                 * */
                val filePath = mStorageRef!!.child("chat_profil_images")
                    .child(userId + ".jpg")

                /**
                 * mengupload file berupa URI image dan memanggil fungsi savetofirebase
                 * */
                filePath.putFile(resultUri).addOnSuccessListener {
                    filePath.downloadUrl.addOnSuccessListener {
                        Log.d("SettingActivity","$it")
                        savetofirebase("$it")
                    }
                }
            }
        }
    }


    private fun savetofirebase(image:String) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid?:""
        val ref = FirebaseDatabase.getInstance().reference.child("User").child((userId))
        val Image = image
        ref.child("image").setValue(Image).addOnSuccessListener {
            SettingSpinner(false) //set Spinner False
            Log.d("SettingActivity","alhamdulilaah")
        }
    }

    fun SettingSpinner(enable:Boolean){
        if (enable){
            settingSpinner.visibility = View.VISIBLE //set visibiliti Spinner visible
        }else{
            settingSpinner.visibility = View.INVISIBLE //set visibiliti Spinner invisibel
        }
        buttonEditStatus.isEnabled = !enable //set buttonEditStatus is !enable
        buttonChangePicturProfil.isEnabled = !enable //set buttonChangePictureProfil is !enable
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_logout,menu) //inflate atau menggabungkan menu
        return true
    }

    /**
     * @param item
     * optionItemSelected berfungsi untuk memilih menu yang ditampilkan
     * setelah menu diklik maka akan memanggil prosedur AlertDialog
     * */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)

        if (item?.itemId == R.id.logout){
            alertDialog() //memanggil fungsi Alert Dialog
        }
        return true
    }


    private fun alertDialog(){
        val alertLogout = AlertDialog.Builder(this)
        alertLogout.setTitle("Logout")
        alertLogout.setMessage("Anda Yakin Ingin Keluar?")
        alertLogout.setPositiveButton("Keluar"){ dialog: DialogInterface?, which: Int ->
            FirebaseAuth.getInstance().signOut() //signout User
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        alertLogout.setNegativeButton("Tidak",null)
        alertLogout.show() //Show alertDialog
    }
}

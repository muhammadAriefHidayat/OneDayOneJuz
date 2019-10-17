package org.odoj.onedayonejuzapp.Activity.Utama


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_dash_board.*
import org.odoj.onedayonejuzapp.Activity.Chat.NewMessageActivity
import org.odoj.onedayonejuzapp.Activity.Setting.SettingActivity
import org.odoj.onedayonejuzapp.Adapter.SectionPagerAdapter
import org.odoj.onedayonejuzapp.R

class DashBoard : AppCompatActivity() {

    var toolbar:Toolbar? = null
    var user : FirebaseUser? = null
    var mAuth: FirebaseAuth? = null
    var mAuthListener : FirebaseAuth.AuthStateListener? = null
   private var sectionpageAdapter : SectionPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)

        // pengecekan user terdaftar atau tidak
        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener {
                firebaseAuth: FirebaseAuth ->
            user = firebaseAuth.currentUser

            if (user == null){
                val intent= Intent(this, MainActivity:: class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        //set toolbar
        toolbar = findViewById(R.id.toolbar_id)
        setSupportActionBar(toolbar)

        //set fragment
        sectionpageAdapter = SectionPagerAdapter(supportFragmentManager)
        dashViewPagger.adapter = sectionpageAdapter
        tablayoutid.setupWithViewPager(dashViewPagger)
    }


    /**
     * set mAuth start ketika onStart
     * */
    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(mAuthListener!!)
    }


    /**
     * remove AuthListener ketika onstop
     * */
    override fun onStop() {
        super.onStop()
        if (mAuthListener != null){
            mAuth!!.removeAuthStateListener(mAuthListener!!)
        }
    }

    /**
     * inflate menu
//     * */


}

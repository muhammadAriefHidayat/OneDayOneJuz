package org.odoj.onedayonejuzapp.activity


import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.ui_navigation_drawer_basic_navigation_drawer_activity.*
import org.odoj.onedayonejuzapp.R
import org.odoj.onedayonejuzapp.adapter.SectionPagerAdapter
import org.odoj.onedayonejuzapp.utils.Utils

class DashBoard : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    internal lateinit var toolbar: Toolbar
    var user: FirebaseUser? = null
    var mAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var sectionpageAdapter: SectionPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ui_navigation_drawer_basic_navigation_drawer_activity)
        //set toolbar

        initUI()

        //set fragment
        sectionpageAdapter = SectionPagerAdapter(supportFragmentManager)
        dashViewPagger.adapter = sectionpageAdapter
        tablayoutid.setupWithViewPager(dashViewPagger)
    }

    private fun initUI() {
        initToolbar()

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        if (Utils.isRTL) {
            navigationView.textDirection = View.TEXT_DIRECTION_RTL
        } else {
            navigationView.textDirection = View.TEXT_DIRECTION_LTR
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.history_menu) {
            startActivity(Intent(this,HistoryActivity::class.java))
        } else if (id == R.id.alquran) {

        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun initToolbar() {

        toolbar = findViewById(R.id.toolbar_id)


        if (toolbar.navigationIcon != null) {
            toolbar.navigationIcon?.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.md_white_1000
                ), PorterDuff.Mode.SRC_ATOP
            )
        }

        toolbar.title = "One Day One Juz"

        try {
            toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.md_white_1000))
        } catch (e: Exception) {
            Log.e("TEAMPS", "Can't set color.")
        }

        try {
            setSupportActionBar(toolbar)
        } catch (e: Exception) {
            Log.e("TEAMPS", "Error in set support action bar.")
        }

        try {
            if (supportActionBar != null) {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
        } catch (e: Exception) {
            Log.e("TEAMPS", "Error in set display home as up enabled.")
        }

    }
}

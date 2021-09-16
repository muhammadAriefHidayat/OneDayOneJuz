package org.odoj.onedayonejuzapp.activity

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_splash_screen.*
import org.odoj.onedayonejuzapp.Appreff
import org.odoj.onedayonejuzapp.R
import org.odoj.onedayonejuzapp.utils.Utils

class SplashScreenActivity : AppCompatActivity() {
    var isRunning: Boolean = false
    var user: FirebaseUser? = null
    var mAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        setImageToImageView(this, splashScreen, R.drawable.bacground_splash)

        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth: FirebaseAuth ->
            user = firebaseAuth.currentUser
            Log.d("ayamg", user?.uid.toString())
        }
        exploreButton.setOnClickListener {
            val progresbarSplas = findViewById<ProgressBar>(R.id.splashScreen_Progressbar)
            Utils.Progressbar(progresbarSplas, true)
            Log.d("ayam", user.toString())
            if (user != null) {
                val uid = user!!.uid
                Appreff.uid = uid
                val dataUser = FirebaseDatabase.getInstance().getReference("dataUser/$uid")
                dataUser.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.value.toString() == "null") {
                            FirebaseAuth.getInstance().signOut()
                            Utils.Peringatan(
                                "harap login dengan akun User",
                                this@SplashScreenActivity
                            )
                            Utils.Progressbar(progresbarSplas, false)
                            startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
                        } else {
                            val intents = Intent(this@SplashScreenActivity, DashBoard::class.java)
                            intents.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intents)
                            finish()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Utils.Progressbar(progresbarSplas, false)
                        Utils.Peringatan(
                            error.toString(),
                            this@SplashScreenActivity
                        )
                    }
                })

            } else {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }

    fun setImageToImageView(context: Context, imageView: ImageView, drawable: Int) {
        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
            .skipMemoryCache(true)

        Glide.with(context)
            .load(drawable)
            .apply(requestOptions)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        //Here you can get the size!

        if (!isRunning) {
            isRunning = true
            val width = Resources.getSystem().displayMetrics.widthPixels
            val height = Resources.getSystem().displayMetrics.heightPixels

            val halfWidth = width / 2
            val halfHeight = height / 2

            val px50 = dpToPx(this, 70)
            val px16 = dpToPx(this, 30)
            val px120 = dpToPx(this, 120)

            val iconToX = halfWidth - px50
            val iconToY = halfHeight - px120


            iconImageView.animate().alpha(1f).translationX(iconToX.toFloat())
                .translationY(iconToY.toFloat()).setDuration(1500)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animator: Animator?) {

                    }

                    override fun onAnimationEnd(animator: Animator?) {

                        exploreButton.animate().translationY(height.toFloat()).setDuration(0)
                            .setListener(object : Animator.AnimatorListener {
                                override fun onAnimationStart(animator: Animator?) {

                                }

                                override fun onAnimationEnd(animator: Animator?) {
                                    exploreButton.animate()
                                        .translationY((halfHeight + px120).toFloat()).alpha(1f)
                                        .setDuration(800).start()
                                }

                                override fun onAnimationCancel(animator: Animator?) {

                                }

                                override fun onAnimationRepeat(animator: Animator?) {

                                }
                            }).start()


                    }

                    override fun onAnimationCancel(animator: Animator?) {

                    }

                    override fun onAnimationRepeat(animator: Animator?) {

                    }
                }).start()

            val textToX = halfWidth - nameTextView.width / 2
            val textToY = halfHeight + px16


            nameTextView.animate().alpha(0f).translationY(height.toFloat())
                .translationX(width.toFloat()).setDuration(0)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animator: Animator?) {

                    }

                    override fun onAnimationEnd(animator: Animator?) {
                        nameTextView.animate().alpha(1f).translationX(textToX.toFloat())
                            .translationY(textToY.toFloat()).setDuration(1500).start()
                    }

                    override fun onAnimationCancel(animator: Animator?) {

                    }

                    override fun onAnimationRepeat(animator: Animator?) {

                    }
                }).start()

        }
    }

    fun dpToPx(context: Context, dp: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(mAuthListener!!)
    }

    override fun onPause() {
        super.onPause()
        Utils.Progressbar(splashScreen_Progressbar, false)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth!!.removeAuthStateListener(mAuthListener!!)
        }
    }
}

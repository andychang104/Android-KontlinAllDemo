package m104vip.com.kttest

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.annotation.NonNull
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_bottomnavigationview.*
import kotlinx.android.synthetic.main.activity_collapsingtoolbars.*
import android.util.Rational
import android.app.PictureInPictureParams
import android.support.design.bottomappbar.BottomAppBar
import android.support.design.button.MaterialButton
import android.support.design.widget.AppBarLayout
import android.support.v4.widget.NestedScrollView
import android.widget.Button
import android.widget.ImageView


class CollapsingToolbarsActivity : Activity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collapsingtoolbars)


        btn_test.setOnClickListener({
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val aspectRatio = Rational(5, 10)
                    enterPictureInPictureMode( PictureInPictureParams.Builder().setAspectRatio(aspectRatio).build())
                } else {
                    TODO("VERSION.SDK_INT < O")
                }



            }

        })

    }
    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean,
                                               newConfig: Configuration) {
        if (isInPictureInPictureMode) {

            scroll_view.visibility = View.GONE
            appbar_view.visibility = View.GONE
            iv_bg.visibility = View.VISIBLE
            // Hide the full-screen UI (controls, etc.) while in picture-in-picture mode.
        } else {
            scroll_view.visibility = View.VISIBLE
            appbar_view.visibility = View.VISIBLE
            iv_bg.visibility = View.GONE
            // Restore the full-screen UI.
        }
    }

    override fun onPause() {
        super.onPause()
        // If called while in PIP mode, do not pause playback
        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    isInPictureInPictureMode
                } else {
                    TODO("VERSION.SDK_INT < N")
                }) {
            // Continue playback
        } else {
            // Use existing playback logic for paused Activity behavior.
        }
    }





}
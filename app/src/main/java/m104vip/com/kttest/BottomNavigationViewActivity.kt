package m104vip.com.kttest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.annotation.NonNull
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_bottomnavigationview.*


class BottomNavigationViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottomnavigationview)

        tvMsg.setText("首頁");

        bottomView.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                tvMsg.setText(item.title);
                // you can return false to cancel select
                return true
        }
        })
    }

}
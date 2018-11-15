package m104vip.com.kttest

import android.os.Bundle
import android.support.design.bottomappbar.BottomAppBar
import android.support.design.button.MaterialButton
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_bottomnavigationview.*

class BottomAppBarActivity : AppCompatActivity() {

    var bottomAppBar: BottomAppBar? = null
    var materialButton: MaterialButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottomappbar)

        bottomAppBar = findViewById<BottomAppBar>(R.id.bottom_appbar)
        materialButton = findViewById<MaterialButton>(R.id.materialButton)

        bottomAppBar?.replaceMenu(R.menu.my_menu)
        materialButton?.setOnClickListener(View.OnClickListener {
            if (bottomAppBar?.getFabAlignmentMode()==BottomAppBar.FAB_ALIGNMENT_MODE_CENTER)
                bottomAppBar?.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END)
            else {
                bottomAppBar?.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER)
            }
        })

    }

}
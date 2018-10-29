package m104vip.com.kttest

import android.content.Context
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_kontlin.*
import android.graphics.Bitmap
import android.graphics.Matrix
import android.widget.Toast

class KontlinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kontlin)

        var imageUri = intent.getStringExtra("imageUri")
        var imageAngle = intent.getIntExtra("imageAngle",0)

        toast(imageUri);

        var bitmap = BitmapFactory.decodeFile(imageUri)

        val mtx = Matrix()
        mtx.postRotate(imageAngle.toFloat())

        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, mtx, true);

        ivTest.setImageBitmap(bitmap);
    }

    fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}

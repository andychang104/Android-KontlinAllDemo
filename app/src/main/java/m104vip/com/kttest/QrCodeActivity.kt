package m104vip.com.kttest

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_qrcode.*
import kotlinx.android.synthetic.main.activity_webview.*
import java.io.IOException
import java.util.*

class QrCodeActivity: AppCompatActivity(){

    private var mQRBitmap : Bitmap? = null
    private var mTextInput : String? = null

    val WRITE_PERMISSION = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)


        var data = intent.getStringExtra("data")

        mQRBitmap = generateQR(data)
        if (mQRBitmap != null){
            btnSave.visibility = View.VISIBLE
            mTextInput = data
            ivQrCode.setImageBitmap(mQRBitmap)
        }else{
            btnSave.visibility = View.GONE
            ivQrCode.setImageBitmap(null)
            mQRBitmap = null
        }


        btnSave.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED){
                    val strings = Array<String>(1) { "android.permission.WRITE_EXTERNAL_STORAGE" }
                    requestPermissions(strings, WRITE_PERMISSION)
                    return@setOnClickListener
                }
            }

            CapturePhotoUtils.insertImage(contentResolver, mQRBitmap, Date().time.toString(), this.mTextInput!!)
        }


    }

    object CapturePhotoUtils {

        fun insertImage(cr: ContentResolver,
                        source: Bitmap?,
                        title: String,
                        description: String): String? {

            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, title)
            values.put(MediaStore.Images.Media.DISPLAY_NAME, title)
            values.put(MediaStore.Images.Media.DESCRIPTION, description)
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())

            var url: Uri? = null
            var stringUrl: String? = null

            try {
                url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

                if (source != null) {
                    assert(url != null)
                    val imageOut = cr.openOutputStream(url!!)
                    try {
                        source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut)
                    } finally {
                        assert(imageOut != null)
                        imageOut!!.close()
                    }

                    val id = ContentUris.parseId(url)
                    val miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null)
                    storeThumbnail(cr, miniThumb, id, MediaStore.Images.Thumbnails.MICRO_KIND)
                } else {
                    assert(url != null)
                    cr.delete(url!!, null, null)
                    url = null
                }
            } catch (e: Exception) {
                if (url != null) {
                    cr.delete(url, null, null)
                    url = null
                }
            }

            if (url != null) {
                stringUrl = url.toString()
            }

            return stringUrl
        }

        private fun storeThumbnail(
                cr: ContentResolver,
                source: Bitmap,
                id: Long,
                kind: Int) {

            val matrix = Matrix()

            val scaleX = 50f / source.width
            val scaleY = 50f / source.height

            matrix.setScale(scaleX, scaleY)

            val thumb = Bitmap.createBitmap(source, 0, 0,
                    source.width,
                    source.height, matrix,
                    true
            )

            val values = ContentValues(4)
            values.put(MediaStore.Images.Thumbnails.KIND, kind)
            values.put(MediaStore.Images.Thumbnails.IMAGE_ID, id.toInt())
            values.put(MediaStore.Images.Thumbnails.HEIGHT, thumb.height)
            values.put(MediaStore.Images.Thumbnails.WIDTH, thumb.width)

            val url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values)

            try {
                assert(url != null)
                val thumbOut = cr.openOutputStream(url!!)
                thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut)
                assert(thumbOut != null)
                thumbOut!!.close()
            } catch (ignored: IOException) {
            }

        }
    }
}
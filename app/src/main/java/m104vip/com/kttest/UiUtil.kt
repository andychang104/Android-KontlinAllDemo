package m104vip.com.kttest

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.common.CharacterSetECI
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.*

var density: Float = 0.toFloat()

//統一客製化Toast
fun showToast(activity: Activity,msg: String,src: Int,ivWidth: Int,ivHeight: Int) {
    //inflater可以把xml的資源轉成view
    val inflater = activity.layoutInflater
    val layout = inflater.inflate(R.layout.all_toast,activity.findViewById<View>(R.id.rltSwitchOkBg) as? ViewGroup)

    val ivSwitch = layout.findViewById(R.id.ivSwitch) as ImageView
    val tvSwitch = layout.findViewById(R.id.tvSwitch) as TextView

    val params = ivSwitch.layoutParams
    params.width = ivWidth
    params.height = ivHeight

    ivSwitch.layoutParams = params
    tvSwitch.text = msg
    ivSwitch.setBackgroundResource(src)

    val toast = Toast(activity)
    //設定顯示位置
    toast.setGravity(Gravity.CENTER_VERTICAL,0,0)
    //設定持續時間
    toast.duration = Toast.LENGTH_LONG
    //設定顯示的view
    toast.view = layout
    //使用show()把Toast顯示出來
    toast.show()
}

//dp轉px
fun dpToPix(ct: Context,dp: Float): Int {
    density = ct.getResources().getDisplayMetrics().density
    return (dp * density + 0.5f).toInt()
}


//創建Qrcode圖片
@Throws(WriterException::class)
fun generateQR(value: String) : Bitmap? {
    val bitMatrix: BitMatrix
    try {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        hints[EncodeHintType.CHARACTER_SET] = CharacterSetECI.UTF8

        bitMatrix = MultiFormatWriter().encode(
                value,
                BarcodeFormat.QR_CODE,
                500,
                500,
                hints
        )
    }catch (Illegalargumentexception : IllegalArgumentException){
        return null
    }

    val bitMatrixWidth = bitMatrix.width

    val bitMatrixHeight = bitMatrix.height

    val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)

    for (y in 0 until bitMatrixHeight) {
        val offset = y * bitMatrixWidth

        for (x in 0 until bitMatrixWidth) {

            pixels[offset + x] = if (bitMatrix.get(x, y))
                Color.BLACK
            else
                Color.WHITE
        }
    }
    val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)
    bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight)

    return bitmap
}
package m104vip.com.kttest

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

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
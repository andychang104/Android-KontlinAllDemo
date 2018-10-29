package m104vip.com.kttest

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import kotlinx.android.synthetic.main.activity_spannablestring.*

class SpannableStringActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spannablestring)

        val spannable = SpannableStringBuilder("摳你手心，是沒辦法說服自己放下愛你的心\n" +
                "堅持加滿，是因為吃三明治可能噎到會難過")
        //spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.top_tab1)),14,21, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(TextClick(),20,24,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvSpannableString.setMovementMethod(LinkMovementMethod.getInstance())
        tvSpannableString.setText(spannable)

    }

    private inner class TextClick : ClickableSpan() {
        override fun onClick(widget: View) {
            //在此處理點擊事件
            val intent = Intent()
            intent.setClass(this@SpannableStringActivity,WebViewActivity::class.java)
            intent.putExtra("url","http://www.104.com.tw")
            startActivity(intent)
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.color = ContextCompat.getColor(this@SpannableStringActivity, android.R.color.holo_blue_light)
            val font = Typeface.create(Typeface.DEFAULT,Typeface.BOLD)
            ds.typeface = font
        }
    }

}
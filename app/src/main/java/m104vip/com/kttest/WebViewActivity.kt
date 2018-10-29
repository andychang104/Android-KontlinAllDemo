package m104vip.com.kttest


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_webview.*

class WebViewActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        var url = intent.getStringExtra("url")
        webData.settings.javaScriptEnabled = true
        webData.setWebViewClient(WebViewClient())
        webData.loadUrl(url)


    }
}
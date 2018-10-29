package m104vip.com.kttest

import android.app.Activity
import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.VideoView
import android.widget.MediaController

class VideoViewActivity : Activity() {

    private lateinit var videoView: VideoView
    private var mediacontroller: MediaController? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videoview)

        setupVideoView()
    }

    private fun setupVideoView() {

        val dialog = ProgressDialog(this)
        dialog.setMessage("載入中")
        dialog.show()

        var uri:Uri? = null;


        uri = Uri.parse("http://a307.static-file.com:8080/video/df82f1ee435e69166bf869b14ab5b120/5bd66404/mp4/v12/181022/f7e65cf4e828f2d45748d288ecdb4243.mp4?s=320")

        videoView = findViewById(R.id.layout_video_view)

        mediacontroller = MediaController(this)
        mediacontroller!!.setAnchorView(videoView)
        mediacontroller!!.setMediaPlayer(videoView)
        videoView.setMediaController(mediacontroller);

        videoView.setOnPreparedListener { dialog.dismiss() }

        videoView.setVideoURI(uri)

        videoView.start()

    }

}
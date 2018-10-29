package m104vip.com.kttest

import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.ExifInterface
import android.net.Uri
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import okhttp3.*
import org.json.JSONArray


class MainActivity : AppCompatActivity() {

    lateinit var dialog: ProgressDialog;

    var str: String = "";
    val client = OkHttpClient()
    val array = arrayOf("RED","GREEN","YELLOW","BLACK","MAGENTA","PINK")
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    var imageUri :Uri ? = null
    var mFilePath: String? = ""

    val ACTION_CAMERA_REQUEST_CODE = 101
    val ACTION_ALBUM_REQUEST_CODE = 102
    val ACTION_REQUEST_CODE = 103

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun click(v: View?) {
        when (v?.id) {
            R.id.btnOne ->{
                dialog = ProgressDialog(this)
                dialog.setMessage("載入中")
                dialog.show()
                val request = Request.Builder()
                        .url("https://api.github.com/users/andy/starred")
                        .build()
                callApi(request)
            }
            R.id.btnTwo ->{
                AlertDialog.Builder(this@MainActivity)
                        .setTitle("訊息")
                        .setMessage("AlertDialog1")
                        .setPositiveButton("確定"){dialog, which->
                            toast("SHOW TOAST");

                        }
                        .setNegativeButton("取消"){_,_ ->}
                        .show()
            }
            R.id.btnThree ->{
                AlertDialog.Builder(this@MainActivity)
                        .setTitle("訊息")
                        .setItems(array,{_, which->
                            val dataName = array[which]
                            toast(dataName);

                        })
                        .setNegativeButton("取消"){_,_ ->}
                        .show()
            }
            R.id.btnFour ->{
                DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    toast("" + dayOfMonth + " " + (monthOfYear+1) + ", " + year)
                }, year, month, day).show()
            }
            R.id.btnFive ->{
                val cal = Calendar.getInstance()
                TimePickerDialog(this, TimePickerDialog.OnTimeSetListener{ timePicker, hour, minute ->
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)
                    toast(SimpleDateFormat("HH:mm").format(cal.time))
                },cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
            }
            R.id.btnSix ->{
                Snackbar.make(v, "We changes app background color.", Snackbar.LENGTH_LONG ).setAction("Undo",
                        { Snackbar.make(v, "Hello Snackbar!", Snackbar.LENGTH_SHORT).show() }).show()
            }
            R.id.btnSeven ->{
                val array =arrayOf("相機", "相簿");
                AlertDialog.Builder(this@MainActivity)
                        .setTitle("訊息")
                        .setItems(array,{_, which->

                            if(which == 0){
                                startCamera();
                            }
                            else{
                                startGallary();
                            }


                        })
                        .setNegativeButton("取消"){_,_ ->}
                        .show()

            }
            R.id.btnEight ->{
                startActivity(Intent(this@MainActivity, RecyclerViewActivity::class.java))
            }
            R.id.btnNine ->{
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type="text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, "我要分享");
                startActivity(Intent.createChooser(shareIntent,"分享測試"))
            }
            R.id.btnTen ->{
                showToast(this, "客製化Toast", R.mipmap.ic_launcher, dpToPix(this,20F), dpToPix(this,20F))
            }
            R.id.btnEleven ->{
                val inflater = this.layoutInflater
                val layout = inflater.inflate(R.layout.editview_dialog,null)

                val editDialog = layout.findViewById(R.id.editDialog) as EditText
                val tvDialog = layout.findViewById(R.id.tvDialog) as TextView


                editDialog.addTextChangedListener(object : TextWatcher {
                    override fun onTextChanged(s: CharSequence,start: Int,before: Int,count: Int) {

                        if(editDialog.text.length > 10){
                            tvDialog.setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_red_light));
                        }
                        else{
                            tvDialog.setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.black));
                        }
                        tvDialog.text = editDialog.text.length.toString() + "/10"
                    }

                    override fun beforeTextChanged(s: CharSequence,start: Int,count: Int,after: Int) {}

                    override fun afterTextChanged(s: Editable) {

                    }
                })

                AlertDialog.Builder(this@MainActivity)
                        .setTitle("資料輸入")
                        .setView(layout)
                        .setPositiveButton("確定"){dialog, which->
                            if(editDialog.text.length > 10){
                                Snackbar.make(v, "資料輸入過長請重新確認", Snackbar.LENGTH_SHORT).show()
                            }
                            else{
                                Snackbar.make(v, "資料已送出", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                        .setNegativeButton("取消"){_,_ ->}
                        .show()
            }
            R.id.btnTwelve ->{
                startActivity(Intent(this@MainActivity, VideoViewActivity::class.java))
            }
            R.id.btnThirteen ->{
                startActivity(Intent(this@MainActivity, ScaleImageViewActivity::class.java))
            }
            R.id.btnFourteen ->{
                startActivity(Intent(this@MainActivity, SpannableStringActivity::class.java))
            }
            R.id.btnFifteen ->{
                startActivityForResult(Intent(this, ScanActivity::class.java), ACTION_REQUEST_CODE )
            }

        }
    }

    fun startGallary(){
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val checkSelfPermission = ActivityCompat.checkSelfPermission(this ,permission)

        if (checkSelfPermission  == PackageManager.PERMISSION_GRANTED) {

            //讀取圖片
            val intent = Intent()
            //開啟Pictures畫面Type設定為image
            intent.type = "image/jpeg"
            //使用Intent.ACTION_GET_CONTENT這個Action
            intent.action = Intent.ACTION_OPEN_DOCUMENT
            //取得照片後返回此畫面
            startActivityForResult(intent, ACTION_ALBUM_REQUEST_CODE)

        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(this ,permission)){
                val PERMISSION_ALL = 1
                val PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }
        }
    }

    /**
     * 啟動相機
     */
    fun startCamera(){

        val permission = Manifest.permission.CAMERA
        val permission1 = Manifest.permission.WRITE_EXTERNAL_STORAGE

        val checkSelfPermission = ActivityCompat.checkSelfPermission(this ,permission)
        val checkSelfPermission1 = ActivityCompat.checkSelfPermission(this ,permission1)

        if (checkSelfPermission  == PackageManager.PERMISSION_GRANTED && checkSelfPermission1 == PackageManager.PERMISSION_GRANTED) {

            val currentapiVersion = android.os.Build.VERSION.SDK_INT

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            val simpleDateFormat = SimpleDateFormat("yyyy_MM_dd_mm_ss")
            val fileName = simpleDateFormat.format(Date())
            val tempFile = File(Environment.getExternalStorageDirectory(),fileName+".jpg")

            if (currentapiVersion<24) {
                imageUri = Uri.fromFile(tempFile)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            }
            else{
                val contentValues = ContentValues(1)
                contentValues.put(MediaStore.Images.Media.DATA, tempFile.absolutePath)
                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            }

            startActivityForResult(intent, ACTION_CAMERA_REQUEST_CODE);
        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(this ,permission) && ActivityCompat.shouldShowRequestPermissionRationale(this ,permission1)){
                val PERMISSION_ALL = 1
                val PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }else{
                startCamera()

            }
        }
    }


    /**
     * UI執行緒
     */
    private var handler : Handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if (msg?.what == 0) {
                dialog.dismiss()
                startActivity(Intent(this@MainActivity, JavaActivity::class.java).putExtra("test", str))
            }

        }
    }

    /**
     * callApi使用
     */
    fun callApi(request:Request){
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                toast("get data failed");
            }
            override fun onResponse(call: Call?, response: Response?) {
                val responseData = response?.body()?.string()
                val json = JSONArray(responseData)
                val projects: ArrayList<ProjectModel> = ArrayList()

                for(i in 0..(json.length() - 1)){
                    val item = json.getJSONObject(i)

                    val owner = item.getJSONObject("owner")
                    val ownerName = owner.get("login").toString()
                    val avatarURL = owner.get("avatar_url").toString()
                    val projectName = item.get("name").toString()
                    val description = item.get("description").toString()
                    val starCount = item.get("stargazers_count").toString().toInt()
                    val forkCount = item.get("forks_count").toString().toInt()

                    val project = ProjectModel(projectName, description, avatarURL, starCount, forkCount, ownerName)
                    projects.add(project)
                }


                str = projects.get(0).projectName.toString()
                Log.d("@@@@", "@@@@:"+projects.get(0).projectName)
                var message = Message()
                message.what = 0
                handler.sendMessage(message)
            }

        })
    }

    fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            ACTION_CAMERA_REQUEST_CODE -> {
                if(resultCode == Activity.RESULT_OK){
                    val picturePath:String? = imageUri?.let { getPath(this ,it) };
                    val angle = fileSave(picturePath!!)
                    startActivity(Intent(this@MainActivity, KontlinActivity::class.java).putExtra("imageUri", picturePath).putExtra("imageAngle", angle))

                }
            }
            ACTION_ALBUM_REQUEST_CODE -> {
                if(resultCode == Activity.RESULT_OK && data != null){
                    var imageUri :Uri ? = null
                    imageUri = data.getData()
                    if(imageUri!=null){
                        val picturePath:String? = getPath(this ,imageUri);
                        val angle = fileSave(picturePath!!)
                        startActivity(Intent(this@MainActivity, KontlinActivity::class.java).putExtra("imageUri", picturePath).putExtra("imageAngle", angle))
                    }
                }
            }
            ACTION_REQUEST_CODE -> {
                if(resultCode == Activity.RESULT_OK && data != null){
                    val bundleResult = data!!.extras
                    toast(""+bundleResult.getString("KEY_RESULT"));
                }
            }
            else -> {
                println("no handler onActivityReenter")
            }
        }
    }

    /**
     * 照片轉向處理
     */
    fun fileSave(uri:String): Int? {
        var selectedImageUri: Uri? = null
        var angle = 0;

        imageUri = Uri.parse(uri)

        if (imageUri!=null) {
            val imageFile = File(imageUri!!.getPath())
            if (imageFile.exists()) {
                selectedImageUri = imageUri
                mFilePath = selectedImageUri!!.getPath()

                val exifInterface: ExifInterface
                try {
                    exifInterface = ExifInterface(mFilePath)
                    val orientation_ = exifInterface.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL)
                    when (orientation_) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> angle = 90
                        ExifInterface.ORIENTATION_ROTATE_180 -> angle = 180
                        ExifInterface.ORIENTATION_ROTATE_270 -> angle = 270
                    }
                } catch (e: IOException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }

            }
        }

        return angle;
    }




}

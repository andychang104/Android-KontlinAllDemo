package m104vip.com.kttest

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_sharedpreferences.*

class SharedPreferencesActivity : AppCompatActivity() {
    private val mKeyPasswd = "passwd"
    private val mKeyName = "name"


    private val sharedPreferences: SharedPreferences
        get() = getPreferences(Activity.MODE_PRIVATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sharedpreferences)

        initView()
    }

    private fun initView() {
        ed_name.setText(readData(mKeyName).toString())
        ed_passwd.setText(readData(mKeyPasswd).toString())
        val button = findViewById<Button>(R.id.btn_submit)
        button.setOnClickListener {
            if (checkbox.isChecked) {
                val name = ed_name.text.toString().trim { it <= ' ' }
                val passwd = ed_passwd.text.toString().trim { it <= ' ' }
                val nameBool = saveData(name,mKeyName)
                val passwdPool = saveData(passwd,mKeyPasswd)
                Log.d("bool",nameBool.toString() + "--" + passwdPool)
            } else {
                remove()
            }
            finish()
        }
    }

    // 移除數據
    private fun remove() {
        val sharedPreferences = sharedPreferences
        val editor = sharedPreferences.edit()
        // 移除
        editor.remove(mKeyPasswd)
        // 更新
        editor.apply()
    }

    // 清空所有數據
    private fun clearAll() {
        val sharedPreferences = sharedPreferences
        val editor = sharedPreferences.edit()
        // 清空
        editor.clear()
        // 更新
        editor.apply()
    }

    /**
     * 儲存數據
     *
     * @param obj 需儲存數據
     * @return true/false
     */
    private fun saveData(obj: Any,key: String): Boolean {
        val sharedPreferences = sharedPreferences
        val editor = sharedPreferences.edit()
        // 加入數據
        editor.putString(key,obj.toString())
        return editor.commit()// 送出
    }

    /**
     * 讀取數據
     *
     * @param key
     * @return 讀取出的內容
     */
    private fun readData(key: String): Any {
        val sharedPreferences = sharedPreferences
        return sharedPreferences.getString(key,"")
    }

}
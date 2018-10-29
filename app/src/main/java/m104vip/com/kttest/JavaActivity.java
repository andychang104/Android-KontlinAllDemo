package m104vip.com.kttest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class JavaActivity extends Activity{

    TextView tvJava;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_java);

        EditText edit = new EditText(this);

        tvJava = (TextView) findViewById(R.id.tv_java);

        String test = getIntent().getStringExtra("test");

        Log.d("@@@@", "@@@@:"+test);

        tvJava.setText(test);

        tvJava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(JavaActivity.this, KontlinActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
    }
}

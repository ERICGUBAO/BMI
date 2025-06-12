package com.example.bmi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ConfigActivity extends AppCompatActivity {

    private static final String TAG = "Rate";
    private EditText dollarText;
    private EditText euroText;
    private EditText wonText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        // 接收传入的数据
        Intent intent = getIntent();
        float dollar = intent.getFloatExtra("dollar_rate_key", 0.1f);
        float euro = intent.getFloatExtra("euro_rate_key", 0.2f);
        float won = intent.getFloatExtra("won_rate_key", 500f);

        Log.i(TAG, "onCreate: dollar=" + dollar);
        Log.i(TAG, "onCreate: euro=" + euro);
        Log.i(TAG, "onCreate: won=" + won);

        // 把数据放入到页面控件里，供用户修改
        dollarText = findViewById(R.id.dollar_edit);
        euroText = findViewById(R.id.euro_edit);
        wonText = findViewById(R.id.won_edit);

        dollarText.setText(String.valueOf(dollar));
        euroText.setText(String.valueOf(euro));
        wonText.setText(String.valueOf(won));
    }

    public void save(View btn) {
        Log.i(TAG, "save:");

        try {
            // 获取并验证输入值
            float dollar = Float.parseFloat(dollarText.getText().toString());
            float euro = Float.parseFloat(euroText.getText().toString());
            float won = Float.parseFloat(wonText.getText().toString());

            if (dollar <= 0 || euro <= 0 || won <= 0) {
                Toast.makeText(this, "汇率必须大于0", Toast.LENGTH_SHORT).show();
                return;
            }

            // 创建新的Intent返回数据
            Intent resultIntent = new Intent();
            resultIntent.putExtra("key_dollar2", dollar);
            resultIntent.putExtra("key_euro2", euro);
            resultIntent.putExtra("key_won2", won);

            setResult(6, resultIntent); // 使用与RateActivity约定的结果码6
            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入有效数字", Toast.LENGTH_SHORT).show();
        }
    }
}
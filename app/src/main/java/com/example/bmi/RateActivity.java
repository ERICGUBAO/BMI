package com.example.bmi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RateActivity extends AppCompatActivity {
    private static final String TAG = "Rate";
    private static final int REQUEST_CODE_CONFIG = 3;
    private static final int RESULT_CODE_UPDATE = 6;

    TextView show;
    private float dollarrate = 0.1f;
    private float eurorate = 0.2f;
    private float wonrate = 500f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        show = findViewById(R.id.rmb_show);

        // 从SharedPreferences加载保存的汇率
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        dollarrate = sharedPreferences.getFloat("dollar_rate", 0.1f);
        eurorate = sharedPreferences.getFloat("euro_rate", 0.2f);
        wonrate = sharedPreferences.getFloat("won_rate", 500f);

        Log.i(TAG, "onCreate: get from sp dollarRate=" + dollarrate);
        Log.i(TAG, "onCreate: get from sp euroRate=" + eurorate);
        Log.i(TAG, "onCreate: get from sp wonRate=" + wonrate);
    }

    public void click(View btn) {
        EditText input = findViewById(R.id.rmb);
        String inpStr = input.getText().toString();

        try {
            float rmb = Float.parseFloat(inpStr);
            float result = 0.0f;

            if (btn.getId() == R.id.btn_dollar) {
                result = rmb * dollarrate;
            } else if (btn.getId() == R.id.btn_euro) {
                result = rmb * eurorate;
            } else if (btn.getId() == R.id.btn_won) {
                result = rmb * wonrate;
            }

            show.setText(String.valueOf(result));
        } catch (NumberFormatException ex) {
            show.setText("请输入正确数据");
            Toast.makeText(this, "请输入正确数据", Toast.LENGTH_SHORT).show();
        }
    }

    public void clickOpen(View btn) {
        openConfigActivity();
    }

    private void openConfigActivity() {
        Intent config = new Intent(this, ConfigActivity.class);
        config.putExtra("dollar_rate_key", dollarrate);
        config.putExtra("euro_rate_key", eurorate);
        config.putExtra("won_rate_key", wonrate);

        Log.i(TAG, "clickOpen: dollarRate=" + dollarrate);
        Log.i(TAG, "clickOpen: euroRate=" + eurorate);
        Log.i(TAG, "clickOpen: wonRate=" + wonrate);

        startActivityForResult(config, REQUEST_CODE_CONFIG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CONFIG && resultCode == RESULT_CODE_UPDATE && data != null) {
            // 更新汇率值
            dollarrate = data.getFloatExtra("key_dollar2", dollarrate);
            eurorate = data.getFloatExtra("key_euro2", eurorate);
            wonrate = data.getFloatExtra("key_won2", wonrate);

            Log.i(TAG, "onActivityResult: dollarRate=" + dollarrate);
            Log.i(TAG, "onActivityResult: euroRate=" + eurorate);
            Log.i(TAG, "onActivityResult: wonRate=" + wonrate);

            // 保存到SharedPreferences
            SharedPreferences sp = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat("dollar_rate", dollarrate);
            editor.putFloat("euro_rate", eurorate);
            editor.putFloat("won_rate", wonrate);
            editor.apply();

            // 清空当前显示
            show.setText("");
            EditText input = findViewById(R.id.rmb);
            input.setText("");

            Toast.makeText(this, "汇率已更新", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_set) {
            openConfigActivity();
        }
        return super.onOptionsItemSelected(item);
    }
}

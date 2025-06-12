package com.example.bmi;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetActivity extends AppCompatActivity {

    private static final String TAG = "WebContentFetcher";
    private static final String TARGET_URL = "http://www.usd-cny.com/bankofchina.htm";

    private TextView resultTextView;
    private Button fetchButton;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// 注意这行关键修改！
        setContentView(R.layout.activity_net); // 必须与您的XML文件名完全一致

// 初始化视图
        resultTextView = findViewById(R.id.result_text);
        fetchButton = findViewById(R.id.fetch_button);

// 添加验证
        if (resultTextView == null || fetchButton == null) {
            Toast.makeText(this,
                    "布局加载错误: " +
                            (resultTextView == null ? "缺少result_text " : "") +
                            (fetchButton == null ? "缺少fetch_button" : ""),
                    Toast.LENGTH_LONG).show();
            Log.e(TAG, "布局ID不匹配");
            finish(); // 关闭Activity避免后续崩溃
            return;
        }

        fetchButton.setOnClickListener(v -> fetchWebContent());
    }

    private void fetchWebContent() {
        resultTextView.setText("正在获取数据...");
        fetchButton.setEnabled(false);

        executor.execute(() -> {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            StringBuilder htmlContent = new StringBuilder();

            try {
                URL url = new URL(TARGET_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw new IOException("HTTP错误代码: " + responseCode);
                }

                InputStream inputStream = urlConnection.getInputStream();
                reader = new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                String line;
                while ((line = reader.readLine()) != null) {
                    htmlContent.append(line).append("\n");
                }

                handler.post(() -> {
                    resultTextView.setText(htmlContent.toString());
                    fetchButton.setEnabled(true);
                    Toast.makeText(NetActivity.this,
                            "数据获取成功，共"+htmlContent.length()+"字符",
                            Toast.LENGTH_SHORT).show();
                });

            } catch (IOException e) {
                Log.e(TAG, "网络错误", e);
                handler.post(() -> {
                    resultTextView.setText("错误: " + e.getMessage());
                    fetchButton.setEnabled(true);
                    Toast.makeText(NetActivity.this,
                            "获取失败: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            } finally {
                if (urlConnection != null) urlConnection.disconnect();
                if (reader != null) try { reader.close(); } catch (IOException e) {
                    Log.e(TAG, "关闭流错误", e);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
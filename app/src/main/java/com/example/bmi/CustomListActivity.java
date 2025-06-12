package com.example.bmi;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RateListActivity extends ListActivity {

    private static final String TAG = "RateList";
    private static final int MSG_UPDATE_LIST = 1;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_UPDATE_LIST) {
                @SuppressWarnings("unchecked")
                List<String> rates = (List<String>) msg.obj;
                if (rates != null && !rates.isEmpty()) {
                    setListAdapter(new ArrayAdapter<>(
                            RateListActivity.this,
                            android.R.layout.simple_list_item_1,
                            rates));
                    Log.d(TAG, "成功显示 " + rates.size() + " 条汇率数据");
                } else {
                    Log.d(TAG, "未获取到有效汇率数据");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始空数据
        setListAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                new ArrayList<>()));

        new Thread(this::fetchExchangeRates).start();
    }

    private void fetchExchangeRates() {
        List<String> rateList = new ArrayList<>();
        try {
            URL url = new URL("https://www.huilvbiao.com/bank/spdb");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(15000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"))) {

                // 读取完整HTML
                StringBuilder html = new StringBuilder();
                char[] buffer = new char[1024];
                int read;
                while ((read = reader.read(buffer)) != -1) {
                    html.append(buffer, 0, read);
                }

                // 调试：打印HTML片段
                Log.d(TAG, "网页内容片段：" + html.substring(0, Math.min(1000, html.length())));

                // 精确匹配表格数据
                Pattern pattern = Pattern.compile(
                        "<th class=\"table-coin\">\\s*<a[^>]*>\\s*<img[^>]*>\\s*<span>([^<]+)</span>\\s*</a>\\s*</th>" +
                                "\\s*<td>([^<]+)</td>" +
                                "\\s*<td>([^<]+)</td>" +
                                "\\s*<td>([^<]+)</td>" +
                                "\\s*<td>([^<]+)</td>",
                        Pattern.DOTALL);

                Matcher matcher = pattern.matcher(html.toString());
                while (matcher.find()) {
                    String currency = matcher.group(1).trim();
                    String buyRate = matcher.group(2).trim();
                    String sellRate = matcher.group(3).trim();
                    String cashBuy = matcher.group(4).trim();
                    String cashSell = matcher.group(5).trim();

                    rateList.add(String.format("%s 现汇:%s/%s 现钞:%s/%s",
                            currency, buyRate, sellRate, cashBuy, cashSell));

                    Log.d(TAG, "解析到: " + currency + " " + buyRate);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "数据获取异常: " + e.getMessage());
        }

        handler.sendMessage(handler.obtainMessage(MSG_UPDATE_LIST, rateList));
    }
}
package com.example.bmi;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText heightInput, weightInput;
    private TextView resultText, adviceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        heightInput = findViewById(R.id.heightInput);
        weightInput = findViewById(R.id.weightInput);
        resultText = findViewById(R.id.resultText);
        adviceText = findViewById(R.id.adviceText);
        Button calculateButton = findViewById(R.id.calculateButton);

        calculateButton.setOnClickListener(v -> calculateBMI());
    }

    @SuppressLint("DefaultLocale")
    private void calculateBMI() {
        String heightStr = heightInput.getText().toString();
        String weightStr = weightInput.getText().toString();

        if (!heightStr.isEmpty() && !weightStr.isEmpty()) {
            float height = Float.parseFloat(heightStr) / 100; // 转换为米
            float weight = Float.parseFloat(weightStr);

            float bmi = weight / (height * height);

            resultText.setText(String.format("BMI: %.2f", bmi));

            if (bmi < 18.5) {
                adviceText.setText("体重过轻，建议增加营养摄入。");
            } else if (bmi >= 18.5 && bmi < 24.9) {
                adviceText.setText("体重正常，继续保持。");
            } else if (bmi >= 25 && bmi < 29.9) {
                adviceText.setText("体重超重，建议适当减肥。");
            } else {
                adviceText.setText("肥胖，建议加强锻炼并控制饮食。");
            }
        } else {
            resultText.setText("请输入身高和体重。");
            adviceText.setText("");
        }
    }
}
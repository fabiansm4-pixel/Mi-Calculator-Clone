package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;

public class BaseActivity extends AppCompatActivity {

    @SuppressLint({"ResourceType", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        LinearLayout base = findViewById(R.id.activity_base);

        //пользовательнские параметры Layout
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, convertPixelToDp(1)
        );

        //создал тулбар, добавил кнопку назад и отключил название(title)
        Toolbar toolbarBase = findViewById(R.id.toolbarBase);
        setSupportActionBar(toolbarBase);

        //переопределение кнопки "Назад" на предыдущий фрагмент
        toolbarBase.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.back_button);

        //добавил textView в тулбар. Указал текст по центру, цвет, размер и тип шрифта
        TextView toolbarBaseTitle = findViewById(R.id.toolbarBaseTitle);
        toolbarBaseTitle.setText(getString(R.string.app_name));
        toolbarBaseTitle.setTextColor(Color.BLACK);
        toolbarBaseTitle.setTextSize(18);
        toolbarBaseTitle.setTypeface(null, Typeface.BOLD);

        //программно добавляем view
        View line = new View(this);
        line.setLayoutParams(params);
        line.setBackgroundColor(Color.parseColor("#D8D8D8"));

        //программно добавляем TextView
        TextView aboutAppText = new TextView(this);
        aboutAppText.setPadding(
                convertPixelToDp(20),
                convertPixelToDp(10),
                0,
                convertPixelToDp(10));
        aboutAppText.setText("О ПРИЛОЖЕНИИ");
        aboutAppText.setTextSize(12);
        aboutAppText.setGravity(Gravity.START);

        //программно добавляем view - линия
        View line2 = new View(this);
        /*line2.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, 50).setMargins(20, 0, 0, 0));*/
        LinearLayout.LayoutParams paramsLine2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, convertPixelToDp(1)
        );
        paramsLine2.setMargins(
                convertPixelToDp(20),
                0,
                0,
                0);
        line2.setLayoutParams(paramsLine2);
        line2.setBackgroundColor(Color.parseColor("#D8D8D8"));

        //программно добавляем TextView
        TextView aboutAppTextDetail = new TextView(this);
        aboutAppTextDetail.setPadding(
                convertPixelToDp(20),
                convertPixelToDp(10),
                convertPixelToDp(20),
                convertPixelToDp(10));
        aboutAppTextDetail.setText("Приложение версии " + BuildConfig.VERSION_NAME +
                "\nРазрабатывал лично в учебных целях." +
                "\nE-mail: ilyxan89@gmail.com");
        Linkify.addLinks(aboutAppTextDetail, Linkify.EMAIL_ADDRESSES);
        aboutAppTextDetail.setTextSize(16);
        aboutAppTextDetail.setLinkTextColor(Color.MAGENTA);
//        aboutAppTextDetail.setTypeface(Typeface.create("roboto", Typeface.NORMAL));
        aboutAppTextDetail.setTextColor(Color.BLACK);
        aboutAppTextDetail.setGravity(Gravity.START);

        //программно добавляем view
        View line3 = new View(this);
        line3.setLayoutParams(params);
        line3.setBackgroundColor(Color.parseColor("#D8D8D8"));

        //выводим созданные View на экран
        base.addView(line);
        base.addView(aboutAppText);
        base.addView(line2);
        base.addView(aboutAppTextDetail);
        base.addView(line3);
    }

    //метод конвертирует число пикселей в dp
    public int convertPixelToDp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (value * density);
    }
}
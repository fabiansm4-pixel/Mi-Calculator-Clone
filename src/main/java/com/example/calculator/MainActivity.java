package com.example.calculator;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ImageButton multi_window_button;
    private ImageButton popup_button;
    private CoordinatorLayout mainActivity;
    private Toolbar toolbar;
    TabLayout tabLayout;
    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Связывание SectionsPagerAdapter с ViewPager
        SectionsPagerAdapter pagerAdapter =
                new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager pager = findViewById(R.id.view_pager);
        pager.setAdapter(pagerAdapter);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Связывание ViewPager с TabLayout
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);

        //установил иконки для TabLayout
        int[] imageResId = {
                R.drawable.selector_calc_icon,
                R.drawable.selector_convert_icon,
                R.drawable.selector_money_icon};

        for (int i = 0; i < imageResId.length; i++) {
            Objects.requireNonNull(tabLayout.getTabAt(i)).setIcon(imageResId[i]);
        }

        //добавил popupWindow меню
        mainActivity = findViewById(R.id.mainActivity);
        popup_button = findViewById(R.id.button_popup_menu);
        popUpWindow();

        //переход в многооконный режим (мини-версия калькулятора)
        //кнопку спрятал, т.к. мини-версия калькулятора еще не готова
        multi_window_button = findViewById(R.id.button_multi_window);
        multi_window_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (Settings.canDrawOverlays(MainActivity.this)) {
                    Intent intent = new Intent(MainActivity.this, MultiWindowActivity.class);
                    startService(intent);
                    finishAndRemoveTask();
                    onBackPressed();
                } else {
                    askPermission();
                    Toast.makeText(MainActivity.this,
                            "Необходимо предоставить доступ приложению",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        //скрытие значка multi_window_button, если не выбрана вкладка калькулятора
        //переход на страницу "О приложении", если не выбрана вкладка калькулятора
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    multi_window_button.setVisibility(View.VISIBLE);
                    popUpWindow();
                } else {
                    multi_window_button.setVisibility(View.INVISIBLE);
                    clickListenerPopupWindow();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    multi_window_button.setVisibility(View.VISIBLE);
                    popUpWindow();
                } else {
                    multi_window_button.setVisibility(View.INVISIBLE);
                    clickListenerPopupWindow();
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    multi_window_button.setVisibility(View.VISIBLE);
                    popUpWindow();
                } else {
                    multi_window_button.setVisibility(View.INVISIBLE);
                    clickListenerPopupWindow();
                }
            }
        });
    }

    //метод количество страниц в TabLayout и позиции фрагментов
    private static class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // кол-во страниц
        @Override
        public int getCount() {
            return 3;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new CalcFragment();
                case 1:
                    return new ConvertFragment();
                case 2:
                    return new MoneyFragment();
            }
            return new CalcFragment();
        }

        //Этот метод добавляет текст на вкладки.
        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

    }

    //диалоговое окно очистки истории
    public void clearAllAlertDialog() {
        final Context context = this;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);

        //настройка шапки диалогового окна
        TextView title = new TextView(this);
        title.setText("Очистить");
        title.setPadding(10, 100, 10, 70);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        alertDialogBuilder.setCustomTitle(title);

        //найстройка сообщения
        alertDialogBuilder
                .setMessage("Очистить историю?")
                //.setCancelable(false)
                .setPositiveButton("Очистить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TextView historyOfCalcFragment = findViewById(R.id.history);
                        historyOfCalcFragment.setText(" ");
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });
        //создание alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.BOTTOM);
        alertDialog.show();

        //изменить шрифт сообщения
        TextView alertMessage = alertDialog.findViewById(android.R.id.message);
        alertMessage.setTextSize(18);

        //настройка кнопки "Отмена"
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextSize(18);
        negativeButton.setBackgroundResource(R.drawable.shape_rectangle_button);
        negativeButton.setTextColor(Color.BLACK);

        //настройка кнопки "Очистить"
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setTextSize(18);
        positiveButton.setBackgroundResource(R.drawable.shape_rectangle_button);
        positiveButton.setTextColor(Color.rgb(70, 165, 231));

        //получаем параметры линейного макета
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)
                negativeButton.getLayoutParams();
        layoutParams.weight = 10;
        layoutParams.setMargins(15, 40, 40, 0);

        //отключаем allCaps для текста кнопок и устанавливаем отступы
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setLayoutParams(layoutParams);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setLayoutParams(layoutParams);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
    }

    //создание меню приложения
    public void popUpWindow() {
        popup_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                assert inflater != null;
                View popupView = inflater.inflate(R.layout.popup_window, null);

                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;

                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
                popupView.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });

                int distanceFromTop = toolbar.getHeight();
                int widthTop = toolbar.getWidth() / 2 + popup_button.getWidth() / 2;


                popupWindow.getContentView().findViewById(R.id.btn_clear_all).setOnClickListener(
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                clearAllAlertDialog();
                                popupWindow.dismiss();
                            }
                        });

                popupWindow.getContentView().findViewById(R.id.btn_info).setOnClickListener(
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplication(), BaseActivity.class);
                                startActivity(intent);
                                popupWindow.dismiss();
                            }
                        });

                //размер тени окна
                popupWindow.setElevation(20);

                //указываем координаты расположения окна
                popupWindow.showAsDropDown(mainActivity, widthTop, distanceFromTop, Gravity.NO_GRAVITY);
            }
        });
    }

    //переход на страницу "О приложении"
    public void clickListenerPopupWindow() {
        popup_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), BaseActivity.class);
                startActivity(intent);
            }
        });
    }

    //запрос разрешения у пользователя
    private void askPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
    }

    /*@RequiresApi(api = Build.VERSION_CODES.N)
    public void onStartMinimumSizeActivity(View view) {
        // Define the bounds in which the Activity will be launched into.
        Rect bounds = new Rect(100, 100, 100, 0);

        // Set the bounds as an activity option.
        ActivityOptions options = ActivityOptions.makeBasic();
        options.setLaunchBounds(bounds);

        // Start the LaunchBoundsActivity with the specified options
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        startActivity(intent, options.toBundle());
    }*/
}

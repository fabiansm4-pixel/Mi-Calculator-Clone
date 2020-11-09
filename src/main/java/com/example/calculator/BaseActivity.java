package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class BaseActivity extends AppCompatActivity {

    LinearLayout view_converter_screen;
    View view_converter_line;
    LinearLayout view_converter_keyboard;
    File imagePath;
    Spinner string1_spinner;
    EditText string1_editText;
    TextView string1_input;
    TextView string1_add;
    TextView string1_text;
    Spinner string2_spinner;
    EditText string2_editText;
    TextView string2_input;
    TextView string2_add;
    TextView string2_text;
    RelativeLayout view_converter_string3;
    TextView string3_text;
    TextView string3_output;
    RelativeLayout view_converter_string4;
    TextView string4_text;
    TextView toolbarBaseTitle;
    Button c_plus_minus;
    Button c_btn_go;

    TextView spinner_custom;

    PopupWindow popupWindowBMI;
    PopupWindow popupWindowAge;
    PopupWindow popupWindowDate;

    boolean isSelectedTextView = true; //выбрана строка первая (true) или вторая (false)
    boolean isSelectedSpinner; //выбран первый спиннер (true) или второй (false)
    boolean isSelectedDate; //выбран первая строка с датой (true) или вторая (false)

    String sInput = "0";
    String sValueOne = "0";
    String sValueTwo = "0";
    String tvOut = "0";
    String tvOutBMI = "0";

    String keyIntent; //по этому ключу запускает нужная конфигурация макета и методы расчета

    int selectedPositionInSpinner; //номер выбранной позиции в списке спиннера

    DatePickerDialog datePicker;
    String firstDate = "";
    String secondDate = "";

    int year = Calendar.getInstance().get(Calendar.YEAR);
    int month = Calendar.getInstance().get(Calendar.MONTH);
    int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

    double valueOne;
    double valueTwo;

    //получаем HashMap из класса Coefficients
    HashMap<String, Double> toLength = new Coefficients().toLength();
    HashMap<String, Double> toSquare = new Coefficients().toSquare();
    HashMap<String, Double> toVolume = new Coefficients().toVolume();
    HashMap<String, Double> toSpeed = new Coefficients().toSpeed();
    HashMap<String, Double> toTime = new Coefficients().toTime();
    HashMap<String, Double> toWeight = new Coefficients().toWeight();

    @SuppressLint({"ResourceType", "SetTextI18n", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        //создал тулбар
        Toolbar toolbarBase = findViewById(R.id.toolbarBase);
        setSupportActionBar(toolbarBase);

        //переопределение кнопки "Назад" на предыдущий фрагмент
        toolbarBase.setNavigationOnClickListener(v -> onBackPressed());

        //добавил кнопку назад и отключил название(title)
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.back_button);

        //добавил textView в тулбар. Указал текст по центру, цвет, размер и тип шрифта
        toolbarBaseTitle = findViewById(R.id.toolbarBaseTitle);
        toolbarBaseTitle.setText(getString(R.string.app_name));
        toolbarBaseTitle.setTextColor(Color.BLACK);
        toolbarBaseTitle.setTextSize(18);
        toolbarBaseTitle.setTypeface(null, Typeface.BOLD);

        //основные компоненты макета конвертера
        //компоненты ввода-вывода информации на экран
        view_converter_screen = findViewById(R.id.view_converter_screen);
        //элементы первой строки ввода-вывода информации на экран
//        RelativeLayout view_converter_string1 = findViewById(R.id.view_converter_string1);
        string1_spinner = findViewById(R.id.string1_spinner);
        string1_editText = findViewById(R.id.string1_editText);
        string1_text = findViewById(R.id.string1_text);
        string1_input = findViewById(R.id.string1_input);
        string1_add = findViewById(R.id.string1_add);

        //элементы второй строки ввода-вывода информации на экран
//        RelativeLayout view_converter_string2 = findViewById(R.id.view_converter_string2);
        string2_spinner = findViewById(R.id.string2_spinner);
        string2_editText = findViewById(R.id.string2_editText);
        string2_text = findViewById(R.id.string2_text);
        string2_input = findViewById(R.id.string2_input);
        string2_add = findViewById(R.id.string2_add);

        string1_input.setOnClickListener(v -> {
            isSelectedTextView = true;
            //при повторном выборе сбрасываем sInput на 0
            sInput = "0";
            string1_input.setTextColor(ContextCompat.getColor(getApplication(), R.color.Orange));
            string2_input.setTextColor(ContextCompat.getColor(getApplication(), R.color.Black));
        });

        string2_input.setOnClickListener(v -> {
            isSelectedTextView = false;
            //при повторном выборе сбрасываем sInput на 0
            sInput = "0";
            string2_input.setTextColor(ContextCompat.getColor(getApplication(), R.color.Orange));
            string1_input.setTextColor(ContextCompat.getColor(getApplication(), R.color.Black));
        });

        //элементы третьей строки ввода-вывода информации на экран
        view_converter_string3 = findViewById(R.id.view_converter_string3);
        string3_text = findViewById(R.id.string3_text);
        string3_output = findViewById(R.id.string3_output);

        //элементы четвертой строки ввода-вывода информации на экран
        view_converter_string4 = findViewById(R.id.view_converter_string4);
        string4_text = findViewById(R.id.string4_text);

        view_converter_line = findViewById(R.id.view_converter_line);
        view_converter_keyboard = findViewById(R.id.view_converter_keyboard);

        spinner_custom = findViewById(R.id.spinner_custom);

        //кнопки клавиатуры
        Button c_btn0 = findViewById(R.id.c_btn0);
        Button c_btn1 = findViewById(R.id.c_btn1);
        Button c_btn2 = findViewById(R.id.c_btn2);
        Button c_btn3 = findViewById(R.id.c_btn3);
        Button c_btn4 = findViewById(R.id.c_btn4);
        Button c_btn5 = findViewById(R.id.c_btn5);
        Button c_btn6 = findViewById(R.id.c_btn6);
        Button c_btn7 = findViewById(R.id.c_btn7);
        Button c_btn8 = findViewById(R.id.c_btn8);
        Button c_btn9 = findViewById(R.id.c_btn9);
        Button c_btn_virgule = findViewById(R.id.c_btn_comma);
        Button c_btn_clear = findViewById(R.id.c_btn_clear);
        Button c_btn_erase = findViewById(R.id.c_btn_erase);
        c_plus_minus = findViewById(R.id.c_plus_minus);
        c_btn_go = findViewById(R.id.c_btn_go);

        View.OnClickListener listener = v -> {
            int id = v.getId();
            try {
                switch (id) {
                    //Цифры
                    case R.id.c_btn0:
                        if (sInput.equals("0")) {
                            sInput = "0";
                        } else
                            sInput += "0";
                        break;
                    case R.id.c_btn1:
                        if (sInput.equals("0")) {
                            sInput = "1";
                        } else
                            sInput += "1";
                        break;
                    case R.id.c_btn2:
                        if (sInput.equals("0")) {
                            sInput = "2";
                        } else
                            sInput += "2";
                        break;
                    case R.id.c_btn3:
                        if (sInput.equals("0")) {
                            sInput = "3";
                        } else
                            sInput += "3";
                        break;
                    case R.id.c_btn4:
                        if (sInput.equals("0")) {
                            sInput = "4";
                        } else
                            sInput += "4";
                        break;
                    case R.id.c_btn5:
                        if (sInput.equals("0")) {
                            sInput = "5";
                        } else
                            sInput += "5";
                        break;
                    case R.id.c_btn6:
                        if (sInput.equals("0")) {
                            sInput = "6";
                        } else
                            sInput += "6";
                        break;
                    case R.id.c_btn7:
                        if (sInput.equals("0")) {
                            sInput = "7";
                        } else
                            sInput += "7";
                        break;
                    case R.id.c_btn8:
                        if (sInput.equals("0")) {
                            sInput = "8";
                        } else
                            sInput += "8";
                        break;
                    case R.id.c_btn9:
                        if (sInput.equals("0")) {
                            sInput = "9";
                        } else
                            sInput += "9";
                        break;

                    //Запятая
                    case R.id.c_btn_comma:
//                        if (isSelectedTextView) {
//                            sInput = toCalculate(string1_input.getText().toString());
//                        } else
//                            sInput = toCalculate(string2_input.getText().toString());
                        if (isSelectedTextView) {
                            sInput = sValueOne;
                        } else
                            sInput = sValueTwo;
                        if (sInput.contains(".")) {
                            break;
                        } else if (sInput.equals("0")) {
                            sInput = "0.";
                        } else
                            sInput += ".";
                        break;

                    //очистить поле
                    case R.id.c_btn_clear:
                        sInput = "0";
                        if (isSelectedTextView) {
                            valueOne = 0;
                        } else
                            valueTwo = 0;
                        break;

                    //удалить 1 знак справа
                    case R.id.c_btn_erase:
//                        Toast toast = Toast.makeText(getApplication(),
//                                String.valueOf(valueOne),
//                                Toast.LENGTH_SHORT);
//                        toast.show();
//
//                        if ((string1_input.getText().toString().length() == 2)
//                                & (string1_input.getText().toString().contains("-"))) {
//                            System.out.println("!!!!!!!!! СРАБОТАЛ");
//                            valueOne = 0;
////                            sInput = "0";
//                        }
                        if (sValueOne.isEmpty() || sInput.isEmpty()) {
                            break;
                        }
//                        if (isSelectedTextView) {
//                            sInput = sValueOne;
//                        } else
//                            sInput = sValueTwo;
                        if (isSelectedTextView) {
                            sInput = toCalculate(string1_input.getText().toString());
                        } else {
                            sInput = toCalculate(string2_input.getText().toString());
                        }
                        if ((sInput.length() == 1)) {
                            sInput = "0";
                        } else
                            sInput = sInput.substring(0, sInput.length() - 1);
                        break;

                    //добавить к значению температуры + или -
                    case R.id.c_plus_minus:
                        if (sInput.isEmpty() || sInput.equals("0")) {
                            break;
                        }
                        if (isSelectedTextView) {
                            sInput = toCalculate(string1_input.getText().toString());
                        } else {
                            sInput = toCalculate(string2_input.getText().toString());
                        }
                        if (sInput.charAt(0) != '-') {
                            sInput = "-" + sInput;
                        } else
                            sInput = sInput.substring(1);
                        break;

                    //запуск подсчет индекса массы тела
                    case R.id.c_btn_go:
                        popUpWindowViewBMI();
                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + id);
                }

                switch (keyIntent) {
                    case "viewBMI":
                        calculateBMI();
                        break;
                    case "viewDiscount":
                        calculateDiscount();
                        break;
                    case "viewLength":
                        calculateLength();
                        break;
                    case "viewSquare":
                        calculateSquare();
                        break;
                    case "viewVolume":
                        calculateVolume();
                        break;
                    case "viewTemperature":
                        calculateTemperature();
                        break;
                    case "viewSpeed":
                        calculateSpeed();
                        break;
                    case "viewTime":
                        calculateTime();
                        break;
                    case "viewWeight":
                        calculateWeight();
                        break;
                    case "viewSplitBill":
                        calculateSplitBill();
                        break;
                    case "viewPercent":
                        calculatePercent();
                        break;
                }

            } catch (Exception e) {
                if (isSelectedTextView) {
                    string1_input.setText("Error: " + e.getMessage());
                } else
                    string2_input.setText("Error: " + e.getMessage());
                e.printStackTrace();
                string3_output.setText("0");
            }
        };

        c_btn0.setOnClickListener(listener);
        c_btn1.setOnClickListener(listener);
        c_btn2.setOnClickListener(listener);
        c_btn3.setOnClickListener(listener);
        c_btn4.setOnClickListener(listener);
        c_btn5.setOnClickListener(listener);
        c_btn6.setOnClickListener(listener);
        c_btn7.setOnClickListener(listener);
        c_btn8.setOnClickListener(listener);
        c_btn9.setOnClickListener(listener);
        c_btn_virgule.setOnClickListener(listener);
        c_btn_clear.setOnClickListener(listener);
        c_btn_erase.setOnClickListener(listener);
        c_plus_minus.setOnClickListener(listener);
        c_btn_go.setOnClickListener(listener);

        //конструктор для постороения макета экрана
        //получение ключа из intent
        Intent intent = getIntent();
        keyIntent = intent.getStringExtra("keyIntent");
        if (keyIntent != null) {
            switch (keyIntent) {
                //вызов окна "О приложении"
                case "viewInfo":
                    createViewAbout();
                    break;

                //вызов конвертеров величин
                case "viewBMI":
                    createViewBMI();
                    break;
                case "viewAge":
                    createViewAge();
                    break;
                case "viewDiscount":
                    createViewDiscount();
                    break;
                case "viewPercent":
                    createViewPercent();
                    break;
                case "viewData":
                    createViewDate();
                    break;
                case "viewLength":
                    createViewLength();
                    break;
                case "viewSquare":
                    createViewSquare();
                    break;
                case "viewVolume":
                    createViewVolume();
                    break;
                case "viewTemperature":
                    createViewTemperature();
                    break;
                case "viewSpeed":
                    createViewSpeed();
                    break;
                case "viewTime":
                    createViewTime();
                    break;
                case "viewWeight":
                    createViewWeight();
                    break;
                case "viewScaleOfNotation":
                    toolbarBaseTitle.setText(getString(R.string.scale_of_notation));
                    break;

                //вызов финансовых конвертеров
                case "viewInvestments":
                    toolbarBaseTitle.setText(getString(R.string.investments_calc));
                    break;
                case "viewCurrency":
                    toolbarBaseTitle.setText(getString(R.string.currency_calc));
                    break;
                case "viewCredit":
                    toolbarBaseTitle.setText(getString(R.string.credit_calc));
                    break;
                case "viewSplitBill":
                    createViewSplitBill();
                    break;
            }
        }
    }

    //вызов окна "ИМТ"
    @SuppressLint("ClickableViewAccessibility")
    public void createViewBMI() {
        toolbarBaseTitle.setText(getString(R.string.bmi));
        string1_editText.setVisibility(View.GONE);
        string2_editText.setVisibility(View.GONE);
        view_converter_string3.setVisibility(View.INVISIBLE);
        view_converter_string4.setVisibility(View.GONE);
        c_plus_minus.setVisibility(View.GONE);

        String[] ves = {"Вес"};
        String[] rost = {"Рост"};

        String[] weight = {"Килограммы", "Фунты"};
        String[] height = {"Сантиметры", "Метры", "Фут", "Дюймы"};

        ArrayAdapter<String> adapterWeight = new ArrayAdapter<>(this,
                R.layout.spinner_custom, ves);
        string1_spinner.setAdapter(adapterWeight);
        string1_add.setText(weight[0]);

        string1_spinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isSelectedSpinner = true;
                alertDialogBMI(weight);
            }
            return true;
        });

        ArrayAdapter<String> adapterHeight = new ArrayAdapter<>(this,
                R.layout.spinner_custom, rost);
        string2_spinner.setAdapter(adapterHeight);
        string2_add.setText(height[0]);

        string2_spinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isSelectedSpinner = false;
                alertDialogBMI(height);
            }
            return true;
        });
    }

    //всплывающее окно для подсчета и вывода индекса массы тела
    @SuppressLint("ClickableViewAccessibility")
    public void popUpWindowViewBMI() {
        HashMap<String, Double> toWeight = new HashMap<>();
        toWeight.put("Килограммы", 1.0);
        toWeight.put("Фунты", 2.205);

        HashMap<String, Double> toHeight = new HashMap<>();
        toHeight.put("Сантиметры", 100.0);
        toHeight.put("Метры", 1.0);
        toHeight.put("Фут", 3.28084);
        toHeight.put("Дюймы", 39.3701);

        // ИМТ определяют путем деления имеющейся массы тела (в кг) на рост (в м),
        // возведенный в квадрат, то есть: ИМТ = масса тела (кг: (рост, м)2.
        double dResult = valueOne / toWeight.get(string1_add.getText().toString())
                / Math.pow((valueTwo / toHeight.get(string2_add.getText().toString())), 2);
        String sResult = toFormatDouble(dResult, "", 1);
        tvOutBMI = toExpression(toFormatDouble(dResult, "ru", 1));

        //если индекс массы тела больше 50, то показать сообщение
        if (dResult <= 50) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            @SuppressLint("InflateParams")
            View popupViewBMI = inflater.inflate(R.layout.popup_window_bmi, null);

            int width = LinearLayout.LayoutParams.MATCH_PARENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;

            popupWindowBMI = new PopupWindow(popupViewBMI, width, height, false);
            popupWindowBMI.setOutsideTouchable(true);

            if (sResult.matches("\\D+")) {
                ((TextView) popupWindowBMI.getContentView().findViewById(R.id.result_bmi))
                        .setText("0");
            } else
                ((TextView) popupWindowBMI.getContentView().findViewById(R.id.result_bmi))
                        .setText(tvOutBMI);

            if (dResult < 18.5) {
                ((TextView) popupWindowBMI.getContentView().findViewById(R.id.result_type_bmi))
                        .setText("Недостаток веса");
                ((TextView) popupWindowBMI.getContentView().findViewById(R.id.result_type_bmi))
                        .setTextColor(Color.parseColor("#4E8FE3"));
            } else if (dResult < 25) {
                ((TextView) popupWindowBMI.getContentView().findViewById(R.id.result_type_bmi))
                        .setText("Норма");
                ((TextView) popupWindowBMI.getContentView().findViewById(R.id.result_type_bmi))
                        .setTextColor(Color.parseColor("#64B501"));
            } else if (dResult >= 25) {
                ((TextView) popupWindowBMI.getContentView().findViewById(R.id.result_type_bmi))
                        .setText("Избыточный вес");
                ((TextView) popupWindowBMI.getContentView().findViewById(R.id.result_type_bmi))
                        .setTextColor(Color.parseColor("#EB4E69"));
            }
            popupWindowBMI.showAtLocation(toolbarBaseTitle, Gravity.BOTTOM, 0, 0);

            Button send_screenshot = popupWindowBMI.getContentView().findViewById(R.id.send_screenshot_BMI);
            LinearLayout mainViewBMI = popupWindowBMI.getContentView().findViewById(R.id.mainViewBMI);

            //обработка нажатия кнопки "Отправить"
            send_screenshot.setOnClickListener(v -> {
                createScreenShot(mainViewBMI);
                saveBitmap(createScreenShot(mainViewBMI));
                shareScreenshot();
            });

        } else {
            Toast toast = Toast.makeText(getApplication(),
                    "Возможно, ИМТ был рассчитан неправильно. " +
                            "Убедитесь, что ввели правильные значения роста и веса.",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //вывод на экран значений набранных вручную
    @SuppressLint("SetTextI18n")
    public void calculateBMI() {
        if (isSelectedTextView) {
            if (Double.parseDouble(sInput) < 1000) {
                sValueOne = toFormatString(sInput, "", 2);
                valueOne = Double.parseDouble(sValueOne);
                tvOut = toExpression(toFormatString(sInput, "ru", 2));
                string1_input.setText(tvOut);
            }
        } else {
            if (Double.parseDouble(sInput) < 1000) {
                sValueTwo = toFormatString(sInput, "", 2);
                valueTwo = Double.parseDouble(sValueTwo);
                tvOut = toExpression(toFormatString(sInput, "ru", 2));
                string2_input.setText(tvOut);
            }
        }
    }

    //окно выбора физической величины в окне ИМТ, принимает массив со списком величин
    public void alertDialogBMI(String[] array) {
        final Context context = this;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.ConverterAlertDialogStyle);

        //настройка шапки диалогового окна
        TextView title = new TextView(this);
        title.setText("Выберите величину");
        title.setPadding(0, 50, 10, 0);
        title.setTextColor(Color.BLACK);
        title.setTextSize(18);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        alertDialogBuilder.setCustomTitle(title);

        alertDialogBuilder
                .setItems(array, (dialog, position) -> {
                    switch (position) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                            selectedPositionInSpinner = position;
                            if (isSelectedSpinner) {
                                string1_add.setText(array[selectedPositionInSpinner]);
                                isSelectedTextView = true;
                                sInput = toCalculate(string1_input.getText().toString());
                                valueOne = Double.parseDouble(sInput);
                                string1_input.setTextColor(ContextCompat.getColor(getApplication(), R.color.Orange));
                                string2_input.setTextColor(ContextCompat.getColor(getApplication(), R.color.Black));
                            } else {
                                string2_add.setText(array[selectedPositionInSpinner]);
                                isSelectedTextView = false;
                                sInput = toCalculate(string2_input.getText().toString());
                                valueTwo = Double.parseDouble(sInput);
                                string2_input.setTextColor(ContextCompat.getColor(getApplication(), R.color.Orange));
                                string1_input.setTextColor(ContextCompat.getColor(getApplication(), R.color.Black));
                            }
                    }
                })
                .setNegativeButton("Отмена", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.BOTTOM);
        alertDialog.show();

        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextSize(16);
        negativeButton.setBackgroundResource(R.drawable.shape_rectangle_button);
        negativeButton.setTextColor(Color.BLACK);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        //отключаем allCaps для текста кнопок и устанавливаем отступы
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setLayoutParams(layoutParams);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
    }

    //вызов окна "Возраст"
    @SuppressLint({"ClickableViewAccessibility"})
    public void createViewAge() {
        toolbarBaseTitle.setText(getString(R.string.age));
        string1_spinner.setVisibility(View.GONE);
        string2_spinner.setVisibility(View.GONE);
        string1_input.setVisibility(View.INVISIBLE);
        string2_input.setVisibility(View.INVISIBLE);

        string1_text.setText("Дата рождения");
        string1_text.setTextColor(Color.parseColor("#636363"));
        string1_text.setTextSize(18);
        string1_add.setVisibility(View.GONE);

        string2_text.setText("Сегодня");
        string2_text.setTextColor(Color.parseColor("#636363"));
        string2_text.setTextSize(18);
        string2_add.setVisibility(View.INVISIBLE);

        view_converter_string3.setVisibility(View.INVISIBLE);
        view_converter_string4.setVisibility(View.INVISIBLE);
        view_converter_line.setVisibility(View.INVISIBLE);
        view_converter_keyboard.setVisibility(View.INVISIBLE);

        string1_editText.setInputType(InputType.TYPE_NULL);
        string2_editText.setInputType(InputType.TYPE_NULL);

        //стартовые даты
        string1_editText.setText(getRandomDateOnSpinner());
        string2_editText.setText(getCurrentDateOnSpinner());

        string1_editText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isSelectedDate = true;
                string1_editText.setTextColor(ContextCompat.getColor(getApplication(), R.color.Orange));
                string2_editText.setTextColor(ContextCompat.getColor(getApplication(), R.color.Black));
                datePickerSpinner(string1_text.getText().toString(), string1_editText);
            }
            return true;
        });

        string2_editText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isSelectedDate = false;
                string2_editText.setTextColor(ContextCompat.getColor(getApplication(), R.color.Orange));
                string1_editText.setTextColor(ContextCompat.getColor(getApplication(), R.color.Black));
                datePickerSpinner(string2_text.getText().toString(), string2_editText);
            }
            return true;
        });

        //показать всплывающее окно после загрузки окна
        new Handler().postDelayed(this::popUpWindowViewAge, 100);
    }

    //всплывающее окно для подсчета и вывода разницы между двумя датами
    public void popUpWindowViewAge() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d", Locale.US);
        LocalDate date1 = LocalDate.parse(parseDate(firstDate), formatter);
        LocalDate date2 = LocalDate.parse(parseDate(secondDate), formatter);

        long totalDaysWithNegativeValue = date2.toEpochDay() - date1.toEpochDay();

        Period difference = Period.between(date1, date2);

        int years = Math.abs(difference.getYears());
        int month = Math.abs(difference.getMonths());
        int days = Math.abs(difference.getDays());

        long totalYears = Math.abs(ChronoUnit.YEARS.between(date1, date2));
        long totalMonths = Math.abs(ChronoUnit.MONTHS.between(date1, date2));
        long totalWeeks = Math.abs(ChronoUnit.WEEKS.between(date1, date2));
        long totalDays = Math.abs(ChronoUnit.DAYS.between(date1, date2));
        long totalHours = Math.abs(totalDays * 24);
        long totalMinutes = Math.abs(totalHours * 60);

        //определяем день недели следующего дня рождения
        LocalDate nextBirthdayDate = LocalDate.parse(parseDate(firstDate), formatter);
        int nextBirthdayYear = date2.getYear() + 1;
        nextBirthdayDate = nextBirthdayDate.withYear(nextBirthdayYear);

        Period nextBirthdayDifference = Period.between(date2, nextBirthdayDate);

        int nextBirthdayMonth = Math.abs(nextBirthdayDifference.getMonths());
        int nextBirthdayDays = Math.abs(nextBirthdayDifference.getDays());

        //определяем день недели следующего дня рождения
        DayOfWeek nextBirthdayDayOfWeek = DayOfWeek.from(nextBirthdayDate);
        String sNextBirthdayDayOfWeek = "";

        switch (nextBirthdayDayOfWeek.getValue()) {
            case 1:
                sNextBirthdayDayOfWeek = "в понедельник";
                break;
            case 2:
                sNextBirthdayDayOfWeek = "во вторник";
                break;
            case 3:
                sNextBirthdayDayOfWeek = "в среду";
                break;
            case 4:
                sNextBirthdayDayOfWeek = "в четверг";
                break;
            case 5:
                sNextBirthdayDayOfWeek = "в пятницу";
                break;
            case 6:
                sNextBirthdayDayOfWeek = "в субботу";
                break;
            case 7:
                sNextBirthdayDayOfWeek = "в воскресенье";
                break;
        }

        String nextBirthdayMonthAge = nextBirthdayMonth + " "
                + declinationOfDate(nextBirthdayMonth, "месяц");
        String nextBirthdayDaysAge = nextBirthdayDays + " "
                + declinationOfDate(nextBirthdayDays, "день");

        String yearsNameAge = declinationOfDate(years, "год");
        String monthNameAge = month + " " + declinationOfDate(month, "месяц");
        String daysNameAge = days + " " + declinationOfDate(days, "день");

        //если указать дату позже текущей, то выдает предупреждение
        if (totalDaysWithNegativeValue >= 0) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            @SuppressLint("InflateParams")
            View popupViewBMI = inflater.inflate(R.layout.popup_window_age, null);

//            DisplayMetrics metrics = new DisplayMetrics();
//            getWindowManager().getDefaultDisplay().getMetrics(metrics);
//            int height = (int) (metrics.heightPixels * 0.7); //установил высоту 70% от размера экрана
//            int width = (metrics.widthPixels);

            popupWindowAge = new PopupWindow(popupViewBMI, LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, false);
            popupWindowAge.setOutsideTouchable(false);


            ((TextView) popupWindowAge.getContentView().findViewById(R.id.years_age))
                    .setText(String.valueOf(years));

            ((TextView) popupWindowAge.getContentView().findViewById(R.id.years_name_age))
                    .setText(yearsNameAge);

            ((TextView) popupWindowAge.getContentView().findViewById(R.id.month_age))
                    .setText(monthNameAge);

            ((TextView) popupWindowAge.getContentView().findViewById(R.id.days_age))
                    .setText(daysNameAge);

            ((TextView) popupWindowAge.getContentView().findViewById(R.id.total_years_age))
                    .setText(String.valueOf(totalYears));

            ((TextView) popupWindowAge.getContentView().findViewById(R.id.total_months_age))
                    .setText(String.valueOf(totalMonths));

            ((TextView) popupWindowAge.getContentView().findViewById(R.id.total_weeks_age))
                    .setText(String.valueOf(totalWeeks));

            ((TextView) popupWindowAge.getContentView().findViewById(R.id.total_days_age))
                    .setText(String.valueOf(totalDays));

            ((TextView) popupWindowAge.getContentView().findViewById(R.id.total_hours_age))
                    .setText(String.valueOf(totalHours));

            ((TextView) popupWindowAge.getContentView().findViewById(R.id.total_minutes_age))
                    .setText(String.valueOf(totalMinutes));

            ((TextView) popupWindowAge.getContentView().findViewById(R.id.next_birthday_day_of_week_age))
                    .setText(sNextBirthdayDayOfWeek);

            ((TextView) popupWindowAge.getContentView().findViewById(R.id.next_birthday_month_age))
                    .setText(nextBirthdayMonthAge);

            ((TextView) popupWindowAge.getContentView().findViewById(R.id.next_birthday_days_age))
                    .setText(nextBirthdayDaysAge);

            popupWindowAge.showAtLocation(toolbarBaseTitle, Gravity.BOTTOM, 0, 0);

            Button send_screenshot = popupWindowAge.getContentView().findViewById(R.id.send_screenshot_Age);
            LinearLayout mainViewAge = popupWindowAge.getContentView().findViewById(R.id.mainViewAge);

            //обработка нажатия кнопки "Отправить"
            send_screenshot.setOnClickListener(v -> {
                createScreenShot(mainViewAge);
                saveBitmap(createScreenShot(mainViewAge));
                shareScreenshot();
            });

        } else {
            Toast toast = Toast.makeText(getApplication(),
                    "Дата рождения не может быть позже сегодняшней даты",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //возвращает дату ввиде yyyy/M/d
    public String parseDate(String sDate) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy/M/d", Locale.US);
        Date date = null;
        try {
            date = sd.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(Objects.requireNonNull(date));
        int year = calendar.get(Calendar.YEAR);
        int month = (calendar.get(Calendar.MONTH) + 1);
        int days = calendar.get(Calendar.DAY_OF_MONTH);
        return (year + "/" + month + "/" + days);
    }

    //метод склоняет год, месяц, день
    //принимает дату, тип даты и подбирает нужное склонение
    public String declinationOfDate(int numDate, String nameDate) {
        String name = "";

        if (nameDate.equals("год")) {
            if (Math.abs(numDate) >= 10 && Math.abs(numDate) <= 20
                    || (Math.abs(numDate) % 10) >= 5
                    || Math.abs(numDate) == 0) {
                name = "лет";
            } else if ((Math.abs(numDate) % 10) < 5 && (Math.abs(numDate) % 10) > 1) {
                name = "года";
            } else
                name = "год";
        }

        if (nameDate.equals("месяц")) {
            if (Math.abs(numDate) >= 5 && Math.abs(numDate) <= 12 || Math.abs(numDate) == 0) {
                name = "месяцев";
            } else if ((Math.abs(numDate) % 10) < 5 && (Math.abs(numDate) % 10) > 1) {
                name = "месяца";
            } else
                name = "месяц";
        }

        if (nameDate.equals("день")) {
            if (Math.abs(numDate) >= 10 && Math.abs(numDate) <= 20 || (Math.abs(numDate) % 10) >= 5
                    || (Math.abs(numDate) % 10) == 0) {
                name = "дней";
            } else if ((Math.abs(numDate) % 10) < 5 & (Math.abs(numDate) % 10) > 1) {
                name = "дня";
            } else
                name = "день";
        }
        return name;
    }

    //получение случайной даты для первого спиннера
    public String getRandomDateOnSpinner() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy", Locale.US);

        int year = ThreadLocalRandom.current().nextInt(1989, calendar.get(Calendar.YEAR));
        int month = ThreadLocalRandom.current().nextInt(1, 12);
        int day = ThreadLocalRandom.current().nextInt(1, 28);
        firstDate = year + "/" + month + "/" + day;

        calendar.set(year, month - 1, day);
        string1_editText.setText(df.format(calendar.getTime()));

        return df.format(calendar.getTime());
    }

    //получение текущей даты для второго спиннера
    public String getCurrentDateOnSpinner() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy", Locale.US);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        secondDate = year + "/" + month + "/" + day;

        return df.format(calendar.getTime());
    }

    //диалоговое окно выбора даты и возврат строки с выбранной датой
    public void datePickerSpinner(String titleText, EditText date) {
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        datePicker = new DatePickerDialog(this, R.style.MySpinnerDatePickerStyle,
                (view, selectedYear, selectedMonthOfYear, selectedDayOfMonth) -> {
                    if (isSelectedDate) {
                        firstDate = selectedYear + "/" + (selectedMonthOfYear + 1) + "/" + selectedDayOfMonth;
                    } else
                        secondDate = selectedYear + "/" + (selectedMonthOfYear + 1) + "/" + selectedDayOfMonth;
                    year = selectedYear;
                    month = selectedMonthOfYear;
                    day = selectedDayOfMonth;
                    calendar.set(Calendar.YEAR, selectedYear);
                    calendar.set(Calendar.MONTH, selectedMonthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDayOfMonth);
                    SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy", Locale.US);
                    date.setText(df.format(calendar.getTime()));
                    if (keyIntent.equals("viewAge")) {
                        popUpWindowViewAge();
                    } else
                        popUpWindowViewDate();

                }, currentYear, currentMonth, currentDay);

        //сохраняем последнюю введенную дату
        datePicker.updateDate(year, month, day);

        TextView title = new TextView(this);
        title.setText(titleText);
        title.setPadding(0, 50, 10, 0);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        datePicker.setCustomTitle(title);

        datePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Отмена", datePicker);

        Objects.requireNonNull(datePicker.getWindow()).setGravity(Gravity.BOTTOM);

        datePicker.show();

        Button negativeButton = datePicker.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextSize(18);
        negativeButton.setBackgroundResource(R.drawable.shape_rectangle_button);
        negativeButton.setTextColor(Color.BLACK);

        Button positiveButton = datePicker.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setTextSize(18);
        positiveButton.setBackgroundResource(R.drawable.shape_rectangle_button);
        positiveButton.setTextColor(Color.rgb(70, 165, 231));

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)
                negativeButton.getLayoutParams();
        layoutParams.weight = 10;
        layoutParams.setMargins(15, 40, 40, 0);

        datePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setLayoutParams(layoutParams);
        datePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setLayoutParams(layoutParams);
        datePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setAllCaps(false);
        datePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setAllCaps(false);
    }

    //вызов окна "Дата"
    @SuppressLint("ClickableViewAccessibility")
    public void createViewDate() {
        toolbarBaseTitle.setText(getString(R.string.data));
        string1_spinner.setVisibility(View.GONE);
        string2_spinner.setVisibility(View.GONE);
        string1_input.setVisibility(View.INVISIBLE);
        string2_input.setVisibility(View.INVISIBLE);

        string1_text.setText("С");
        string1_text.setTextColor(Color.parseColor("#636363"));
        string1_text.setTextSize(18);
        string1_add.setVisibility(View.GONE);

        string2_text.setText("До");
        string2_text.setTextColor(Color.parseColor("#636363"));
        string2_text.setTextSize(18);
        string2_add.setVisibility(View.INVISIBLE);

        view_converter_string3.setVisibility(View.INVISIBLE);
        view_converter_string4.setVisibility(View.INVISIBLE);
        view_converter_line.setVisibility(View.INVISIBLE);
        view_converter_keyboard.setVisibility(View.INVISIBLE);

        string1_editText.setInputType(InputType.TYPE_NULL);
        string2_editText.setInputType(InputType.TYPE_NULL);

        //стартовые даты
        string1_editText.setText(getRandomDateOnSpinner());
        string2_editText.setText(getCurrentDateOnSpinner());

        string1_editText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isSelectedDate = true;
                string1_editText.setTextColor(ContextCompat.getColor(getApplication(), R.color.Orange));
                string2_editText.setTextColor(ContextCompat.getColor(getApplication(), R.color.Black));
                datePickerSpinner(string1_text.getText().toString(), string1_editText);
            }
            return true;
        });

        string2_editText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isSelectedDate = false;
                string2_editText.setTextColor(ContextCompat.getColor(getApplication(), R.color.Orange));
                string1_editText.setTextColor(ContextCompat.getColor(getApplication(), R.color.Black));
                datePickerSpinner(string2_text.getText().toString(), string2_editText);
            }
            return true;
        });

        //показать всплывающее окно после загрузки окна
        new Handler().postDelayed(this::popUpWindowViewDate, 100);
    }

    //всплывающее окно для подсчета и вывода разницы между двумя датами
    public void popUpWindowViewDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d", Locale.US);
        LocalDate date1 = LocalDate.parse(parseDate(firstDate), formatter);
        LocalDate date2 = LocalDate.parse(parseDate(secondDate), formatter);

        Period difference = Period.between(date1, date2);

        int years = Math.abs(difference.getYears());
        int month = Math.abs(difference.getMonths());
        int days = Math.abs(difference.getDays());

        DateTimeFormatter tvOutFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams")
        View popupViewBMI = inflater.inflate(R.layout.popup_window_date, null);

        popupWindowDate = new PopupWindow(popupViewBMI, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, false);
        popupWindowDate.setOutsideTouchable(false);

        ((TextView) popupWindowDate.getContentView().findViewById(R.id.years_date))
                .setText(String.valueOf(years));
        ((TextView) popupWindowDate.getContentView().findViewById(R.id.years_name_date))
                .setText(declinationOfDate(years, "год"));

        ((TextView) popupWindowDate.getContentView().findViewById(R.id.months_date))
                .setText(String.valueOf(month));
        ((TextView) popupWindowDate.getContentView().findViewById(R.id.months_name_date))
                .setText(declinationOfDate(month, "месяц"));

        ((TextView) popupWindowDate.getContentView().findViewById(R.id.days_date))
                .setText(String.valueOf(days));
        ((TextView) popupWindowDate.getContentView().findViewById(R.id.days_name_date))
                .setText(declinationOfDate(days, "день"));

        ((TextView) popupWindowDate.getContentView().findViewById(R.id.first_date))
                .setText(date1.format(tvOutFormatter));

        ((TextView) popupWindowDate.getContentView().findViewById(R.id.second_date))
                .setText(String.valueOf(date2.format(tvOutFormatter)));

        popupWindowDate.showAtLocation(toolbarBaseTitle, Gravity.BOTTOM, 0, 0);

        Button send_screenshot = popupWindowDate.getContentView().findViewById(R.id.send_screenshot_date);
        LinearLayout mainViewDate = popupWindowDate.getContentView().findViewById(R.id.mainViewDate);

        //обработка нажатия кнопки "Отправить"
        send_screenshot.setOnClickListener(v -> {
            createScreenShot(mainViewDate);
            saveBitmap(createScreenShot(mainViewDate));
            shareScreenshot();
        });

    }

    //вызов окна "Скидка"
    public void createViewDiscount() {
        toolbarBaseTitle.setText(getString(R.string.discount));
        //прячем спиннеры
        string1_spinner.setVisibility(View.GONE);
        string2_spinner.setVisibility(View.GONE);
        //задаем значения первой строки
        string1_text.setText("Первоначальная цена");
        string1_text.setTextColor(Color.BLACK);
        string1_text.setTextSize(18);
        string1_add.setVisibility(View.INVISIBLE);
        string1_editText.setVisibility(View.GONE);
        string2_editText.setVisibility(View.GONE);
        //задаем значения второй строки
        string2_text.setText("Скидка (%)");
        string2_text.setTextColor(Color.BLACK);
        string2_text.setTextSize(18);
        string2_add.setVisibility(View.INVISIBLE);
        //задаем значения третьей строки
        string3_text.setText("Окончательная цена");
        string3_text.setTextColor(Color.BLACK);
        string3_text.setTextSize(18);
        //задаем значения четвертой строки
        string4_text.setText("Вы сэкономите ");
        string4_text.setTextColor(getApplication().getResources().getColor(R.color.MediumGrey, getApplication().getTheme()));
        string4_text.setTextSize(18);
        //прячем кнопку "GO"
        c_plus_minus.setVisibility(View.GONE);
        c_btn_go.setVisibility(View.GONE);
    }

    //расчет для окна "Скидка"
    @SuppressLint("SetTextI18n")
    public void calculateDiscount() {
        if (isSelectedTextView) {
            //преобразуем строку в double, задаем региональные настройки
            //потом задаем кол-во цифр после запятой
            sValueOne = toFormatString(sInput, "", 2);
            //преобразуем строку в double
            valueOne = Double.parseDouble(sValueOne);
            //выводим на экран отформатированную строку
            tvOut = toExpression(toFormatString(sInput, "ru", 2));
            string1_input.setText(tvOut);
        } else {
            if (Double.parseDouble(sInput) <= 100) {
                sValueTwo = toFormatString(sInput, "", 1);
                valueTwo = Double.parseDouble(sValueTwo);
                tvOut = toExpression(toFormatString(sInput, "ru", 1));
                if (tvOut.equals("100,")) {
                    string2_input.setText("100");
                } else
                    string2_input.setText(tvOut);
            }
        }
        //подсчет сэкономленной суммы
        double dResult = valueOne / 100 * valueTwo;
        //подсчет окончательной цены
        String sResult = toFormatDouble(valueOne - dResult, "ru", 2);

        //убираем из вывода не цифры, например, сообщения "не число" или знак бесконечности
        if (sResult.matches("\\D+") || sResult.equals("-0")) {
            string3_output.setText("0");
        } else {
            string3_output.setText(sResult);
            string4_text.setText("Вы сэкономите " + toFormatDouble(dResult, "ru", 2));
        }
    }

    //вызов окна "Конвертер длины"
    public void createViewPercent() {
        toolbarBaseTitle.setText(getString(R.string.percent));
        string1_spinner.setVisibility(View.GONE);
        string2_spinner.setVisibility(View.GONE);
        string1_text.setText("Процент (%)");
        string1_text.setTextColor(Color.BLACK);
        string1_text.setTextSize(18);
        string1_add.setVisibility(View.INVISIBLE);
        string1_editText.setVisibility(View.GONE);
        string2_editText.setVisibility(View.GONE);
        string2_text.setText("Всего");
        string2_text.setTextColor(Color.BLACK);
        string2_text.setTextSize(18);
        string2_add.setVisibility(View.INVISIBLE);
        string3_text.setText("Результат");
        string3_text.setTextColor(Color.BLACK);
        string3_text.setTextSize(18);
        string4_text.setVisibility(View.INVISIBLE);
        c_plus_minus.setVisibility(View.GONE);
        c_btn_go.setVisibility(View.GONE);
    }

    //расчет для окна "Процент"
    @SuppressLint("SetTextI18n")
    public void calculatePercent() {
        if (isSelectedTextView) {
            if (Double.parseDouble(sInput) <= 100) {
                sValueOne = toFormatString(sInput, "", 1);
                valueOne = Double.parseDouble(sValueOne);
                tvOut = toExpression(toFormatString(sInput, "ru", 1));
                if (tvOut.equals("100,")) {
                    string1_input.setText("100");
                } else
                    string1_input.setText(tvOut);
            }
        } else {
            sValueTwo = toFormatString(sInput, "", 2);
            valueTwo = Double.parseDouble(sValueTwo);
            tvOut = toExpression(toFormatString(sInput, "ru", 2));
            string2_input.setText(tvOut);
        }

        String sResult = toFormatDouble(valueTwo / 100 * valueOne, "ru", 2);

        if (sResult.matches("\\D+")) {
            string3_output.setText("0");
        } else
            string3_output.setText(sResult);
    }

    //вызов окна "Конвертер длины"
    @SuppressLint("ClickableViewAccessibility")
    public void createViewLength() {
        toolbarBaseTitle.setText(getString(R.string.length_calc));
        view_converter_string3.setVisibility(View.INVISIBLE);
        view_converter_string4.setVisibility(View.GONE);
        string1_editText.setVisibility(View.GONE);
        string2_editText.setVisibility(View.GONE);
        c_plus_minus.setVisibility(View.GONE);
        c_btn_go.setVisibility(View.GONE);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_custom, getResources().getStringArray(R.array.lengthsShort));
        string1_spinner.setAdapter(adapter);
        string1_spinner.setSelection(selectedPositionInSpinner);
        string1_add.setText(getResources().getStringArray(R.array.lengths)[selectedPositionInSpinner]);

        String[] lengthsLong = getResources().getStringArray(R.array.lengths);
        String[] lengthsShort = getResources().getStringArray(R.array.lengthsShort);
        String[] lengthsUnion = new String[lengthsLong.length];

        for (int i = 0; i < lengthsLong.length; i++) {
            lengthsUnion[i] = lengthsLong[i] + " " + lengthsShort[i];
        }

        string1_spinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isSelectedSpinner = true;
                converterAlertDialog(lengthsUnion, lengthsLong, string1_spinner, toLength);
            }
            return true;
        });

        string2_spinner.setAdapter(adapter);
        string2_spinner.setSelection(selectedPositionInSpinner);
        string2_add.setText(getResources().getStringArray(R.array.lengths)[selectedPositionInSpinner]);

        string2_spinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isSelectedSpinner = false;
                converterAlertDialog(lengthsUnion, lengthsLong, string2_spinner, toLength);
            }
            return true;
        });
    }

    //расчет для окна "Конвертер длины"
    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    public void calculateLength() {
        if (isSelectedTextView) {
            sValueOne = toFormatString(sInput, "", 10);
            valueOne = Double.parseDouble(sValueOne);
            tvOut = toExpression(toFormatString(sInput, "ru", 10));
            string1_input.setText(tvOut);

            double res1 = valueOne * toLength.get(string1_spinner.getSelectedItem().toString())
                    * 1.0 / (toLength.get(string2_spinner.getSelectedItem().toString()));
            tvOut = toFormatDouble(res1, "ru", 10);
            string2_input.setText(tvOut);
        } else {
            sValueTwo = toFormatString(sInput, "", 10);
            valueTwo = Double.parseDouble(sValueTwo);
            tvOut = toExpression(toFormatString(sInput, "ru", 10));
            string2_input.setText(tvOut);

            double res2 = valueTwo * toLength.get(string2_spinner.getSelectedItem().toString())
                    * 1.0 / (toLength.get(string1_spinner.getSelectedItem().toString()));
            tvOut = toFormatDouble(res2, "ru", 10);
            string1_input.setText(tvOut);
        }
    }

    //вызов окна "Конвертер площади"
    @SuppressLint("ClickableViewAccessibility")
    public void createViewSquare() {
        toolbarBaseTitle.setText(getString(R.string.square_calc));
        view_converter_string3.setVisibility(View.INVISIBLE);
        view_converter_string4.setVisibility(View.GONE);
        string1_editText.setVisibility(View.GONE);
        string2_editText.setVisibility(View.GONE);
        c_plus_minus.setVisibility(View.GONE);
        c_btn_go.setVisibility(View.GONE);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_custom, getResources().getStringArray(R.array.squaresShort));
        string1_spinner.setAdapter(adapter);
        string1_spinner.setSelection(selectedPositionInSpinner);
        string1_add.setText(getResources().getStringArray(R.array.squares)[selectedPositionInSpinner]);

        String[] squaresLong = getResources().getStringArray(R.array.squares);
        String[] squaresShort = getResources().getStringArray(R.array.squaresShort);
        String[] squaresUnion = new String[squaresLong.length];

        for (int i = 0; i < squaresLong.length; i++) {
            squaresUnion[i] = squaresLong[i] + " " + squaresShort[i];
        }

        string1_spinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isSelectedSpinner = true;
                converterAlertDialog(squaresUnion, squaresLong, string1_spinner, toSquare);
            }
            return true;
        });

        string2_spinner.setAdapter(adapter);
        string2_spinner.setSelection(selectedPositionInSpinner);
        string2_add.setText(getResources().getStringArray(R.array.squares)[selectedPositionInSpinner]);

        string2_spinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isSelectedSpinner = false;
                converterAlertDialog(squaresUnion, squaresLong, string2_spinner, toSquare);
            }
            return true;
        });
    }

    //расчет для окна "Конвертер площади"
    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    public void calculateSquare() {
        if (isSelectedTextView) {
            sValueOne = toFormatString(sInput, "", 10);
            valueOne = Double.parseDouble(sValueOne);
            tvOut = toExpression(toFormatString(sInput, "ru", 10));
            string1_input.setText(tvOut);

            double res1 = valueOne * toSquare.get(string1_spinner.getSelectedItem().toString())
                    * 1.0 / (toSquare.get(string2_spinner.getSelectedItem().toString()));
            tvOut = toFormatDouble(res1, "ru", 10);
            string2_input.setText(tvOut);
        } else {
            sValueTwo = toFormatString(sInput, "", 10);
            valueTwo = Double.parseDouble(sValueTwo);
            tvOut = toExpression(toFormatString(sInput, "ru", 10));
            string2_input.setText(tvOut);

            double res2 = valueTwo * toSquare.get(string2_spinner.getSelectedItem().toString())
                    * 1.0 / (toSquare.get(string1_spinner.getSelectedItem().toString()));
            tvOut = toFormatDouble(res2, "ru", 10);
            string1_input.setText(tvOut);
        }
    }

    //вызов окна "Конвертер объема"
    @SuppressLint("ClickableViewAccessibility")
    public void createViewVolume() {
        toolbarBaseTitle.setText(getString(R.string.volume_calc));
        view_converter_string3.setVisibility(View.INVISIBLE);
        view_converter_string4.setVisibility(View.GONE);
        string1_editText.setVisibility(View.GONE);
        string2_editText.setVisibility(View.GONE);
        c_plus_minus.setVisibility(View.GONE);
        c_btn_go.setVisibility(View.GONE);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_custom, getResources().getStringArray(R.array.volumesShort));
        string1_spinner.setAdapter(adapter);
        string1_spinner.setSelection(selectedPositionInSpinner);
        string1_add.setText(getResources().getStringArray(R.array.volumes)[selectedPositionInSpinner]);

        String[] volumesLong = getResources().getStringArray(R.array.volumes);
        String[] volumesShort = getResources().getStringArray(R.array.volumesShort);
        String[] volumesUnion = new String[volumesLong.length];

        for (int i = 0; i < volumesLong.length; i++) {
            volumesUnion[i] = volumesLong[i] + " " + volumesShort[i];
        }

        string1_spinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isSelectedSpinner = true;
                converterAlertDialog(volumesUnion, volumesLong, string1_spinner, toVolume);
            }
            return true;
        });

        string2_spinner.setAdapter(adapter);
        string2_spinner.setSelection(selectedPositionInSpinner);
        string2_add.setText(getResources().getStringArray(R.array.squares)[selectedPositionInSpinner]);

        string2_spinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isSelectedSpinner = false;
                converterAlertDialog(volumesUnion, volumesLong, string2_spinner, toVolume);
            }
            return true;
        });
    }

    //расчет для окна "Конвертер объема"
    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    public void calculateVolume() {
        if (isSelectedTextView) {
            sValueOne = toFormatString(sInput, "", 10);
            valueOne = Double.parseDouble(sValueOne);
            tvOut = toExpression(toFormatString(sInput, "ru", 10));
            string1_input.setText(tvOut);

            double res1 = valueOne * toVolume.get(string1_spinner.getSelectedItem().toString())
                    * 1.0 / (toVolume.get(string2_spinner.getSelectedItem().toString()));
            tvOut = toFormatDouble(res1, "ru", 10);
            string2_input.setText(tvOut);
        } else {
            sValueTwo = toFormatString(sInput, "", 10);
            valueTwo = Double.parseDouble(sValueTwo);
            tvOut = toExpression(toFormatString(sInput, "ru", 10));
            string2_input.setText(tvOut);

            double res2 = valueTwo * toVolume.get(string2_spinner.getSelectedItem().toString())
                    * 1.0 / (toVolume.get(string1_spinner.getSelectedItem().toString()));
            tvOut = toFormatDouble(res2, "ru", 10);
            string1_input.setText(tvOut);
        }
    }

    //вызов окна "Конвертер температуры"
    @SuppressLint("ClickableViewAccessibility")
    public void createViewTemperature() {
        toolbarBaseTitle.setText(getString(R.string.temperature_calc));
        view_converter_string3.setVisibility(View.INVISIBLE);
        view_converter_string4.setVisibility(View.GONE);
        string1_editText.setVisibility(View.GONE);
        string2_editText.setVisibility(View.GONE);
        c_btn_go.setVisibility(View.GONE);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_custom, getResources().getStringArray(R.array.temperatureShort));
        string1_spinner.setAdapter(adapter);
        string1_spinner.setSelection(selectedPositionInSpinner);
        string1_add.setText(getResources().getStringArray(R.array.temperature)[selectedPositionInSpinner]);

        String[] temperatureLong = getResources().getStringArray(R.array.temperature);
        String[] temperatureShort = getResources().getStringArray(R.array.temperatureShort);
        String[] temperatureUnion = new String[temperatureLong.length];

        for (int i = 0; i < temperatureLong.length; i++) {
            temperatureUnion[i] = temperatureLong[i] + " " + temperatureShort[i];
        }

        string1_spinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isSelectedSpinner = true;
                converterAlertDialogTemperature(temperatureUnion, temperatureLong, string1_spinner);
            }
            return true;
        });

        string2_spinner.setAdapter(adapter);
        string2_spinner.setSelection(selectedPositionInSpinner);
        string2_add.setText(getResources().getStringArray(R.array.temperature)[selectedPositionInSpinner]);

        string2_spinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isSelectedSpinner = false;
                converterAlertDialogTemperature(temperatureUnion, temperatureLong, string2_spinner);
            }
            return true;
        });
    }

    //вывод на экран значений набранных вручную и расчета для окна "Конвертер температуры"
    public void calculateTemperature() {
        if (isSelectedTextView) {
            sValueOne = toFormatString(sInput, "", 10);
            valueOne = Double.parseDouble(sValueOne);
            tvOut = toExpression(toFormatString(sInput, "ru", 10));
            string1_input.setText(tvOut);

            double res1 = convertTemperature(valueOne,
                    string1_spinner.getSelectedItem().toString(),
                    string2_spinner.getSelectedItem().toString());

            tvOut = toFormatDouble(res1, "ru", 10);
            string2_input.setText(tvOut);
        } else {
            sValueTwo = toFormatString(sInput, "", 10);
            valueTwo = Double.parseDouble(sValueTwo);
            tvOut = toExpression(toFormatString(sInput, "ru", 10));
            string2_input.setText(tvOut);

            double res2 = convertTemperature(valueTwo,
                    string2_spinner.getSelectedItem().toString(),
                    string1_spinner.getSelectedItem().toString());

            tvOut = toFormatDouble(res2, "ru", 10);
            string1_input.setText(tvOut);
        }
    }

    //расчет для окна "Конвертер температуры"
    public double convertTemperature(double degree, String from, String to) {
        //формулы
//        5 °C + 273,15 = 278,15 K              - 5 цельсий в кельвин
//        (5 °C × 9/5) + 32 = 41 °F             - 5 цельсий в фаренгейт

//        (5 °F − 32) × 5/9 = -15 °C            - 5 фаренгейт в цельсий
//        (5 °F − 32) × 5/9 + 273,15 = 258,15 K - 5 фаренгейт в кельвин

//        (5 K − 273,15) × 9/5 + 32 = -450,7 °F - 5 кельвин в фаренгейт
//        5 K − 273,15 = -268,1 °C              - 5 кельвин в цельсий

        double result;

        double Cel_To_Kel = degree + 273.15;
        double Cel_To_Far = degree * (9.0 / 5) + 32;

        double Far_To_Cel = (degree - 32) * (5.0 / 9);
        double Far_To_Kel = (degree - 32) * (5.0 / 9) + 273.15;

        double Kel_To_Far = (degree - 273.15) * (9.0 / 5) + 32;
        double Kel_To_Cel = degree - 273.15;

        if (from.equals("°C") & to.equals("K")) {
            result = Cel_To_Kel;
        } else if (from.equals("°C") & to.equals("°F")) {
            result = Cel_To_Far;
        } else if (from.equals("°F") & to.equals("°C")) {
            result = Far_To_Cel;
        } else if (from.equals("°F") & to.equals("K")) {
            result = Far_To_Kel;
        } else if (from.equals("K") & to.equals("°F")) {
            result = Kel_To_Far;
        } else if (from.equals("K") & to.equals("°C")) {
            result = Kel_To_Cel;
        } else
            result = degree;

        //округляем десятичные до 4 штук
        return new BigDecimal(result).setScale(4, RoundingMode.HALF_UP).doubleValue();
    }

    //окно выбора физической величины окна "Конвертер температуры"
    public void converterAlertDialogTemperature(String[] arrayUnion, String[] arrayLong, Spinner spinner) {
        final Context context = this;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.ConverterAlertDialogStyle);

        //настройка шапки диалогового окна
        TextView title = new TextView(this);
        title.setText("Выберите величину");
        title.setPadding(0, 50, 10, 0);
        title.setTextColor(Color.BLACK);
        title.setTextSize(18);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        alertDialogBuilder.setCustomTitle(title);

        //найстройка сообщения
        alertDialogBuilder
                .setItems(arrayUnion, (dialog, position) -> {
                    switch (position) {
                        case 0:
                        case 1:
                        case 2:
                            selectedPositionInSpinner = position;
                            spinner.setSelection(selectedPositionInSpinner);
                            if (isSelectedSpinner) {
                                string1_add.setText(arrayLong[selectedPositionInSpinner]);
                            } else {
                                string2_add.setText(arrayLong[selectedPositionInSpinner]);
                            }
                            calculateConverterTemperature();
                    }
                })
                .setNegativeButton("Отмена", (dialog, id) -> dialog.dismiss());

        //создание alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.BOTTOM);
        alertDialog.show();

        //настройка кнопки "Отмена"
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextSize(16);
        negativeButton.setBackgroundResource(R.drawable.shape_rectangle_button);
        negativeButton.setTextColor(Color.BLACK);

        //получаем параметры линейного макета
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        //отключаем allCaps для текста кнопок и устанавливаем отступы
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setLayoutParams(layoutParams);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
    }

    //расчет для окна "Конвертер температуры" с учетом выбора спиннера
    public void calculateConverterTemperature() {
        if (isSelectedSpinner) {
            if (!isSelectedTextView) {
                sValueOne = toCalculate(string2_input.getText().toString());
                valueOne = Double.parseDouble(sValueOne);

                double res1 = convertTemperature(valueOne,
                        string2_spinner.getSelectedItem().toString(),
                        string1_spinner.getSelectedItem().toString());

                tvOut = toFormatDouble(res1, "ru", 10);
                string1_input.setText(tvOut);
            } else {
                sValueTwo = toCalculate(string1_input.getText().toString());
                valueTwo = Double.parseDouble(sValueOne);

                double res2 = convertTemperature(valueTwo,
                        string1_spinner.getSelectedItem().toString(),
                        string2_spinner.getSelectedItem().toString());

                tvOut = toFormatDouble(res2, "ru", 10);
                string2_input.setText(tvOut);
            }
        } else {
            if (isSelectedTextView) {
                sValueTwo = toCalculate(string1_input.getText().toString());
                valueTwo = Double.parseDouble(sValueOne);

                double res2 = convertTemperature(valueTwo,
                        string1_spinner.getSelectedItem().toString(),
                        string2_spinner.getSelectedItem().toString());

                tvOut = toFormatDouble(res2, "ru", 10);
                string2_input.setText(tvOut);
            } else {
                sValueOne = toCalculate(string2_input.getText().toString());
                valueOne = Double.parseDouble(sValueOne);

                double res1 = convertTemperature(valueOne,
                        string2_spinner.getSelectedItem().toString(),
                        string1_spinner.getSelectedItem().toString());

                tvOut = toFormatDouble(res1, "ru", 10);
                string1_input.setText(tvOut);
            }
        }
    }

    //вызов окна "Конвертер скорости"
    @SuppressLint("ClickableViewAccessibility")
    public void createViewSpeed() {
        toolbarBaseTitle.setText(getString(R.string.speed_calc));
        view_converter_string3.setVisibility(View.INVISIBLE);
        view_converter_string4.setVisibility(View.GONE);
        string1_editText.setVisibility(View.GONE);
        string2_editText.setVisibility(View.GONE);
        c_plus_minus.setVisibility(View.GONE);
        c_btn_go.setVisibility(View.GONE);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_custom, getResources().getStringArray(R.array.speedsShort));
        string1_spinner.setAdapter(adapter);
        string1_spinner.setSelection(selectedPositionInSpinner);
        string1_add.setText(getResources().getStringArray(R.array.speeds)[selectedPositionInSpinner]);

        String[] speedsLong = getResources().getStringArray(R.array.speeds);
        String[] speedsShort = getResources().getStringArray(R.array.speedsShort);
        String[] speedsUnion = new String[speedsLong.length];

        for (int i = 0; i < speedsLong.length; i++) {
            speedsUnion[i] = speedsLong[i] + " " + speedsShort[i];
        }

        string1_spinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isSelectedSpinner = true;
                converterAlertDialog(speedsUnion, speedsLong, string1_spinner, toSpeed);
            }
            return true;
        });

        string2_spinner.setAdapter(adapter);
        string2_spinner.setSelection(selectedPositionInSpinner);
        string2_add.setText(getResources().getStringArray(R.array.speeds)[selectedPositionInSpinner]);

        string2_spinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isSelectedSpinner = false;
                converterAlertDialog(speedsUnion, speedsLong, string2_spinner, toSpeed);
            }
            return true;
        });
    }

    //расчет для окна "Конвертер скорости"
    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    public void calculateSpeed() {
        if (isSelectedTextView) {
            sValueOne = toFormatString(sInput, "", 10);
            valueOne = Double.parseDouble(sValueOne);
            tvOut = toExpression(toFormatString(sInput, "ru", 10));
            string1_input.setText(tvOut);

            double res1 = valueOne * toSpeed.get(string1_spinner.getSelectedItem().toString())
                    * 1.0 / (toSpeed.get(string2_spinner.getSelectedItem().toString()));
            tvOut = toFormatDouble(res1, "ru", 10);
            string2_input.setText(tvOut);
        } else {
            sValueTwo = toFormatString(sInput, "", 10);
            valueTwo = Double.parseDouble(sValueTwo);
            tvOut = toExpression(toFormatString(sInput, "ru", 10));
            string2_input.setText(tvOut);

            double res2 = valueTwo * toSpeed.get(string2_spinner.getSelectedItem().toString())
                    * 1.0 / (toSpeed.get(string1_spinner.getSelectedItem().toString()));
            tvOut = toFormatDouble(res2, "ru", 10);
            string1_input.setText(tvOut);
        }
    }

    //вызов окна "Конвертер времени"
    @SuppressLint("ClickableViewAccessibility")
    public void createViewTime() {
        toolbarBaseTitle.setText(getString(R.string.time_calc));
        view_converter_string3.setVisibility(View.INVISIBLE);
        view_converter_string4.setVisibility(View.GONE);
        string1_editText.setVisibility(View.GONE);
        string2_editText.setVisibility(View.GONE);
        c_plus_minus.setVisibility(View.GONE);
        c_btn_go.setVisibility(View.GONE);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_custom, getResources().getStringArray(R.array.timeShort));
        string1_spinner.setAdapter(adapter);
        string1_spinner.setSelection(selectedPositionInSpinner);
        string1_add.setText(getResources().getStringArray(R.array.time)[selectedPositionInSpinner]);

        String[] timeLong = getResources().getStringArray(R.array.time);
        String[] timeShort = getResources().getStringArray(R.array.timeShort);
        String[] timeUnion = new String[timeLong.length];

        for (int i = 0; i < timeLong.length; i++) {
            timeUnion[i] = timeLong[i] + " " + timeShort[i];
        }

        string1_spinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isSelectedSpinner = true;
                converterAlertDialog(timeUnion, timeLong, string1_spinner, toTime);
            }
            return true;
        });

        string2_spinner.setAdapter(adapter);
        string2_spinner.setSelection(selectedPositionInSpinner);
        string2_add.setText(getResources().getStringArray(R.array.time)[selectedPositionInSpinner]);

        string2_spinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isSelectedSpinner = false;
                converterAlertDialog(timeUnion, timeLong, string2_spinner, toTime);
            }
            return true;
        });
    }

    //расчет для окна "Конвертер времени"
    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    public void calculateTime() {
        if (isSelectedTextView) {
            sValueOne = toFormatString(sInput, "", 10);
            valueOne = Double.parseDouble(sValueOne);
            tvOut = toExpression(toFormatString(sInput, "ru", 10));
            string1_input.setText(tvOut);

            double res1 = valueOne * toTime.get(string1_spinner.getSelectedItem().toString())
                    * 1.0 / (toTime.get(string2_spinner.getSelectedItem().toString()));
            tvOut = toFormatDouble(res1, "ru", 10);
            string2_input.setText(tvOut);
        } else {
            sValueTwo = toFormatString(sInput, "", 10);
            valueTwo = Double.parseDouble(sValueTwo);
            tvOut = toExpression(toFormatString(sInput, "ru", 10));
            string2_input.setText(tvOut);

            double res2 = valueTwo * toTime.get(string2_spinner.getSelectedItem().toString())
                    * 1.0 / (toTime.get(string1_spinner.getSelectedItem().toString()));
            tvOut = toFormatDouble(res2, "ru", 10);
            string1_input.setText(tvOut);
        }
    }

    //вызов окна "Конвертер массы"
    @SuppressLint("ClickableViewAccessibility")
    public void createViewWeight() {
        toolbarBaseTitle.setText(getString(R.string.weight_calc));
        view_converter_string3.setVisibility(View.INVISIBLE);
        view_converter_string4.setVisibility(View.GONE);
        string1_editText.setVisibility(View.GONE);
        string2_editText.setVisibility(View.GONE);
        c_plus_minus.setVisibility(View.GONE);
        c_btn_go.setVisibility(View.GONE);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_custom, getResources().getStringArray(R.array.weightShort));
        string1_spinner.setAdapter(adapter);
        string1_spinner.setSelection(selectedPositionInSpinner);
        string1_add.setText(getResources().getStringArray(R.array.weight)[selectedPositionInSpinner]);

        String[] weightLong = getResources().getStringArray(R.array.weight);
        String[] weightShort = getResources().getStringArray(R.array.weightShort);
        String[] weightUnion = new String[weightLong.length];

        for (int i = 0; i < weightLong.length; i++) {
            weightUnion[i] = weightLong[i] + " " + weightShort[i];
        }

        string1_spinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isSelectedSpinner = true;
                converterAlertDialog(weightUnion, weightLong, string1_spinner, toWeight);
            }
            return true;
        });

        string2_spinner.setAdapter(adapter);
        string2_spinner.setSelection(selectedPositionInSpinner);
        string2_add.setText(getResources().getStringArray(R.array.weight)[selectedPositionInSpinner]);

        string2_spinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isSelectedSpinner = false;
                converterAlertDialog(weightUnion, weightLong, string2_spinner, toWeight);
            }
            return true;
        });
    }

    //расчет для окна "Конвертер массы"
    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    public void calculateWeight() {
        if (isSelectedTextView) {
            sValueOne = toFormatString(sInput, "", 10);
            valueOne = Double.parseDouble(sValueOne);
            tvOut = toExpression(toFormatString(sInput, "ru", 10));
            string1_input.setText(tvOut);

            double res1 = valueOne * toWeight.get(string1_spinner.getSelectedItem().toString())
                    * 1.0 / (toWeight.get(string2_spinner.getSelectedItem().toString()));
            tvOut = toFormatDouble(res1, "ru", 10);
            string2_input.setText(tvOut);
        } else {
            sValueTwo = toFormatString(sInput, "", 10);
            valueTwo = Double.parseDouble(sValueTwo);
            tvOut = toExpression(toFormatString(sInput, "ru", 10));
            string2_input.setText(tvOut);

            double res2 = valueTwo * toWeight.get(string2_spinner.getSelectedItem().toString())
                    * 1.0 / (toWeight.get(string1_spinner.getSelectedItem().toString()));
            tvOut = toFormatDouble(res2, "ru", 10);
            string1_input.setText(tvOut);
        }
    }

    //вызов окна "Разелить счет"
    public void createViewSplitBill() {
        toolbarBaseTitle.setText(getString(R.string.split_bill));
        string1_spinner.setVisibility(View.GONE);
        string2_spinner.setVisibility(View.GONE);
        string1_text.setText("Сумма");
        string1_text.setTextColor(Color.BLACK);
        string1_text.setTextSize(18);
        string1_add.setVisibility(View.INVISIBLE);
        string1_editText.setVisibility(View.GONE);
        string2_editText.setVisibility(View.GONE);
        string2_text.setText("Люди");
        string2_text.setTextColor(Color.BLACK);
        string2_text.setTextSize(18);
        string2_add.setVisibility(View.INVISIBLE);
        string3_text.setText("Разделить сумму");
        string3_text.setTextColor(Color.BLACK);
        string3_text.setTextSize(18);
        string4_text.setVisibility(View.INVISIBLE);
        c_plus_minus.setVisibility(View.GONE);
        c_btn_go.setVisibility(View.GONE);
    }

    //расчет для окна "Разделить счет"
    @SuppressLint("SetTextI18n")
    public void calculateSplitBill() {
        if (isSelectedTextView) {
            sValueOne = toFormatString(sInput, "", 2);
            valueOne = Double.parseDouble(sValueOne);
            tvOut = toExpression(toFormatString(sInput, "ru", 2));
            string1_input.setText(tvOut);
        } else {
            if (Double.parseDouble(sInput) < 1000) {
                sValueTwo = toFormatString(sInput, "", 0);
                valueTwo = Double.parseDouble(sValueTwo);
                tvOut = toExpression(toFormatString(sInput, "ru", 0));
                if (tvOut.equals("999,")) {
                    string2_input.setText("999");
                } else
                    string2_input.setText(tvOut);
            }
        }

        String sResult = toFormatDouble(valueOne / valueTwo, "ru", 2);

        if (sResult.matches("\\D+")) {
            string3_output.setText("0");
        } else
            string3_output.setText(sResult);
    }

    //вызов окна "О приложении"
    @SuppressLint("SetTextI18n")
    public void createViewAbout() {
        view_converter_screen.setVisibility(View.GONE);
        view_converter_line.setVisibility(View.GONE);
        view_converter_keyboard.setVisibility(View.GONE);
        LinearLayout base = findViewById(R.id.activity_base);
        //пользовательнские параметры Layout
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, convertPixelToDp(1)
        );
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
                "\nРазработано в учебных целях." +
                "\nE-mail: ilyxan89@gmail.com");
        Linkify.addLinks(aboutAppTextDetail, Linkify.EMAIL_ADDRESSES);
        aboutAppTextDetail.setTextSize(16);
        aboutAppTextDetail.setLinkTextColor(Color.MAGENTA);
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

    //преобразование знака "." в ","
    public String toExpression(String expression) {
        if (expression.indexOf('.') != -1) {
            expression = expression.replace('.', ',');
        }
        return expression;
    }

    //преобразование строки формата 1 000,02 в 1000.02
    //убираем пробелы и заменяем запятую на точку
    public String toCalculate(String expression) {
        if (expression.indexOf(',') != -1) {
            expression = expression.replace(',', '.');
        }
        return expression.replaceAll("\\s+", "");
    }

    //делит целую часть числа на триады, если выбрать Locale "ru",
    //выдает целое число без десятичной,
    //если выбрать Locale "", без деления на триады
    //amountDec - количество знаков после запятой
    public String toFormatDouble(double expression, String locale, int amountDec) {
        Locale local = new Locale(locale);
        NumberFormat formatter = NumberFormat.getInstance(local);
        formatter.setMaximumFractionDigits(amountDec);
        return formatter.format(expression);
    }

    //преобразует строку задачи из 1000.02 в 1 000.02, amountDec - количество знаков после запятой
    public String toFormatString(String expression, String locale, int amountDec) {
        int length = amountDec + 1;
        if (expression.equals("-")) {
            expression = "0";
        }
        StringBuilder sb = new StringBuilder();
        if (expression.contains(".")) {
            String[] arr = expression.split("(?=[.])");
            for (int i = 0; i < arr.length; i++) {
                if (i % 2 == 0) {
                    sb.append(toFormatDouble(Double.parseDouble(arr[i]), locale, 0));
                } else if (arr[i].length() <= length) {
                    int lenArr = arr[i].length();
                    sb.append(arr[i].substring(0, lenArr));
                } else
                    sb.append(arr[i].substring(0, length));
            }
        } else sb.append(toFormatDouble(Double.parseDouble(expression), locale, 0));
        return sb.toString();
    }

    //метод конвертирует число пикселей в dp
    public int convertPixelToDp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (value * density);
    }

    //метод принимает 2 массива с полным и коротким названием величины (Ярды ярд),
    //при нажатии на спиннер будет вызвано диалоговое окно с выбором величины
    //и хэшмап с коротким названием величины и ее множитель
    public void converterAlertDialog(String[] arrayUnion, String[] arrayLong, Spinner spinner, HashMap<String, Double> hashMap) {

        final Context context = this;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.ConverterAlertDialogStyle);

        //настройка шапки диалогового окна
        TextView title = new TextView(this);
        title.setText("Выберите величину");
        title.setPadding(0, 50, 10, 0);
        title.setTextColor(Color.BLACK);
        title.setTextSize(18);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        alertDialogBuilder.setCustomTitle(title);

        //найстройка сообщения
        alertDialogBuilder
                .setItems(arrayUnion, (dialog, position) -> {
                    switch (position) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                            selectedPositionInSpinner = position;
                            spinner.setSelection(selectedPositionInSpinner);
                            if (isSelectedSpinner) {
                                string1_add.setText(arrayLong[selectedPositionInSpinner]);
                            } else {
                                string2_add.setText(arrayLong[selectedPositionInSpinner]);
                            }
                            calculateConverters(hashMap);
                    }
                })
                .setNegativeButton("Отмена", (dialog, id) -> dialog.dismiss());

        //создание alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.BOTTOM);
        alertDialog.show();

        //устанавливаем высоту 85% от высоты экрана, если количество элементов в массиве больше 9
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        if (arrayUnion.length > 9) {
            int height = (int) (metrics.heightPixels * 0.85);
            int width = (metrics.widthPixels);
            alertDialog.getWindow().setLayout(width, height);
        }

        //настройка кнопки "Отмена"
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextSize(16);
        negativeButton.setBackgroundResource(R.drawable.shape_rectangle_button);
        negativeButton.setTextColor(Color.BLACK);

        //получаем параметры линейного макета
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        //отключаем allCaps для текста кнопок и устанавливаем отступы
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setLayoutParams(layoutParams);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
    }

    //метод для подсчета конвертеров: Длина, Площадь, Объём, Скорость, Время, Масса
    public void calculateConverters(HashMap<String, Double> hashMap) {
        if (isSelectedSpinner) {
            if (!isSelectedTextView) {
//                String takeValue = toCalculate(string2_input.getText().toString());
                sValueOne = toCalculate(string2_input.getText().toString());
                valueOne = Double.parseDouble(sValueOne);
                double res1 = valueOne * hashMap.get(string2_spinner.getSelectedItem().toString())
                        * 1.0 / (hashMap.get(string1_spinner.getSelectedItem().toString()));
//                tvOut = toExpression(toFormatDouble(res1, "ru", 10));
                tvOut = toFormatDouble(res1, "ru", 10);
                string1_input.setText(tvOut);

//                valueOne = Double.parseDouble(toCalculate(string2_input.getText().toString()));
//                double res1 = value1 * hashMap.get(string2_spinner.getSelectedItem().toString())
//                        * 1 / (hashMap.get(string1_spinner.getSelectedItem().toString()));
////                tvOut = toExpression(toFormatDouble(res1, "ru", 10));
//                tvOut = toFormatDouble(res1, "ru", 10);
//                sValueOne = value1;
//                string1_input.setText(tvOut);
            } else {
//                double value2 = Double.parseDouble(string1_input.getText().toString());
                sValueTwo = toCalculate(string1_input.getText().toString());
                valueTwo = Double.parseDouble(sValueOne);
                double res2 = valueTwo * hashMap.get(string1_spinner.getSelectedItem().toString())
                        * 1.0 / (hashMap.get(string2_spinner.getSelectedItem().toString()));
//                tvOut = toExpression(toFormatDouble(res2, "ru", 10));
                tvOut = toFormatDouble(res2, "ru", 10);
//                sValueTwo = tvOut;
                string2_input.setText(tvOut);
            }
        } else {
            if (isSelectedTextView) {
                sValueTwo = toCalculate(string1_input.getText().toString());
                valueTwo = Double.parseDouble(sValueOne);
//                double value2 = Double.parseDouble(string1_input.getText().toString());
                double res2 = valueTwo * hashMap.get(string1_spinner.getSelectedItem().toString())
                        * 1.0 / (hashMap.get(string2_spinner.getSelectedItem().toString()));
//                tvOut = toExpression(toFormatDouble(res2, "ru", 10));
                tvOut = toFormatDouble(res2, "ru", 10);
//                sValueTwo = tvOut;
                string2_input.setText(tvOut);
            } else {
                sValueOne = toCalculate(string2_input.getText().toString());
                valueOne = Double.parseDouble(sValueOne);
//                double value1 = Double.parseDouble(string2_input.getText().toString());
                double res1 = valueOne * hashMap.get(string2_spinner.getSelectedItem().toString())
                        * 1.0 / (hashMap.get(string1_spinner.getSelectedItem().toString()));
//                tvOut = toExpression(toFormatDouble(res1, "ru", 10));
                tvOut = toFormatDouble(res1, "ru", 10);
//                sValueOne = tvOut;
                string1_input.setText(tvOut);
            }
        }
    }

    //сделать скриншот View
    public Bitmap createScreenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    //сохранить скриншот
    public void saveBitmap(Bitmap bitmap) {
        //именуем файл скриншота с учетом даты и времени
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyyHHmmss", Locale.US);
        String formattedDate = df.format(c.getTime());
        String imageName = "/Image_" + formattedDate + ".png";

        imagePath = new File(getApplication().getExternalFilesDir(null) + imageName);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e("ilyxan89 ", e.getMessage(), e);
        }
    }

    //делимся скриншотом ИМТ
    private void shareScreenshot() {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", imagePath);
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/*");

        //тема сообщения
        switch (keyIntent) {
            case "viewBMI":
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Мой индекс массы тела");
                break;
            case "viewAge":
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Мой возраст");
                break;
            case "viewDate":
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Разница между датами");
                break;
        }
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Скриншот");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            startActivity(Intent.createChooser(sharingIntent, "Поделиться скриншотом"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplication(), "No App Available", Toast.LENGTH_SHORT).show();
        }
    }

    //класс в котором заполнены HashMap с физическими величинами и их коэффициентами
    public static class Coefficients {
        HashMap<String, Double> toLength(){
            HashMap<String, Double> toLength = new HashMap<>();
            toLength.put("км", 1000.0);
            toLength.put("м", 1.0);
            toLength.put("дм", 0.1);
            toLength.put("см", 0.01);
            toLength.put("мм", 0.001);
            toLength.put("мкм", 0.000001);
            toLength.put("нм", 0.000000001);
            toLength.put("nmi", 1852.0);
            toLength.put("mi", 1609.344);
            toLength.put("ярд", 0.9144);
            toLength.put("ft", 0.3048);
            toLength.put("in", 0.0254);
            return toLength;
        }

        HashMap<String, Double> toSquare() {
            HashMap<String, Double> toSquare = new HashMap<>();
            toSquare.put("км²", 1000000.0);
            toSquare.put("ha", 10000.0);
            toSquare.put("a", 100.0);
            toSquare.put("м²", 1.0);
            toSquare.put("дм²", 0.01);
            toSquare.put("см²", 0.0001);
            toSquare.put("мм²", 0.000001);
            toSquare.put("мкм²", 0.000000000001);
            toSquare.put("акр", 4046.856);
            toSquare.put("mi²", 2590002.59);
            toSquare.put("ярд²", 1/1.19599);
            toSquare.put("ft²", 1/10.76391);
            toSquare.put("in²", 1/1550.003);
            return toSquare;
        }

        HashMap<String, Double> toVolume() {
            HashMap<String, Double> toVolume = new HashMap<>();
            toVolume.put("м³", 1.0);
            toVolume.put("дм³", 0.001);
            toVolume.put("см³", 0.000001);
            toVolume.put("мм³", 0.000000001);
            toVolume.put("гл", 0.1);
            toVolume.put("л", 0.001);
            toVolume.put("дл", 0.0001);
            toVolume.put("сл", 0.00001);
            toVolume.put("мл", 0.000001);
            toVolume.put("ярд³", 1/1.307951);
            toVolume.put("ft³", 1/35.31467);
            toVolume.put("in³", 1/61023.740);
            return toVolume;
        }

        HashMap<String, Double> toSpeed() {
            HashMap<String, Double> toSpeed = new HashMap<>();
            toSpeed.put("м/с", 1.0);
            toSpeed.put("км/ч", 1/3.6);
            toSpeed.put("км/с", 1000.0);
            toSpeed.put("М", 340.3);
            toSpeed.put("уз", 1/1.943845);
            toSpeed.put("миль/ч", 1/2.236936);
            toSpeed.put("ips", 1/39.37008);
            toSpeed.put("fps", 1/3.28084);
            toSpeed.put("c", 299792458.0);
            return toSpeed;
        }

        HashMap<String, Double> toTime() {
            HashMap<String, Double> toSpeed = new HashMap<>();
            toSpeed.put("г", 1.0);
            toSpeed.put("н", 1/52.142849);
            toSpeed.put("д", 1/365.0);
            toSpeed.put("ч", 1/8760.0);
            toSpeed.put("мин", 1/525600.0);
            toSpeed.put("сек", 1/31536000.0);
            toSpeed.put("мс", 1/31536000000.0);
            toSpeed.put("мкс", 1/31536000000000.0);
            toSpeed.put("пс", 1/31536000000000000000.0);
            return toSpeed;
        }

        HashMap<String, Double> toWeight() {
            HashMap<String, Double> toSpeed = new HashMap<>();
            toSpeed.put("т", 1000.0);
            toSpeed.put("ц", 100.0);
            toSpeed.put("кг", 1.0);
            toSpeed.put("г", 0.001);
            toSpeed.put("мг", 0.000001);
            toSpeed.put("мкг", 0.000000001);
            toSpeed.put("lb", 1/2.204623);
            toSpeed.put("oz", 1/35.274);
            toSpeed.put("кар", 1/5000.0);
            return toSpeed;
        }
    }
}


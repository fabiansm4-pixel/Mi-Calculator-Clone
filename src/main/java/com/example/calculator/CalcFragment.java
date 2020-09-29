package com.example.calculator;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Stack;
import java.util.StringTokenizer;

import static android.content.Context.MODE_PRIVATE;


public class CalcFragment extends Fragment {

    TextView history;
    TextView task;
    TextView result;

    SharedPreferences sharedPreferences;

    String sTask = "";
    String sResult = "";
    String formattedNum = "";

    //костыль для скрытия нуля при работе с отрицательными значениями использую 0!0, эквивалент 0+0
    String hiddenZero = "0!0";

    boolean isPressedEqual;
    boolean isPressedClear;

    int statusApp;

    PostFix postFix;
    Calculator calc;

    private static final String FILE_HISTORY = "history.txt";
    private static final String FILE_TASK = "task.txt";

    public static final String SHARED_PREFS = "";
    public static final String STATUS = "";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        history = view.findViewById(R.id.history);
        history.setMovementMethod(new ScrollingMovementMethod());

        result = view.findViewById(R.id.result);
        task = view.findViewById(R.id.task);

        final Button btn0 = view.findViewById(R.id.btn0);
        final Button btn1 = view.findViewById(R.id.btn1);
        final Button btn2 = view.findViewById(R.id.btn2);
        final Button btn3 = view.findViewById(R.id.btn3);
        final Button btn4 = view.findViewById(R.id.btn4);
        final Button btn5 = view.findViewById(R.id.btn5);
        final Button btn6 = view.findViewById(R.id.btn6);
        final Button btn7 = view.findViewById(R.id.btn7);
        final Button btn8 = view.findViewById(R.id.btn8);
        final Button btn9 = view.findViewById(R.id.btn9);
        final Button btn_virgule = view.findViewById(R.id.btn_comma);
        final Button btn_clear = view.findViewById(R.id.btn_clear);
        final Button btn_erase = view.findViewById(R.id.btn_erase);
        final Button btn_percent = view.findViewById(R.id.btn_percent);
        final Button btn_div = view.findViewById(R.id.btn_div);
        final Button btn_multi = view.findViewById(R.id.btn_multi);
        final Button btn_minus = view.findViewById(R.id.btn_minus);
        final Button btn_plus = view.findViewById(R.id.btn_plus);
        final Button btn_equal = view.findViewById(R.id.btn_equal);

        //включение анимация кнопки равно
        btn_equal.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    animationScaleEqual(btn_equal);
                }
                return false;
            }
        });

        final View.OnClickListener listener = new View.OnClickListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                int id = v.getId();
                isPressedClear = false;
                try {
                    switch (id) {
                        //Цифры
                        case R.id.btn0:
                            addHistory();
                            hideResult();
                            if (sTask.isEmpty() || sTask.equals("0")) {
                                clear();
                            } else
                                sTask += "0";
                            break;
                        case R.id.btn1:
                            addHistory();
                            hideResult();
                            if (sTask.equals("0")) {
                                sTask = "1";
                            } else
                                sTask += "1";
                            break;
                        case R.id.btn2:
                            addHistory();
                            hideResult();
                            if (sTask.equals("0")) {
                                sTask = "2";
                            } else
                                sTask += "2";
                            break;
                        case R.id.btn3:
                            addHistory();
                            hideResult();
                            if (sTask.equals("0")) {
                                sTask = "3";
                            } else
                                sTask += "3";
                            break;
                        case R.id.btn4:
                            addHistory();
                            hideResult();
                            if (sTask.equals("0")) {
                                sTask = "4";
                            } else
                                sTask += "4";
                            break;
                        case R.id.btn5:
                            addHistory();
                            hideResult();
                            if (sTask.equals("0")) {
                                sTask = "5";
                            } else
                                sTask += "5";
                            break;
                        case R.id.btn6:
                            addHistory();
                            hideResult();
                            if (sTask.equals("0")) {
                                sTask = "6";
                            } else
                                sTask += "6";
                            break;
                        case R.id.btn7:
                            addHistory();
                            hideResult();
                            if (sTask.equals("0")) {
                                sTask = "7";
                            } else
                                sTask += "7";
                            break;
                        case R.id.btn8:
                            addHistory();
                            hideResult();
                            if (sTask.equals("0")) {
                                sTask = "8";
                            } else
                                sTask += "8";
                            break;
                        case R.id.btn9:
                            addHistory();
                            hideResult();
                            if (sTask.equals("0")) {
                                sTask = "9";
                            } else
                                sTask += "9";
                            break;

                        //Знаки
                        case R.id.btn_comma:
                            addHistory();
                            hideResult();
                            if (sTask.endsWith(".")) {
                                break;
                            } else if (sTask.isEmpty()) {
                                sTask += "0.";
                            } else if (sTask.endsWith("+")
                                    || sTask.endsWith("-")
                                    || sTask.endsWith("/")
                                    || sTask.endsWith("*")) {
                                sTask += "0.";
                            } else sTask += ".";
                            break;

                        //очистить поле задачи
                        case R.id.btn_clear:
                            clear();
                            break;

                        //удалить 1 знак справа в task
                        case R.id.btn_erase:
                            if (sTask.isEmpty() || isPressedEqual) {
                                break;
                            }
                            erase();
                            break;

                        //процент
                        case R.id.btn_percent:
                            if (sTask.isEmpty()
                                    || sTask.endsWith(".")
                                    || sTask.endsWith("+")
                                    || sTask.endsWith("-")
                                    || sTask.endsWith("/")
                                    || sTask.endsWith("*")) {
                                break;
                            } else if (isPressedEqual) {
                                transferResultValueToTask();
                            }
                            calculatePercent();
                            break;

                        //операторы
                        case R.id.btn_div:
                            hideResult();
                            if (isPressedEqual) {
                                transferResultValueToTask();
                            }
                            if (sTask.endsWith("/")) {
                                break;
                            } else if (sTask.isEmpty()) {
                                sTask += "0/";
                            } else if (sTask.endsWith("+")
                                    || sTask.endsWith("-")
                                    || sTask.endsWith("*")) {
                                sTask = sTask.substring(0, sTask.length() - 1);
                                sTask += "/";
                            } else sTask += "/";
                            break;
                        case R.id.btn_multi:
                            hideResult();
                            if (isPressedEqual) {
                                transferResultValueToTask();
                            }
                            if (sTask.endsWith("*")) {
                                break;
                            } else if (sTask.isEmpty()) {
                                sTask += "0*";
                            } else if (sTask.endsWith("+")
                                    || sTask.endsWith("-")
                                    || sTask.endsWith("/")) {
                                sTask = sTask.substring(0, sTask.length() - 1);
                                sTask += "*";
                            } else sTask += "*";
                            break;
                        case R.id.btn_minus:
                            hideResult();
                            if (isPressedEqual) {
                                transferResultValueToTask();
                            }
                            if (sTask.endsWith("-")) {
                                break;
                            } else if (sTask.isEmpty()) {
                                sTask += "0-";
                            } else if (sTask.endsWith("+")
                                    || sTask.endsWith("*")
                                    || sTask.endsWith("/")) {
                                sTask = sTask.substring(0, sTask.length() - 1);
                                sTask += "-";
                            } else sTask += "-";
                            break;
                        case R.id.btn_plus:
                            hideResult();
                            if (isPressedEqual) {
                                transferResultValueToTask();
                            }
                            if (sTask.endsWith("+")) {
                                break;
                            } else if (sTask.isEmpty()) {
                                sTask += "0+";
                            } else if (sTask.endsWith("*")
                                    || sTask.endsWith("-")
                                    || sTask.endsWith("/")) {
                                sTask = sTask.substring(0, sTask.length() - 1);
                                sTask += "+";
                            } else sTask += "+";
                            break;

                        case R.id.btn_equal:
                            if (sTask.equals("") || isPressedEqual) {
                                break;
                            } else {
                                animationScaleDown(task);
                                animationScaleUp(result);
                                addHistory();
                                isPressedEqual = true;
                            }
                            break;

                        default:
                            throw new IllegalStateException("Unexpected value: " + id);
                    }

                    if (checkValid(toCalculate(sTask)) && sTask.length() > 0) {
                        sTask = toCalculate(sTask);
                        calculate();//вычисление
                        release();//вывод задачи и ответа на экран
                    }

                } catch (Throwable e) {
                    clear();
                    toCalculate(sTask);
                    calculate();
                    task.setText("Error: " + e.getMessage());
                }
            }
        };

        btn0.setOnClickListener(listener);
        btn1.setOnClickListener(listener);
        btn2.setOnClickListener(listener);
        btn3.setOnClickListener(listener);
        btn4.setOnClickListener(listener);
        btn5.setOnClickListener(listener);
        btn6.setOnClickListener(listener);
        btn7.setOnClickListener(listener);
        btn8.setOnClickListener(listener);
        btn9.setOnClickListener(listener);
        btn_virgule.setOnClickListener(listener);
        btn_clear.setOnClickListener(listener);
        btn_erase.setOnClickListener(listener);
        btn_percent.setOnClickListener(listener);
        btn_div.setOnClickListener(listener);
        btn_multi.setOnClickListener(listener);
        btn_minus.setOnClickListener(listener);
        btn_plus.setOnClickListener(listener);
        btn_equal.setOnClickListener(listener);
    }

    //вычисление
    @SuppressLint("DefaultLocale")
    public void calculate() {
        if (sTask.length() > 0 && (Character.isDigit(sTask.charAt(sTask.length() - 1)))) {
            postFix = new PostFix(sTask);
            calc = new Calculator(postFix.getPostfixAsList());
            formattedNum = toFormatString(calc.result());
            sResult = toExpression(toFormatDouble(calc.result()));
        }
    }

    //вывод задачи и ответа на экран
    @SuppressLint("SetTextI18n")
    public void release() {
        countingOnFly();
        task.setText(toExpression(toFormatTask(sTask)));
        //отлавливаем 0!0 и заменям их на знак минус
        task.setText(task.getText().toString().replace("0 ! 0 - ", "-"));
        task.setText(task.getText().toString().replace("0 ! ", "-"));
        if (sTask.endsWith("0!0")) {
            task.setText(task.getText().toString().substring(0, task.getText().length() - 1));
        }
        if (task.getText().toString().contains("--")) {
            task.setText(task.getText().toString().replace("--", "-"));
        }
        result.setText("= " + sResult);

        if (sTask.equals("0")) {
            clear();
        }
    }

    //если нажато Clear, то значения сбрасываются на "" и result исчезает
    @SuppressLint("SetTextI18n")
    public void clear() {
        isPressedClear = true;
        animationScaleReset(task);
        animationScaleReset(result);
        sTask = "0";
        sResult = "";
        task.setText("0");
        hideResult();
    }

    //метод удаления одного знака справа
    public void erase() {
        if ((sTask.length() == 1) || (sTask.equals("0."))) {
            clear();
        } else if (task.getText().toString().startsWith("-")
                & task.getText().toString().length() == 2) {
            clear();
        } else if (sTask.endsWith("0!0-")) {
            sTask = sTask.substring(0, sTask.length() - 4);
        } else if (sTask.endsWith("0!0")) {
            sTask = sTask.substring(0, sTask.length() - 3);
        } else
            sTask = sTask.substring(0, sTask.length() - 1);
    }

    //корректный вывод result, если task заканчивается на знак оператора
    public void countingOnFly() {
        if (sTask.endsWith("+")) {
            sTask = sTask.substring(0, sTask.length() - 1);
            calculate();
            sTask += "+";
        } else if (sTask.endsWith("-")) {
            sTask = sTask.substring(0, sTask.length() - 1);
            calculate();
            sTask += "-";
        } else if (sTask.endsWith("*")) {
            sTask = sTask.substring(0, sTask.length() - 1);
            calculate();
            sTask += "*";
        } else if (sTask.endsWith("/")) {
            sTask = sTask.substring(0, sTask.length() - 1);
            calculate();
            sTask += "/";
        } else if (sTask.endsWith(".")) {
            sTask = sTask.substring(0, sTask.length() - 1);
            calculate();
            sTask += ".";
        }
    }

    //перенос числа из result в task
    public void transferResultValueToTask() {
        String tempString = formattedNum;
        addHistory();
        sTask = tempString;
    }

    //принимает double и преобразует в текст число с максимум 8ми числами после запятой
    //или убирает ноль, если число целое
    public String toFormatString(double expression) {
        DecimalFormat df = new DecimalFormat("0.#");
        df.setMaximumFractionDigits(8);
        return df.format(expression);
    }

    //делит целую часть числа на триады
    public String toFormatDouble(double expression) {
        Locale local = new Locale("ru");
        NumberFormat formatter = NumberFormat.getInstance(local);
        formatter.setMaximumFractionDigits(8);
        return formatter.format(expression);
    }

    //преобразует строку задачи из 1000*1000.02 в 1 000 * 1 000.02
    public String toFormatTask(String expression) {
        String[] taskArray = expression.split("(?<=[-+*/!])|(?=[-+*/!])");
        StringBuilder sb = new StringBuilder();
            for (int i = 0; i < taskArray.length; i++) {
                if (i % 2 == 0) {
                    if (taskArray[i].contains(".")) {
                        String[] arr2 = taskArray[i].split("(?=[.])");
                        for (int j = 0; j < arr2.length; j++) {
                            if (j % 2 == 0) {
                                sb.append(toFormatDouble(Double.parseDouble(arr2[j])));
                            } else sb.append(arr2[j]);
                        }
                    } else sb.append(toFormatDouble(Double.parseDouble(taskArray[i])));
                } else sb.append(" ").append(taskArray[i]).append(" ");
            }
        return sb.toString();
    }

    //преобразование знаков "* / ." в "× ÷ ,"
    public String toExpression(String expression) {
        if (expression.indexOf('*') != -1) {
            expression = expression.replace('*', '×');
        }
        if (expression.indexOf('/') != -1) {
            expression = expression.replace('/', '÷');
        }
        if (expression.indexOf('.') != -1) {
            expression = expression.replace('.', ',');
        }
        return expression;
    }

    //преобразование знаков "× ÷ ," в "* / ."
    public String toCalculate(String expression) {
        //в случае отрицательного числа, подставить перед отр. числом ноль
        //т.к. постфиксный калькулятор не умеет, напрямую, работать с отриц.значениями
        if (expression.length() > 0 && expression.charAt(0) == '-') {
            expression = hiddenZero + expression;
            expression = expression.replace('×', '*');
        }
        if (expression.length() > 0 && expression.indexOf('×') != -1) {
            expression = expression.replace('×', '*');
        }
        if (expression.indexOf('÷') != -1) {
            expression = expression.replace('÷', '/');
        }
        if (expression.indexOf(',') != -1) {
            expression = expression.replace(',', '.');
        }
        return expression;
    }

    @SuppressLint("SetTextI18n")
    public boolean checkValid(String expression) {
        for (int i = 1; i < expression.length(); i++) {
            if (expression.charAt(i) == '+'
                    || expression.charAt(i) == '-'
                    || expression.charAt(i) == '*'
                    || expression.charAt(i) == '/') {
                if (expression.charAt(i - 1) == '+'
                        || expression.charAt(i - 1) == '-'
                        || expression.charAt(i - 1) == '*'
                        || expression.charAt(i - 1) == '/') {
                    clear();
                    task.setText("Error valid");
                    return false;
                }
            }
        }
        return true;
    }

    //если нажали равно, то при следующем вводе задача и результат переносятся в историю
    public void addHistory() {
        if (isPressedEqual) {
            history.append(task.getText().toString() + '\n');
            history.append("= " + sResult + '\n' + '\n');
            animationScaleReset(task);
            animationScaleReset(result);
            sTask = "";
            sResult = "0";
            isPressedEqual = false;
        }
    }

    //анимация увеличения текста
    public void animationScaleUp(TextView view) {
        if (result.length() > 14) {
            ScaleAnimation animation = new ScaleAnimation(1, (float) 1.1, 1, (float) 1.1,
                    Animation.RELATIVE_TO_SELF, 1f,
                    Animation.RELATIVE_TO_SELF, 1f);
            animation.setFillAfter(true);
            animation.setDuration(180);
            view.startAnimation(animation);
        } else {
            ScaleAnimation animation = new ScaleAnimation(1, (float) 1.8, 1, (float) 1.8,
                    Animation.RELATIVE_TO_SELF, 1f,
                    Animation.RELATIVE_TO_SELF, 1f);
            animation.setFillAfter(true);
            animation.setDuration(180);
            view.startAnimation(animation);
        }
    }

    //анимация уменьшения текста
    public void animationScaleDown(TextView view) {
        if (result.length() > 20) {
            ScaleAnimation animation = new ScaleAnimation(1, (float) 0.8, 1, (float) 0.8,
                    Animation.RELATIVE_TO_SELF, 1f,
                    Animation.RELATIVE_TO_SELF, 0.2f);
            animation.setFillAfter(true);
            animation.setDuration(180);
            view.startAnimation(animation);
        } else {
            ScaleAnimation animation = new ScaleAnimation(1, (float) 0.6, 1, (float) 0.6,
                    Animation.RELATIVE_TO_SELF, 1f,
                    Animation.RELATIVE_TO_SELF, 0.2f);
            animation.setFillAfter(true);
            animation.setDuration(180);
            view.startAnimation(animation);
        }
    }

    //сброс анимации
    public void animationScaleReset(TextView view) {
        ScaleAnimation animation = new ScaleAnimation(1, 1, 1, 1,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 1f);
        animation.setFillBefore(true);
        animation.setDuration(180);
        view.startAnimation(animation);
    }

    //анимация кнопки "равно"
    public void animationScaleEqual(TextView view) {
        ScaleAnimation animation = new ScaleAnimation(1, (float) 0.9, 1, (float) 0.9,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setFillBefore(true);
        animation.setDuration(300);
        view.startAnimation(animation);
    }

    //подсчет процента
    public void calculatePercent() {
        String[] taskArray = sTask.split("(?<=[-+*/!])|(?=[-+*/!])");
        double lastNum = Double.parseDouble(taskArray[taskArray.length - 1]);
        String lastOperator = "";
        StringBuilder sbTask = new StringBuilder();
        if (taskArray.length == 1 && taskArray[0].equals("0")) {
            sTask = "0";
        } else if (taskArray.length == 1) {
            sTask = toFormatString(calc.result() / 100);
        } else
            lastOperator = taskArray[taskArray.length - 2];
        switch (lastOperator) {
            case "+":
            case "-":
                taskArray[taskArray.length - 1] = "";
                taskArray[taskArray.length - 2] = "";
                for (String s : taskArray) {
                    sbTask.append(s);
                }
                sTask = sbTask.toString();
                calculate();
                double percentage = calc.result() / 100 * lastNum;
                double modulo = Math.abs(percentage);
                if ((percentage < 0) & (lastOperator.equals("-"))) {
                    sTask += "-" + hiddenZero + modulo;
                    //если не добавить ноль, то при подсчете процента от отрицательного числа метод calculate()
                    //получает выражение типа 0-1+-1.0, т.е. подряд идут пара операторов +-
                    //также, метод не умеет работать с отрицательными числами
                } else if ((percentage < 0) & (lastOperator.equals("+"))) {
                    sTask += "+" + hiddenZero + percentage;
                } else
                    sTask += lastOperator + toFormatString(percentage);
                break;
            case "*":
            case "/":
                taskArray[taskArray.length - 1] = toFormatString(lastNum / 100);
                for (String s : taskArray) {
                    sbTask.append(s);
                }
                sTask = sbTask.toString();
                break;
        }
    }

    //классы Calculator и PostFix - движок постфиксного калькулятора
    public static class Calculator {
        private ArrayList<String> postfixArray;
        private Stack<Double> stack = new Stack<>();// Used stack because it has all functions needed for postfix

        public double one;
        public double two;

        // Push to last, pop last digit are the only required
        public Calculator(ArrayList<String> postfix) {
            this.postfixArray = postfix;
        }

        public double result() {

            for (int i = 0; i < postfixArray.size(); i++) {
                if (Character.isDigit(postfixArray.get(i).charAt(0))
                        || postfixArray.get(i).charAt(0) == '.') {
                    stack.push(Double.parseDouble(postfixArray.get(i)));
                } else {
                    one = stack.pop();
                    two = stack.pop();
                    // Does TWO - ONE because ONE is removed before TWO
                    // "3 2 -" results in ONE as 2 and TWO as 3 (must do 3-2, which is TWO-ONE
                    switch (postfixArray.get(i)) {
                        case "+":
                        case "!":
                            stack.push(two + one);
                            break;
                        case "-":
                            stack.push(two - one);
                            break;
                        case "*":
                            stack.push(two * one);
                            break;
                        case "/":
                            stack.push(two / one);
                            break;
                    }
                }
            }
            return stack.pop();
        }
    }

    public static class PostFix {
        private String original;
        private ArrayList<String> postfixArr = new ArrayList<>();
        private Stack<Character> stack = new Stack<>();

        public PostFix(String expression) {
            original = expression;
            convertExpression();
        }

        public void convertExpression() {
            StringTokenizer tokenizer = new StringTokenizer(original, "+-*/!", true);

            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (Character.isDigit(token.charAt(0))) {
                    postfixArr.add(token);
                } else {
                    addToStack(token.charAt(0));
                }
            }
            addStackToPostFix();
        }

        public void addToStack(char input) {
            if (stack.isEmpty())
                stack.push(input);
            else {
                if (getPriority(input) <= getPriority(stack.peek())) {
                    while (!stack.isEmpty()) {
                        postfixArr.add(stack.pop().toString());
                    }
                }
                stack.push(input);
            }
        }

        public int getPriority(char op) {
            switch (op) {
                case '+':
                case '-':
                case '!':
                    return 1;
                case '*':
                case '/':
                    return 2;
                default:
                    return 0;
            }
        }

        public void addStackToPostFix() {
            while (!stack.isEmpty()) {
                postfixArr.add(stack.pop().toString());
            }
        }

        public ArrayList<String> getPostfixAsList() {
            return postfixArr;
        }
    }

    //сохрание history в текстовый файл
    public void saveHistory() {
        String text_history = history.getText().toString();
        if (text_history.isEmpty()) text_history = " ";
        FileOutputStream fos = null;
        try {
            fos = Objects.requireNonNull(getContext()).openFileOutput(FILE_HISTORY, MODE_PRIVATE);
            fos.write(text_history.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //сохрание sTask в текстовый файл и в SharedPreferences передаем последнее состояние приложения
    //было ли нажато равно, очистить или состояние набора цифр
    public void saveTask() {
        if (sTask.isEmpty()) sTask = "0";
        FileOutputStream fos = null;
        try {
            fos = Objects.requireNonNull(getContext()).openFileOutput(FILE_TASK, MODE_PRIVATE);
            fos.write(sTask.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (isPressedEqual) {
            statusApp = 1; //нажато equal
        } else if (isPressedClear) {
            statusApp = 2; //нажато clear
        } else
            statusApp = 3; //состояние набора цифр для задачи

        editor.putInt(STATUS, statusApp);
        editor.apply();
    }

    //загрузка истории из файла
    public void loadHistory() {
        FileInputStream fis = null;
        try {
            fis = Objects.requireNonNull(getContext()).openFileInput(FILE_HISTORY);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }
            history.setText(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //загрузка задачи из файла и получение состояния приложения из SharedPreferences
    @SuppressLint("SetTextI18n")
    public void loadTask() {
        FileInputStream fis = null;
        try {
            fis = Objects.requireNonNull(getContext()).openFileInput(FILE_TASK);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null) {
                sb.append(text);
            }
            sTask = sb.toString();
            calculate();
            release();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        statusApp = sharedPreferences.getInt(STATUS, 0);

        if (statusApp == 1) {
            isPressedEqual = true;
            animationScaleDown(task);
            animationScaleUp(result);
        } else if (statusApp == 2) {
            clear();
        } else
            statusApp = 3;
    }

    //Спрятать строку result, если нажато Clear
    public void hideResult() {
        result.setText("");
        if (isPressedClear) {
            result.setVisibility(View.GONE);
        } else
            result.setVisibility(View.VISIBLE);
    }

    //при первом запуске приложения показать крупный ноль
    public void firstLaunch() {
        if (statusApp == 0) {
            task.setText("0");
            animationScaleReset(task);
            animationScaleReset(result);
            result.setVisibility(View.GONE);
        } else if (!task.getText().toString().equals("0")) {
            result.setVisibility(View.VISIBLE);
        }
    }

    //загружаем данные при запуске
    @Override
    public void onStart() {
        super.onStart();
        firstLaunch();
        loadHistory();
        loadTask();
    }

    //загружаем данные при восстановлении
    @Override
    public void onResume() {
        super.onResume();
        firstLaunch();
        loadHistory();
        loadTask();
    }

    //сохранияем данные при сворачивании
    @Override
    public void onPause() {
        super.onPause();
        saveHistory();
        saveTask();
    }

    //сохранияем данные при закрытии
    @Override
    public void onDestroy() {
        super.onDestroy();
        saveHistory();
        saveTask();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        return inflater.inflate(R.layout.fragment_calc, container, false);
    }
}

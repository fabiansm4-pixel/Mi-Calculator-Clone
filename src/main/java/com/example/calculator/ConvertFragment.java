package com.example.calculator;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ConvertFragment extends Fragment {

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout bmi_button = view.findViewById(R.id.bmi_button);
        LinearLayout age_button = view.findViewById(R.id.age_button);
        LinearLayout discount_button = view.findViewById(R.id.discount_button);
        LinearLayout percent_button = view.findViewById(R.id.percent_button);
        LinearLayout data_button = view.findViewById(R.id.data_button);
        LinearLayout length_button = view.findViewById(R.id.length_button);
        LinearLayout square_button = view.findViewById(R.id.square_button);
        LinearLayout volume_button = view.findViewById(R.id.volume_button);
        LinearLayout temperature_button = view.findViewById(R.id.temperature_button);
        LinearLayout speed_button = view.findViewById(R.id.speed_button);
        LinearLayout time_button = view.findViewById(R.id.time_button);
        LinearLayout weight_button = view.findViewById(R.id.weight_button);
        LinearLayout scale_of_notation_button = view.findViewById(R.id.scale_of_notation_button);

        View.OnClickListener listenerMoneyButton = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case R.id.bmi_button:
                        callViewByKey("viewBMI");
                        break;
                    case R.id.age_button:
                        callViewByKey("viewAge");
                        break;
                    case R.id.discount_button:
                        callViewByKey("viewDiscount");
                        break;
                    case R.id.percent_button:
                        callViewByKey("viewPercent");
                        break;
                    case R.id.data_button:
                        callViewByKey("viewData");
                        break;
                    case R.id.length_button:
                        callViewByKey("viewLength");
                        break;
                    case R.id.square_button:
                        callViewByKey("viewSquare");
                        break;
                    case R.id.volume_button:
                        callViewByKey("viewVolume");
                        break;
                    case R.id.temperature_button:
                        callViewByKey("viewTemperature");
                        break;
                    case R.id.speed_button:
                        callViewByKey("viewSpeed");
                        break;
                    case R.id.time_button:
                        callViewByKey("viewTime");
                        break;
                    case R.id.weight_button:
                        callViewByKey("viewWeight");
                        break;
                    case R.id.scale_of_notation_button:
                        callViewByKey("viewScaleOfNotation");
                        break;
                }
            }
        };

        bmi_button.setOnClickListener(listenerMoneyButton);
        age_button.setOnClickListener(listenerMoneyButton);
        discount_button.setOnClickListener(listenerMoneyButton);
        percent_button.setOnClickListener(listenerMoneyButton);
        data_button.setOnClickListener(listenerMoneyButton);
        length_button.setOnClickListener(listenerMoneyButton);
        square_button.setOnClickListener(listenerMoneyButton);
        volume_button.setOnClickListener(listenerMoneyButton);
        temperature_button.setOnClickListener(listenerMoneyButton);
        speed_button.setOnClickListener(listenerMoneyButton);
        time_button.setOnClickListener(listenerMoneyButton);
        weight_button.setOnClickListener(listenerMoneyButton);
        scale_of_notation_button.setOnClickListener(listenerMoneyButton);
    }

    //метод принимает строковый ключ, добавляет его в intent и запускает Activity
    public void callViewByKey(String key) {
        Intent intent = new Intent(getContext(), BaseActivity.class);
        intent.putExtra("keyIntent", key);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        return inflater.inflate(R.layout.fragment_convert, container, false);
    }
}

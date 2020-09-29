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

    LinearLayout bmi_button;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ТЕСТ
        bmi_button = view.findViewById(R.id.bmi_button);
        bmi_button.setOnClickListener(new View.OnClickListener() {

            //тестовый
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BaseActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        return inflater.inflate(R.layout.fragment_convert, container, false);
    }
}

package com.example.calculator;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MoneyFragment extends Fragment {

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout investments_button = view.findViewById(R.id.investments_button);
        LinearLayout currency_button = view.findViewById(R.id.currency_button);
        LinearLayout credit_button = view.findViewById(R.id.credit_button);
        LinearLayout split_bill_button = view.findViewById(R.id.split_bill_button);

        View.OnClickListener listenerMoneyButton = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case R.id.investments_button:
                        callViewByKey("viewInvestments");
                        break;
                    case R.id.currency_button:
                        callViewByKey("viewCurrency");
                        break;
                    case R.id.credit_button:
                        callViewByKey("viewCredit");
                        break;
                    case R.id.split_bill_button:
                        callViewByKey("viewSplitBill");
                        break;
                }
            }
        };

        investments_button.setOnClickListener(listenerMoneyButton);
        currency_button.setOnClickListener(listenerMoneyButton);
        credit_button.setOnClickListener(listenerMoneyButton);
        split_bill_button.setOnClickListener(listenerMoneyButton);
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
        return inflater.inflate(R.layout.fragment_money, container, false);
    }
}

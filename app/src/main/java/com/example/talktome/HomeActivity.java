package com.example.talktome;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class HomeActivity extends AppCompatActivity {
    private ImageButton search, menu, chat, phoneNumbers, callHistory;
    private ChatViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        search = findViewById(R.id.search);
        menu = findViewById(R.id.menu);
        chat = findViewById(R.id.chat);
        phoneNumbers = findViewById(R.id.phonebook);
        callHistory = findViewById(R.id.phone);

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                FragmentChat fb = new FragmentChat();
                ft.replace(R.id.fragment, fb);
                ft.commit();
            }
        });

        phoneNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                FragmentPhoneNumbers fb = new FragmentPhoneNumbers();
                ft.replace(R.id.fragment, fb);
                ft.commit();
            }
        });

        callHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                FragmentCallHistory fb = new FragmentCallHistory();
                ft.replace(R.id.fragment, fb);
                ft.commit();
            }
        });

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        FragmentChat fb = new FragmentChat();
        ft.replace(R.id.fragment, fb);
        ft.commit();
    }

    public ChatViewModel getViewModel() {
        return viewModel;
    }
}

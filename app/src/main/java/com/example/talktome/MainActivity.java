package com.example.talktome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "app_prefs";
    public static final String IS_FIRST_LAUNCH_KEY = "is_first_launch";
    public static final String HAS_ACCOUNT_KEY = "has_account";

    private Button registrationButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        boolean isFirstLaunch = sharedPref.getBoolean(IS_FIRST_LAUNCH_KEY, true);
        boolean hasAccount = sharedPref.getBoolean(HAS_ACCOUNT_KEY, false);

        // Если это не первый запуск и аккаунт уже есть, переходим на HomeActivity
        if (!isFirstLaunch && hasAccount) {
            Intent homeIntend = new Intent(this, HomeActivity.class);
            startActivity(homeIntend);
            finish();
            return;
        }

        registrationButton = findViewById(R.id.buttonRegistration);
        loginButton = findViewById(R.id.buttonLogin);

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(MainActivity.this, EmailPasswordRegistrationActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast(getString(R.string.mistake));
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(MainActivity.this, EmailPasswordAuthActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast(getString(R.string.mistake));
                }
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
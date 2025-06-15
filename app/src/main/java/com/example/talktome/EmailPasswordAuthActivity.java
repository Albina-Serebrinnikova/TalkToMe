package com.example.talktome;

import static com.example.talktome.MainActivity.HAS_ACCOUNT_KEY;
import static com.example.talktome.MainActivity.IS_FIRST_LAUNCH_KEY;
import static com.example.talktome.MainActivity.PREFS_NAME;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class EmailPasswordAuthActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password_auth);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnSignIn);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(view -> singInWithEmailAndPassword());
    }

    private void singInWithEmailAndPassword() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!isInputValid(email, password)) return;

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast(getString(R.string.successful_login));
                        updatePrefs(false, true);
                        goToHomeScreen();
                    } else {
                        handleAuthErrors(task.getException());
                    }
                });
    }

    private boolean isInputValid(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            showToast(getString(R.string.enter_your_email_address));
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast(getString(R.string.enter_the_correct_email));
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            showToast(getString(R.string.enter_the_password));
            return false;
        }
        return true;
    }

    private void goToHomeScreen() {
        Intent intent = new Intent(EmailPasswordAuthActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void handleAuthErrors(Exception exception) {
        String errorMessage = exception != null ? exception.getMessage() : getString(R.string.unknown_error);

        switch (errorMessage) {
            case "The email address is badly formatted.":
                showToast(getString(R.string.incorrect_email_format));
                break;
            case "The password is invalid or the user does not have a password.":
                showToast(getString(R.string.invalid_password));
                break;
            case "There is no user record corresponding to this identifier. The user may have been deleted.":
                showToast(getString(R.string.the_user_was_not_found));
                break;
            default:
                showToast(getString(R.string.mistake) + errorMessage);
                Log.e("AuthError", getString(R.string.authentication_error) + errorMessage);
                break;
        }
    }

    public void updatePrefs(boolean firstLaunch, boolean hasAccount) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(IS_FIRST_LAUNCH_KEY, firstLaunch);
        editor.putBoolean(HAS_ACCOUNT_KEY, hasAccount);
        editor.apply();
    }
}

package com.example.talktome;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EmailPasswordRegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword, etPhoneNumber;
    private Button btnRegister;

    @SuppressLint("MissingInflatedId")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password_registration);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnRegister = findViewById(R.id.btnRegister);

        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(view -> registerUser());
    }


    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();

        if (!isInputValid(email, password, phoneNumber)) return;

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        String userId = user.getUid();
                        addUserToFirestore(userId, phoneNumber, email);
                    }
                    showToast(getString(R.string.registration_was_successful));
                    goToHomeScreen();
                } else {
                        handleAuthErrors(task.getException());
                    }
                });
    }

    private boolean isInputValid(String email, String password, String phoneNumber) {
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
        if (password.length() < 6) {
            showToast(getString(R.string.min_password_length));
            return false;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            showToast(getString(R.string.enter_phone_number));
            return false;
        }
        return true;
    }

    private void addUserToFirestore(String userId, String phoneNumber, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Создаём новый документ с уникальным ID
        Map<String, Object> user = new HashMap<>();
        user.put("userId", userId);
        user.put("phoneNumber", phoneNumber);
        user.put("email", email);

        // Добaвляем документ в коллекцию "users"
        db.collection("users")
                .document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", getString(R.string.document_successfully_with_id) + userId);
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", getString(R.string.error_add_document), e);
                });
    }

    private void goToHomeScreen() {
        Intent intent = new Intent(EmailPasswordRegistrationActivity.this, HomeActivity.class);
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
            case "The email address is already in use by another account.":
                showToast(getString(R.string.this_email_is_already_in_use));
                break;
            default:
                showToast(getString(R.string.mistake) + errorMessage);
                Log.e("AuthError", getString(R.string.authentication_error) + errorMessage);
                break;
        }
    }
}

package com.example.talktome;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FragmentPhoneNumbers extends Fragment {

    private static final int REQUEST_CODE_READ_CONTACTS = 1;
    private RecyclerView recyclerView;
    private PhoneNumberAdapter adapter;
    private List<Contact> contacts; // Список телефонных номеров
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_phonebook, container, false);

        db = FirebaseFirestore.getInstance();
        // Инициализация списка данных
        contacts = new ArrayList<>();

        // Инициализация RecyclerView
        recyclerView = v.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_CODE_READ_CONTACTS);
        } else {
            loadContacts();
        }
        // Загружаем данные из Firestore
        loadPhoneNumbersFromFirestore();

        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts();
            } else {
                Log.e("Permission", "Contacts permission denied");
                Toast.makeText(getActivity(), getString(R.string.app_needs_access_contacts), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadContacts() {
        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = null;

        try {
            cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    @SuppressLint("Range")
                    String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    @SuppressLint("Range")
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                    addUniqueContact(name, phoneNumber);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        setupAdapter();
    }

    private void loadPhoneNumbersFromFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e("FirebaseAuth", "User not authenticated.");
            return; // Возвращаемя, если пользователь не аутентифицирован
        }

        String userId = currentUser.getUid();
        CollectionReference phoneNumbersRef = db.collection("phoneNumbers");

        phoneNumbersRef.whereEqualTo("userId", userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String phoneNumber = document.getString("number");
                            String name = document.getString("name");

                            addUniqueContact(name, phoneNumber);
                        }

                        setupAdapter();
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }

    // Вспомогательный метод для предотвращения дублирования контактов
    private boolean addUniqueContact(String name, String phoneNumber) {
        for (Contact contact : contacts) {
            if (contact.getName().equalsIgnoreCase(name) &&
                    contact.getPhoneNumber().equals(phoneNumber)) { // ПРОВЕРКА УНИКАЛЬНОСТИ ПО НОМЕРУ ТЕЛЕФОНА И ИМЕНИ
                return false; // Контакт уже есть в списке
            }
        }
        contacts.add(new Contact(name, phoneNumber)); // Добавляем контакт в список
        return true;
    }

    private void setupAdapter() {
        if (adapter == null) {
            adapter = new PhoneNumberAdapter(contacts, getContext());
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }
}

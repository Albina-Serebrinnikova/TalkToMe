package com.example.talktome;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity  extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomAdapter customAdapter;
    private List<String> messageList;
    private EditText etTextMessage;
    private MyChat myChat;
    private String chatName, phoneNumber;
    private ImageButton imBtnSend, back, icon, menu, call, callVideo;
    private TextView name;


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        extractIntentData();
        initializeUIElement();
        setupListener();

        myChat = new MyChat(chatName, phoneNumber);
        messageList = new ArrayList<>();
        customAdapter = new CustomAdapter(messageList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(customAdapter);

        String chatId = myChat.hashCode() + "";
        myChat.listenForMessages(chatId, customAdapter);

        imBtnSend.setOnClickListener(v -> {
            String message = etTextMessage.getText().toString();
            if (!message.isEmpty()) {
                myChat.sendMessage(chatId, "senderId", message);
                messageList.add(message);
                etTextMessage.setText("");
                customAdapter.notifyItemInserted(messageList.size()-1);
                recyclerView.scrollToPosition(messageList.size()-1);
            }
        });
    }

    private void extractIntentData() {
        Intent intent = getIntent();
        chatName = intent.getStringExtra("chatnum");
        phoneNumber = intent.getStringExtra("phoneNumber");
        Toast.makeText(this, getString(R.string.chat_with) + chatName + getString(R.string.number_n) + phoneNumber, Toast.LENGTH_SHORT).show();
    }

    private void initializeUIElement() {
        recyclerView = findViewById(R.id.recyclerViewMessages);
        etTextMessage = findViewById(R.id.eTMessage);
        imBtnSend = findViewById(R.id.imBtnSend);
        back = findViewById(R.id.back);
        icon = findViewById(R.id.icon);
        menu = findViewById(R.id.menu);
        call = findViewById(R.id.call);
        callVideo = findViewById(R.id.callVideo);

        name = findViewById(R.id.name);
        name.setText(chatName);
    }

    private void setupListener() {
        call.setOnClickListener(view -> {
            if (isWhatsAppInstalled()) {
                makeVoiceCall(phoneNumber);
            } else {
                Toast.makeText(this, getString(R.string.WhatsApp_not_installed), Toast.LENGTH_SHORT).show();
            }
        });

        callVideo.setOnClickListener(view -> {
            if (isWhatsAppInstalled()) {
                makeVideoCall(phoneNumber);
            } else {
                Toast.makeText(this, getString(R.string.WhatsApp_not_installed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void makeVoiceCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://wa.me/"+phoneNumber));
        startActivity(intent);
    }

    private void makeVideoCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://wa.me/"+phoneNumber+"?text=VideoCall"));
        startActivity(intent);
    }

    private boolean isWhatsAppInstalled() {
        try {
            getPackageManager().getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {}

        try {
            getPackageManager().getPackageInfo("com.whatsapp.w4b", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {}

        return false;
    }
}

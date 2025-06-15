package com.example.talktome;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class PhoneNumberAdapter extends RecyclerView.Adapter<PhoneNumberAdapter.ViewHolder> {

    private final List<Contact> contacts;
    private final Context context;

    public PhoneNumberAdapter(List<Contact> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_phone_numbers, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        // Устанавливаем номер телефона в TextView
        holder.textPhoneNumber.setText(contact.getName() + " (" + contact.getPhoneNumber() + ")");

        // Проверяем, устанрвленно ли приложение TalkToMe
        if (isTalkToMeInstalled()) {
            holder.btn_write.setVisibility(View.VISIBLE);
            holder.btn_invite.setVisibility(View.GONE);

            // Проверяем, есть ли у контакта зарегестрированный аккаунт
            CheckUserRegistration(contact.getPhoneNumber(), isRegistered -> {
                if (isRegistered) {
                    // Обработчик нажатия на кнопку "Написать"
                    holder.btn_write.setOnClickListener(v -> {
                        Log.d("PhoneNumberAdapter", "Кнопка написать нажата для: " + contact);

                        // Здесь будет другой код !!!!!!!!!
//                        FragmentChat fragmentChat = FragmentChat.newInstance(contact.getPhoneNumber());
//                        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
//                        transaction.replace(R.id.fragment_container, fragmentChat);
//                        transaction.addToBackStack(null);
//                        transaction.commit();  // До этой строки !!!!!!!!!

                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("chatnum", contact.getName());
                        intent.putExtra("phoneNumber", contact.getPhoneNumber());
                        context.startActivity(intent);
                    });
                } else {
                    holder.btn_write.setVisibility(View.GONE);
                    holder.btn_invite.setVisibility(View.VISIBLE);
                    // Обработчик нажатия на кнопку "Пригласить"
                    holder.btn_invite.setOnClickListener(v -> sendInvite(contact.getPhoneNumber()));
                }
            });
        } else {
            holder.btn_write.setVisibility(View.GONE);
            holder.btn_invite.setVisibility(View.VISIBLE);
            // Обработчик нажатия на кнопку "Пригласить"
            holder.btn_invite.setOnClickListener(v -> sendInvite(contact.getPhoneNumber()));
        }
    }

    private void CheckUserRegistration(String phoneNumber, UserRegistrationCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("phoneNumber", phoneNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        boolean isRegistered = querySnapshot != null && !querySnapshot.isEmpty();
                        callback.onResult(isRegistered);
                        } else {
                            Log.w("FirestoreError", "Ошибка получения документов.", task.getException());
                            callback.onResult(false);
                        }
                });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    private boolean isTalkToMeInstalled() {
        try {
            context.getPackageManager().getPackageInfo("com.example.talktome", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void sendInvite(String phoneNumber) {
        String inviteMessage = context.getString(R.string.Hello_download_app_from_link);
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, inviteMessage);
        sendIntent.setPackage("com.whatsapp");  // Отправка через WhatsApp
        context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.share_via)));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textPhoneNumber;
        private Button btn_write, btn_invite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textPhoneNumber = itemView.findViewById(R.id.text_phone_number);
            btn_write = itemView.findViewById(R.id.btn_write);
            btn_invite = itemView.findViewById(R.id.btn_invite);
        }
    }

    public interface UserRegistrationCallback {
        void onResult(boolean isRegistered);
    }
}

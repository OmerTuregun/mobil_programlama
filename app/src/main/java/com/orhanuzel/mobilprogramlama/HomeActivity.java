package com.orhanuzel.mobilprogramlama;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Butonları tanımla
        Button btnProfile = findViewById(R.id.btn_profile);
        Button btnSettings = findViewById(R.id.btn_settings);
        Button btnLogout = findViewById(R.id.btn_logout);

        // Profil butonu
        btnProfile.setOnClickListener(v ->
                Toast.makeText(HomeActivity.this, "Profilim açılıyor...", Toast.LENGTH_SHORT).show()
        );

        // Ayarlar butonu
        btnSettings.setOnClickListener(v ->
                Toast.makeText(HomeActivity.this, "Ayarlar sayfası açılıyor...", Toast.LENGTH_SHORT).show()
        );

        // Çıkış yap butonu
        btnLogout.setOnClickListener(v -> {
            Toast.makeText(HomeActivity.this, "Çıkış yapılıyor...", Toast.LENGTH_SHORT).show();

            // Çıkış işlemi gerçekleştikten sonra Broadcast mesajı gönder
            sendBroadcastMessage();

            // Giriş ekranına dön
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Broadcast mesajı gönderme fonksiyonu
    private void sendBroadcastMessage() {
        // Gönderilecek mesajın tanımlandığı Intent
        Intent intent = new Intent();
        intent.setAction("com.orhanuzel.broadcast.LOGOUT");  // Özel action

        // İstediğiniz ekstra verileri ekleyebilirsiniz (isteğe bağlı)
        intent.putExtra("message", "Kullanıcı çıkış yaptı!");

        // Broadcast mesajını gönder
        sendBroadcast(intent);
    }
}

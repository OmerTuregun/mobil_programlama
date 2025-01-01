package com.orhanuzel.mobilprogramlama;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login_button);
        TextView registerLink = findViewById(R.id.register_link);

        // Kayıt ekranına yönlendirme
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // Giriş işlemi ve arka plan işleminin tetiklenmesi
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString();
                String pass = password.getText().toString();

                if (!user.isEmpty() && !pass.isEmpty()) {
                    // Kullanıcıyı ana sayfaya yönlendir
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);

                    // WorkManager ile arka plan işini başlat
                    OneTimeWorkRequest syncWorkRequest =
                            new OneTimeWorkRequest.Builder(SyncWorker.class).build();
                    WorkManager.getInstance(MainActivity.this).enqueue(syncWorkRequest);

                } else {
                    Toast.makeText(MainActivity.this,
                            "Lütfen kullanıcı adı ve şifre girin!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

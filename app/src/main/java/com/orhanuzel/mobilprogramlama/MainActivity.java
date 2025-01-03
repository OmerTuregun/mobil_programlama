package com.orhanuzel.mobilprogramlama;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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


import com.orhanuzel.mobilprogramlama.api.AuthApi;
import com.orhanuzel.mobilprogramlama.api.RetrofitClient;
import com.orhanuzel.mobilprogramlama.api.AuthRequest;
import com.orhanuzel.mobilprogramlama.api.AuthResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 2;

    private BluetoothAdapter bluetoothAdapter; // Bluetooth Adapter
    private BluetoothLeScanner bluetoothLeScanner; // BLE Scanner
    private SharedPreferences sharedPreferences;

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

                    // Broadcast mesajı gönderme
                    sendBroadcastMessage();  // Giriş işlemi tamamlandıktan sonra mesaj gönder

                } else {
                    Toast.makeText(MainActivity.this,
                            "Lütfen kullanıcı adı ve şifre girin!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // SharedPreferences başlatma
        sharedPreferences = getSharedPreferences("AuthPrefs", MODE_PRIVATE);
        findViewById(R.id.loginButton).setOnClickListener(v -> authenticateUser("example@example.com", "password123"));
        // Butonlara tıklama işlevi ekleyin
        findViewById(R.id.startScanButton).setOnClickListener(v -> startScan());
        findViewById(R.id.stopScanButton).setOnClickListener(v -> stopScan());
        findViewById(R.id.checkNfcButton).setOnClickListener(v -> checkNfcSupport());
        // Bluetooth desteğini kontrol et
        checkBluetoothSupport();
        // Wi-Fi Aç-Kapat işlevi kontrolü
        findViewById(R.id.toggleWifiButton).setOnClickListener(v -> toggleWifi());

        // Kullanıcı oturum kontrolü
        String token = sharedPreferences.getString("authToken", null);
        if (token != null) {
            Log.d("AUTH", "Kullanıcı zaten oturum açmış: Token -> " + token);
        } else {
            Log.d("AUTH", "Oturum açmamış kullanıcı.");
        }
    }

    /**
     * Kullanıcı girişini doğrular ve token alır.
     */
    private void authenticateUser(String email, String password) {
        AuthApi authApi = RetrofitClient.getInstance().create(AuthApi.class);
        AuthRequest authRequest = new AuthRequest(email, password);

        authApi.login(authRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();

                    // Token SharedPreferences'a kaydedilir
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("authToken", token);
                    editor.apply();

                    Log.d("AUTH", "Başarılı giriş! Token: " + token);
                    Toast.makeText(MainActivity.this, "Başarıyla giriş yapıldı.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("AUTH", "Giriş başarısız: " + response.code());
                    Toast.makeText(MainActivity.this, "Giriş başarısız!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e("AUTH", "Hata: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Giriş sırasında hata oluştu.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void checkNfcSupport() {
        android.nfc.NfcAdapter nfcAdapter = android.nfc.NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Log.e("NFC", "Cihaz NFC desteklemiyor.");
            Toast.makeText(this, "Cihaz NFC desteklemiyor.", Toast.LENGTH_SHORT).show();
        } else if (!nfcAdapter.isEnabled()) {
            Log.d("NFC", "NFC devre dışı.");
            Toast.makeText(this, "NFC devre dışı. Lütfen etkinleştirin.", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("NFC", "NFC etkin.");
            Toast.makeText(this, "NFC etkin.", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Çalışma zamanı izinlerini kontrol eder ve gerekirse kullanıcıdan izin ister.
     */
    private void checkBluetoothPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // İzin gerekli, kullanıcıdan iste
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.BLUETOOTH_CONNECT,
                            android.Manifest.permission.BLUETOOTH_SCAN,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    REQUEST_BLUETOOTH_PERMISSIONS);
        } else {
            // İzinler verilmiş, Bluetooth'u kontrol et
            checkBluetoothSupportInternal();
        }
    }

    private void checkWifiStatus() {
        android.net.wifi.WifiManager wifiManager = (android.net.wifi.WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled()) {
            Log.d("Wi-Fi", "Wi-Fi açık.");
            Toast.makeText(this, "Wi-Fi açık", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("Wi-Fi", "Wi-Fi kapalı.");
            Toast.makeText(this, "Wi-Fi kapalı", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleWifi() {
        android.net.wifi.WifiManager wifiManager = (android.net.wifi.WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled()) {
            // Wi-Fi'yi kapat
            wifiManager.setWifiEnabled(false);
            Log.d("Wi-Fi", "Wi-Fi kapatıldı.");
            Toast.makeText(this, "Wi-Fi kapatıldı", Toast.LENGTH_SHORT).show();
        } else {
            // Wi-Fi'yi aç
            wifiManager.setWifiEnabled(true);
            Log.d("Wi-Fi", "Wi-Fi açıldı.");
            Toast.makeText(this, "Wi-Fi açıldı.", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Bluetooth desteğini ve durumunu kontrol eder.
     */
    private void checkBluetoothSupport() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            // Cihaz Bluetooth desteklemiyor
            Log.e("BLE", "Cihaz Bluetooth desteklemiyor.");
            return;
        }

        // Çalışma zamanı izinlerini kontrol et
        checkBluetoothPermissions();
    }

    /**
     * Bluetooth durumunu kontrol eder ve kapalıysa etkinleştirme isteği gönderir.
     */
    private void checkBluetoothSupportInternal() {
        // Çalışma zamanı izni kontrolü
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("BLE", "BLUETOOTH_CONNECT izni verilmemiş. İzin istemeniz gerekiyor.");
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.BLUETOOTH_CONNECT},
                    REQUEST_BLUETOOTH_PERMISSIONS);
            return;
        }

        // Bluetooth açık değilse etkinleştirme isteği gönder
        if (!bluetoothAdapter.isEnabled()) {
            Log.d("BLE", "Bluetooth etkin değil, etkinleştiriliyor...");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Log.d("BLE", "Bluetooth etkin.");
            startScan(); // BLE taramayı başlat
        }
    }

    /**
     * BLE tarama işlemini başlatır.
     */
    private void startScan() {
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        if (bluetoothLeScanner != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothLeScanner.startScan(leScanCallback);
            Log.d("BLE", "BLE tarama başlatıldı.");
            Toast.makeText(this, "BLE tarama başlatıldı", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("BLE", "BluetoothLeScanner alınamadı.");
        }
    }

    /**
     * BLE tarama işlemini durdurur.
     */
    private void stopScan() {
        if (bluetoothLeScanner != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothLeScanner.stopScan(leScanCallback);
            Log.d("BLE", "BLE tarama durduruldu.");
            Toast.makeText(this, "BLE tarama durduruldu", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Tarama sonuçlarını işlemek için ScanCallback.
     */
    private final ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                return;
            }
            Log.d("BLE", "Bulunan cihaz: " + result.getDevice().getName() + " - " + result.getDevice().getAddress());
        }
    };

    /**
     * Kullanıcının izin sonuçlarını yönetir.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("BLE", "Bluetooth izinleri verildi.");
                checkBluetoothSupportInternal();
            } else {
                Log.e("BLE", "Bluetooth izinleri reddedildi.");
            }
        }
    }


    // Broadcast mesajı gönderme fonksiyonu
    private void sendBroadcastMessage() {
        // Gönderilecek mesajın tanımlandığı Intent
        Intent intent = new Intent();
        intent.setAction("com.orhanuzel.broadcast.LOGIN");  // Özel action

        // İstediğiniz ekstra verileri ekleyebilirsiniz (isteğe bağlı)
        intent.putExtra("message", "Kullanıcı giriş yaptı!");

        // Broadcast mesajını gönder
        sendBroadcast(intent);
    }
}

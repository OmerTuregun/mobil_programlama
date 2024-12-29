package com.orhanuzel.mobilprogramlama;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 2;

    private BluetoothAdapter bluetoothAdapter; // Bluetooth Adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mevcut kodlarınızı çalıştırın
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Bluetooth desteğini kontrol et
        checkBluetoothSupport();
    }

    /**
     * Çalışma zamanı izinlerini kontrol eder ve gerekirse kullanıcıdan izin ister.
     */
    private void checkBluetoothPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            // İzin gerekli, kullanıcıdan iste
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.BLUETOOTH_CONNECT,
                            android.Manifest.permission.BLUETOOTH_SCAN
                    },
                    REQUEST_BLUETOOTH_PERMISSIONS);
        } else {
            // İzinler verilmiş, Bluetooth'u kontrol et
            checkBluetoothSupportInternal();
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
        }
    }


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
}

package com.orhanuzel.mobilprogramlama;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
    private BluetoothLeScanner bluetoothLeScanner; // BLE Scanner

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


        // Butonlara tıklama işlevi ekleyin
        findViewById(R.id.startScanButton).setOnClickListener(v -> startScan());
        findViewById(R.id.stopScanButton).setOnClickListener(v -> stopScan());
        // Bluetooth desteğini kontrol et
        checkBluetoothSupport();
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
}

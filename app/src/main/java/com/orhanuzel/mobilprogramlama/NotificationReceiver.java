package com.orhanuzel.mobilprogramlama;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Broadcast'tan alınan mesaj
        String message = intent.getStringExtra("message");

        // NotificationManager'a erişim
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Kanal ID'si
        String channelId = "broadcast_channel";

        // Android 8.0 (API 26) ve sonrasında kanal oluşturma
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Broadcast Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Bildirim oluşturma
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)  // Küçük simge
                .setContentTitle("Anlık Bildirim")  // Bildirim başlığı
                .setContentText(message)  // Gönderilen mesaj
                .setPriority(NotificationCompat.PRIORITY_HIGH);  // Yüksek öncelikli bildirim

        // Bildirimi göndermek
        notificationManager.notify(1, builder.build());
    }
}

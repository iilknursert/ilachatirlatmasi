package com.example.ilachatirlatmasi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationManagerCompat;

public class StopAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Alarmı durdur
        AlarmReceiver.stopAlarm();

        // Bildirimi temizle
        int notificationId = intent.getIntExtra("notificationId", -1); // Bildirim kimliği alınıyor.
        if (notificationId != -1) { // Eğer geçerli bir kimlik varsa
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context); // Bildirim yöneticisi oluşturuluyor.
            notificationManager.cancel(notificationId); // Belirtilen kimliğe sahip bildirim siliniyor.
        }
    }
}

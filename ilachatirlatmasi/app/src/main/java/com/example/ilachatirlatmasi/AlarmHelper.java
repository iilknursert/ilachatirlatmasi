package com.example.ilachatirlatmasi;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class AlarmHelper {
    public static void setAlarm(Context context, Calendar calendar, String ilacIsmi, int requestCode) {
        // AlarmManager, sistemdeki alarmları yönetmek için kullanılır.
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Alarm tetiklendiğinde çalışacak olan BroadcastReceiver (AlarmReceiver).
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("ilacIsmi", ilacIsmi); // AlarmReceiver'a ilaç ismi iletilir.

        // PendingIntent, AlarmManager'ın tetiklenecek olan olayı temsil etmesi için kullanılır.
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode, // Bu alarm için benzersiz bir kod.
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            // Alarmı belirlenen tarih ve saatte çalışacak şekilde ayarla.
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Log.d("AlarmHelper", "Alarm kuruldu: " + calendar.getTime());
        } else {
            // AlarmManager bulunamazsa hata günlüğü yaz.
            Log.d("AlarmHelper", "AlarmManager bulunamadı.");
        }
    }
}

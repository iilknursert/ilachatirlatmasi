package com.example.ilachatirlatmasi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.app.PendingIntent;

public class AlarmReceiver extends BroadcastReceiver {
    // MediaPlayer, alarm sesi çalmak için kullanılır.
    private static MediaPlayer mediaPlayer;

    // Alarm tetiklendiğinde çalıştırılan yöntem.
    @Override
    public void onReceive(Context context, Intent intent) {
        // İlaç ismini intent'ten al.
        String ilacIsmi = intent.getStringExtra("ilacIsmi");

        // Bildirim kanalı için bir ID belirlenir.
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "ILAC_HATIRLATICI";
        // Android 8.0 (Oreo) ve üzeri sürümlerde bildirim kanalı oluşturulur.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "İlaç Hatırlatıcı Kanalı",
                    NotificationManager.IMPORTANCE_HIGH); // Yüksek öncelikli bir kanal oluşturulur.
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel); // Kanal bildirim yöneticisine eklenir.
            }
        }

        // "Alarmı Kapat" butonu için bir Intent oluşturulur.
        Intent stopAlarmIntent = new Intent(context, StopAlarmReceiver.class);
        stopAlarmIntent.putExtra("notificationId", 1);  // Bildirim ID'si eklenir.
        PendingIntent stopAlarmPendingIntent = PendingIntent.getBroadcast(
                context, 0, stopAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Bildirimi yapılandırır.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_alarm)  // Küçük ikon ayarlanır.
                .setContentTitle("İlaç Hatırlatma") // Bildirimin başlığı.
                .setContentText("Şimdi " + ilacIsmi + " almanız gerekiyor!")  // Bildirimin açıklama metni.
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Yüksek öncelikli bir bildirim.
                .setAutoCancel(true)  // Bildirim, tıklandığında otomatik olarak kaldırılır.
                .addAction(R.drawable.ic_close, "Alarmı Kapat", stopAlarmPendingIntent); // Kapatma butonu eklenir.

        // Bildirimi göster.
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build()); // Bildirim yöneticisi aracılığıyla bildirimi yayınla.
        }

        // Alarm sesi çalma işlemi.
        try {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM); // Varsayılan alarm sesi alınır.
            if (alarmSound == null) {
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // Alarm sesi bulunamazsa varsayılan bildirim sesi kullanılır.
            }


            // MediaPlayer oluştur ve sesi çal.
            mediaPlayer = MediaPlayer.create(context, alarmSound);
            if (mediaPlayer != null) {
                mediaPlayer.start(); // Alarm sesi başlatılır.

                // Alarmı otomatik olarak durdurmak için 30 saniye süre ayarlanır.
                mediaPlayer.setOnCompletionListener(mp -> mediaPlayer.release());  // Sesi tamamladığında kaynak serbest bırakılır.
                new Handler().postDelayed(() -> {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop(); // Sesi durdur.
                        mediaPlayer.release(); // Kaynağı serbest bırak.
                        mediaPlayer = null; // MediaPlayer'ı sıfırla.
                    }
                }, 30000); // 30 saniye
            }

        } catch (Exception e) {
            e.printStackTrace(); // Olası hatalar yazdırılır.
        }
    }

    // Alarmı manuel olarak durdurmak için bir yöntem.
    public static void stopAlarm() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop(); // Çalan sesi durdur.
            }
            mediaPlayer.release(); // Kaynağı serbest bırak.
            mediaPlayer = null; // MediaPlayer'ı sıfırla.
        }
    }
}

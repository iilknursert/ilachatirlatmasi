package com.example.ilachatirlatmasi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  // Üst sınıfın `onCreate` metodunu çağırır.
        EdgeToEdge.enable(this);  // Uygulamayı tam ekran deneyimi için Edge-to-Edge modunda çalıştırır.
        setContentView(R.layout.activity_main);  // Layout dosyasını bağlar ve UI'yi oluşturur.

        // 1 saniye bekledikten sonra `ilacekle` adlı aktiviteye geçiş yapar.
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, ilacekle.class);  // `ilacekle` sayfasına geçiş için bir `Intent` oluşturur.
            startActivity(intent);  // Yeni aktiviteyi başlatır.
            finish(); // `MainActivity`'yi sonlandırır, geri düğmesiyle geri dönülmesini engeller.
        }, 1000); // 1000 milisaniye (1 saniye) sonra çalışır.

        // Pencere kenarlıklarını ayarlamak için `WindowInsets` kullanır.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());  // Sistem çubuğu kenarlıklarını alır.
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);  // UI'ye bu kenarlıkları uygular.
            return insets;  // Kenarlıkları geri döndürür.
        });
    }
}

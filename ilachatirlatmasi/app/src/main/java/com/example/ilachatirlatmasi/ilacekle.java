package com.example.ilachatirlatmasi;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ilacekle extends AppCompatActivity {

    // Dialog, butonlar, metin alanları ve görüntüler için değişkenler tanımlanır.
    private Dialog ilacekledialog;
    private Button btnclose, btnilacgir, btnSelectDate, btnCreateTimePickers;
    private EditText editilacismi, editDozSayisi;
    private ImageView imgClose, imageOk;
    private LinearLayout timePickerContainer;
    private TextView textView;

    private String selectedDate = "";  // Kullanıcı tarafından seçilen tarih aralığı
    private List<String> selectedTimes = new ArrayList<>();  // Kullanıcı tarafından seçilen saatler

    private RecyclerView recyclerView;  // İlaç listesini göstermek için RecyclerView
    private IlacAdapter ilacAdapter;  // RecyclerView için adaptör
    private List<Ilac> ilacList = new ArrayList<>();  // İlaç bilgilerini tutan liste

    private IlacDatabaseHelper databaseHelper; // Veritabanı yardımcı sınıfı

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);  // Tam ekran deneyimi için Edge-to-Edge modu etkinleştirilir.
        setContentView(R.layout.activity_ilacekle);

        databaseHelper = new IlacDatabaseHelper(this); // Veritabanı yardımcı sınıfı başlatılır.
        recyclerView = findViewById(R.id.recyclerViewIlaclar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // RecyclerView için düzenleme tanımlanır.

        imageOk = findViewById(R.id.imageok);  // Boş liste görüntüsü
        textView = findViewById(R.id.textView);  // Bilgilendirme metni

        // IlacAdapter'ı databaseHelper ile başlat
        ilacAdapter = new IlacAdapter(ilacList, databaseHelper); // İlaç adaptörü başlatılır.
        recyclerView.setAdapter(ilacAdapter);

        loadIlaclarFromDatabase(); // Veritabanından ilaçları yükler
        updateVisibility();  // UI görünürlüğünü günceller.


        // Hassas alarm iznini kontrol et
        checkExactAlarmPermission();

        // Sistem barları için pencere içi yerleşim dinleyicisi ayarlanır.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Hassas alarm iznini kontrol eder ve gerekirse kullanıcıyı ayarlara yönlendirir.
    private void checkExactAlarmPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent); // Kullanıcıyı hassas alarm izni ayarına yönlendirir.
                Toast.makeText(this, "Hassas alarm izni gerekli!", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Kullanıcı özel bir ilaç giriş diyaloğu görüntülemek istediğinde çağrılır.
    public void showCustomDialog(View v) {
        showilacismigirinDialog();
    }

    // İlaç giriş diyaloğunu oluşturur ve gösterir.
    private void showilacismigirinDialog() {
        ilacekledialog = new Dialog(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(ilacekledialog.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        ilacekledialog.setCancelable(false);
        ilacekledialog.setContentView(R.layout.customdialog_ilacekle);

        // Diyalog içindeki bileşenler tanımlanır.
        btnclose = ilacekledialog.findViewById(R.id.customdialog_ilacekle_btnclose);
        btnilacgir = ilacekledialog.findViewById(R.id.customdialog_ilacekle_btnilacgir);
        imgClose = ilacekledialog.findViewById(R.id.customdialog_ilacekle_imageViewClose);
        editilacismi = ilacekledialog.findViewById(R.id.customdialog_ilacekle_editTextilacismi);
        btnSelectDate = ilacekledialog.findViewById(R.id.customdialog_ilacekle_btnSelectDate);
        btnCreateTimePickers = ilacekledialog.findViewById(R.id.customdialog_ilacekle_btnCreateTimePickers);
        editDozSayisi = ilacekledialog.findViewById(R.id.customdialog_ilacekle_editDozSayisi);
        timePickerContainer = ilacekledialog.findViewById(R.id.customdialog_ilacekle_timeContainer);

        btnSelectDate.setOnClickListener(v -> showMultiDatePicker());  // Tarih aralığı seçici açılır.

        btnclose.setOnClickListener(v -> ilacekledialog.dismiss());  // Diyalog kapatılır.
        imgClose.setOnClickListener(v -> ilacekledialog.dismiss());  // Görüntü butonuyla diyalog kapatılır.

        btnCreateTimePickers.setOnClickListener(v -> createTimePickers());  // Saat seçiciler oluşturulur.

        // İlaç bilgisi eklemek için doğrulama yapılır ve işlem gerçekleştirilir.
        btnilacgir.setOnClickListener(v -> {
            if (TextUtils.isEmpty(editilacismi.getText().toString())) {
                Toast.makeText(this, "Lütfen ilaç ismini girin!", Toast.LENGTH_SHORT).show();
            } else if (selectedTimes.isEmpty()) {
                Toast.makeText(this, "Lütfen saat seçin!", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(selectedDate)) {
                Toast.makeText(this, "Lütfen tarih aralığı seçin!", Toast.LENGTH_SHORT).show();
            } else {
                Ilac yeniIlac = new Ilac(
                        editilacismi.getText().toString(),
                        selectedDate,
                        new ArrayList<>(selectedTimes)
                );

                databaseHelper.addIlac(yeniIlac); // Veritabanına eklenir.
                ilacList.add(yeniIlac); // Listeye eklenir.
                ilacAdapter.notifyDataSetChanged();  // RecyclerView güncellenir.

                // Tarih aralığı işlenir ve alarm kurulur.
                String[] dateParts = selectedDate.split(" - ");
                if (dateParts.length == 2) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        Date startDate = sdf.parse(dateParts[0]);
                        Date endDate = sdf.parse(dateParts[1]);

                        if (startDate != null && endDate != null) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(startDate);

                            while (!calendar.getTime().after(endDate)) {
                                String currentDate = sdf.format(calendar.getTime());

                                for (String time : selectedTimes) {
                                    try {
                                        Calendar alarmCalendar = Calendar.getInstance();
                                        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
                                        String tarihSaat = currentDate + " " + time;
                                        alarmCalendar.setTime(dateTimeFormat.parse(tarihSaat));

                                        // Geçmiş zaman kontrolü yapılır.
                                        if (alarmCalendar.getTimeInMillis() > System.currentTimeMillis()) {
                                            AlarmHelper.setAlarm(this, alarmCalendar, yeniIlac.getIsim(), (int) alarmCalendar.getTimeInMillis());
                                            Log.d("AlarmKurma", "Alarm kuruldu: " + tarihSaat);
                                        } else {
                                            Log.d("AlarmKurma", "Geçmiş bir alarm kurulmuyor: " + tarihSaat);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.e("AlarmKurma", "Alarm kurma sırasında hata: " + e.getMessage());
                                    }
                                }

                                // Bir sonraki güne geçilir.
                                calendar.add(Calendar.DAY_OF_MONTH, 1);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("AlarmKurma", "Tarih aralığı işlenemedi: " + e.getMessage());
                    }
                }

                Toast.makeText(this, "İlaç kaydedildi ve alarm kuruldu!", Toast.LENGTH_SHORT).show();
                ilacekledialog.dismiss();  // Diyalog kapatılır.
                updateVisibility();  // UI güncellenir.
            }
        });

        ilacekledialog.getWindow().setAttributes(params);
        ilacekledialog.show();  // Diyalog gösterilir.
    }

    // Saat seçiciler oluşturulur ve UI'ye eklenir.
    private void createTimePickers() {
        String dozSayisiStr = editDozSayisi.getText().toString();
        if (TextUtils.isEmpty(dozSayisiStr)) {
            Toast.makeText(this, "Lütfen doz sayısını girin!", Toast.LENGTH_SHORT).show();
            return;
        }

        int dozSayisi = Integer.parseInt(dozSayisiStr);

        timePickerContainer.removeAllViews();  // Önceki saat seçiciler temizlenir.
        selectedTimes.clear();  // Seçilen saatler listesi temizlenir.

        for (int i = 0; i < dozSayisi; i++) {
            int index = i + 1;

            TextView label = new TextView(this);  // Saat etiketi
            label.setText("Saat " + index + ":");
            label.setTextSize(16);
            label.setPadding(10, 20, 0, 10);

            Button timePickerButton = new Button(this);   // Saat seçici butonu
            timePickerButton.setText("Saat Seç");
            timePickerButton.setTag(index);

            timePickerButton.setOnClickListener(v -> {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(ilacekle.this,
                        (view, hourOfDay, minute1) -> {
                            String selectedTime = String.format("%02d:%02d", hourOfDay, minute1);
                            selectedTimes.add(selectedTime);  // Seçilen saat listeye eklenir.
                            timePickerButton.setText(selectedTime);
                        }, hour, minute, true);
                timePickerDialog.show();  // Saat seçici gösterilir.
            });

            timePickerContainer.addView(label);
            timePickerContainer.addView(timePickerButton);
        }
    }

    // Tarih aralığı seçici açılır.
    private void showMultiDatePicker() {
        MaterialDatePicker<Pair<Long, Long>> dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Tarih Aralığı Seç")
                .build();

        dateRangePicker.addOnPositiveButtonClickListener(selection -> {
            if (selection != null) {
                long startDateMillis = selection.first;
                long endDateMillis = selection.second;

                // Başlangıç ve bitiş tarihleri formatlanır.
                String formattedStartDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(startDateMillis));
                String formattedEndDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(endDateMillis));

                // Seçilen tarih aralığı kullanıcıya gösterilir.
                selectedDate = formattedStartDate + " - " + formattedEndDate;
                btnSelectDate.setText("" + selectedDate);
            }
        });

        dateRangePicker.show(getSupportFragmentManager(), "DATE_RANGE_PICKER");
    }

    // Veritabanından ilaçları yükler ve RecyclerView'i günceller.
    private void loadIlaclarFromDatabase() {
        ilacList.clear();
        ilacList.addAll(databaseHelper.getAllIlaclar());
        ilacAdapter.notifyDataSetChanged();
    }

    // UI görünürlüğünü günceller.
    private void updateVisibility() {
        if (ilacList.isEmpty()) {
            imageOk.setVisibility(View.VISIBLE);  // Boş liste görüntüsü görünür yapılır.
            textView.setVisibility(View.VISIBLE);  // Bilgilendirme metni görünür yapılır.
            recyclerView.setVisibility(View.GONE);  // RecyclerView gizlenir.
        } else {
            imageOk.setVisibility(View.GONE);  // Boş liste görüntüsü gizlenir.
            textView.setVisibility(View.GONE);  // Bilgilendirme metni gizlenir.
            recyclerView.setVisibility(View.VISIBLE);  // RecyclerView görünür yapılır.
        }
    }
}

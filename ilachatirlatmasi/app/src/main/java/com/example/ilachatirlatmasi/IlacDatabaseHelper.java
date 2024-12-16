package com.example.ilachatirlatmasi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class IlacDatabaseHelper extends SQLiteOpenHelper {

    // Veritabanı adı ve sürümü
    private static final String DATABASE_NAME = "ilaclar.db"; // Veritabanı dosyasının adı.
    private static final int DATABASE_VERSION = 1; // Veritabanı sürümü.

    // Tablo ve sütun adları
    private static final String TABLE_ILACLAR = "ilaclar";  // İlaçlar tablosunun adı.
    private static final String COLUMN_ID = "id";  // Her ilaç için benzersiz bir kimlik.
    private static final String COLUMN_ILAC_ISMI = "isim";  // İlaç ismi.
    private static final String COLUMN_TARIH = "tarih";  // İlaç alım tarihleri.
    private static final String COLUMN_SAATLER = "saatler";  // İlaç alım saatleri (virgülle ayrılmış).

    // Constructor: Veritabanı bağlantısı oluşturulur.
    public IlacDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // Veritabanı oluşturulduğunda çalışır.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ILACLAR_TABLE = "CREATE TABLE " + TABLE_ILACLAR + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "  // Otomatik artan birincil anahtar.
                + COLUMN_ILAC_ISMI + " TEXT, "  // İlaç ismi sütunu.
                + COLUMN_TARIH + " TEXT, "  // Tarih sütunu.
                + COLUMN_SAATLER + " TEXT)";  // Saatler sütunu.
        db.execSQL(CREATE_ILACLAR_TABLE);  // SQL sorgusunu çalıştırır.
    }

    // Veritabanı yükseltildiğinde çalışır.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eski tabloyu silip yeniden oluşturur.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ILACLAR);
        onCreate(db);
    }

    // Yeni bir ilaç kaydı ekler.
    public void addIlac(Ilac ilac) {
        SQLiteDatabase db = this.getWritableDatabase();  // Yazılabilir veritabanı bağlantısı.
        ContentValues values = new ContentValues();
        values.put(COLUMN_ILAC_ISMI, ilac.getIsim()); // İlaç ismini ekler.
        values.put(COLUMN_TARIH, ilac.getTarih());  // Tarih bilgisi ekler.
        values.put(COLUMN_SAATLER, String.join(",", ilac.getSaatler())); // Saatleri virgülle ayrılmış şekilde ekler.

        db.insert(TABLE_ILACLAR, null, values);  // Veriyi tabloya ekler.
        db.close();  // Veritabanı bağlantısını kapatır.
    }

    // Tüm ilaçları veritabanından alır.
    public List<Ilac> getAllIlaclar() {
        List<Ilac> ilaclar = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase(); // Okunabilir veritabanı bağlantısı.
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ILACLAR, null);  // Tüm ilaçları sorgular.

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));  // Kimlik alır.
                String isim = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ILAC_ISMI)); // İsim alır.
                String tarih = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TARIH)); // Tarih alır.
                String saatlerStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SAATLER)); // Saatler alır.
                List<String> saatler = List.of(saatlerStr.split(",")); // Saatler listesini oluşturur.

                ilaclar.add(new Ilac(id, isim, tarih, saatler)); // Listeye yeni bir ilaç nesnesi ekler.
            } while (cursor.moveToNext());
        }

        cursor.close(); // Cursor'u kapatır.
        db.close(); // Veritabanı bağlantısını kapatır.
        return ilaclar; // Tüm ilaçların listesi döndürülür.
    }

    // Belirli bir ilaç kaydını siler.
    public void deleteIlac(int id) {
        SQLiteDatabase db = this.getWritableDatabase();  // Yazılabilir veritabanı bağlantısı.
        db.delete(TABLE_ILACLAR, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});  // Belirtilen id'ye sahip kaydı siler.
        db.close(); // Veritabanı bağlantısını kapatır.
    }

    // Mevcut bir ilaç kaydını günceller.
    public void updateIlac(Ilac ilac) {
        SQLiteDatabase db = this.getWritableDatabase();  // Yazılabilir veritabanı bağlantısı.
        ContentValues values = new ContentValues();
        values.put(COLUMN_ILAC_ISMI, ilac.getIsim()); // Yeni ilaç ismini ayarlar.
        values.put(COLUMN_TARIH, ilac.getTarih()); // Yeni tarih ayarlar.
        values.put(COLUMN_SAATLER, String.join(",", ilac.getSaatler())); // Yeni saatleri ayarlar.

        db.update(TABLE_ILACLAR, values, COLUMN_ID + " = ?", new String[]{String.valueOf(ilac.getId())}); // Veriyi günceller.
        db.close(); // Veritabanı bağlantısını kapatır.
    }

    // Belirli bir id'ye sahip ilaç kaydını alır.
    public Ilac getIlacById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();  // Okunabilir veritabanı bağlantısı.
        Cursor cursor = db.query(TABLE_ILACLAR, null, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null); // İlgili id'yi sorgular.

        if (cursor != null && cursor.moveToFirst()) {
            String isim = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ILAC_ISMI)); // İsim alır.
            String tarih = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TARIH)); // Tarih alır.
            String saatlerStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SAATLER)); // Saatler alır.
            List<String> saatler = List.of(saatlerStr.split(",")); // Saatler listesini oluşturur.
            cursor.close(); // Cursor'u kapatır.
            db.close(); // Veritabanı bağlantısını kapatır.
            return new Ilac(id, isim, tarih, saatler); // Bulunan ilaç döndürülür.
        }

        if (cursor != null) cursor.close(); // Cursor varsa kapatılır.
        db.close(); // Veritabanı bağlantısı kapatılır.
        return null; // Hiçbir şey bulunamazsa `null` döndürülür.
    }
}

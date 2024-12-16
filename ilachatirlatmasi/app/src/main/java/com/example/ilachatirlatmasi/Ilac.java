package com.example.ilachatirlatmasi;

// İlaç bilgilerini temsil eden bir model sınıfı tanımlanıyor.
import java.util.List;

public class Ilac {
    private int id; // Veritabanında ilaca özel benzersiz kimlik.
    private String isim; // İlaç adı.
    private String tarih; // İlaç kullanım tarihi veya tarih aralığı.
    private List<String> saatler; // İlaç alım saatlerinin listesi.

    // Constructor (id ile birlikte)
    public Ilac(int id, String isim, String tarih, List<String> saatler) {
        // Bu constructor, ilacın veritabanında zaten var olduğu durumlarda kullanılır.
        // Veritabanındaki id'yi de içeren bir Ilac nesnesi oluşturur.
        this.id = id;
        this.isim = isim;
        this.tarih = tarih;
        this.saatler = saatler;
    }

    // Constructor (id olmadan)
    public Ilac(String isim, String tarih, List<String> saatler) {
        // Bu constructor, yeni bir ilacın oluşturulması için kullanılır.
        // id, veritabanına eklenme sırasında otomatik olarak atanır.
        this.isim = isim;
        this.tarih = tarih;
        this.saatler = saatler;
    }

    // Getter ve Setter metotları: Sınıf içindeki özel değişkenlere erişim ve güncelleme sağlar.

    // `id` için getter ve setter
    public int getId() {

        return id; // İlaç id'sini döndürür.
    }

    public void setId(int id) {

        this.id = id;  // İlaç id'sini günceller.
    }


    // `isim` için getter ve setter
    public String getIsim() {

        return isim;// İlaç ismini döndürür.
    }

    public void setIsim(String isim) {

        this.isim = isim; // İlaç ismini günceller.
    }


    // `tarih` için getter ve setter
    public String getTarih() {

        return tarih; // İlaç tarihini döndürür.
    }

    public void setTarih(String tarih) {

        this.tarih = tarih; // İlaç tarihini günceller.
    }

    // `saatler` için getter ve setter
    public List<String> getSaatler() {  // İlaç saatlerini döndürür.

        return saatler;
    }

    public void setSaatler(List<String> saatler) {

        this.saatler = saatler;  // İlaç saatlerini günceller.
    }
}

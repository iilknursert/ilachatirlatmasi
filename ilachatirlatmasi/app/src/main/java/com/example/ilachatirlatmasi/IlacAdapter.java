package com.example.ilachatirlatmasi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IlacAdapter extends RecyclerView.Adapter<IlacAdapter.IlacViewHolder> {

    // İlaç listesini ve veritabanını tutan değişkenler.
    private List<Ilac> ilacList;  // İlaç bilgilerini tutar.
    private IlacDatabaseHelper databaseHelper; // Veritabanı işlemleri için referans.

    // Constructor: İlaç listesi ve veritabanı referansını alır.
    public IlacAdapter(List<Ilac> ilacList, IlacDatabaseHelper databaseHelper) {
        this.ilacList = ilacList;  // Dışarıdan alınan ilaç listesi
        this.databaseHelper = databaseHelper;  // Veritabanı yardımcı sınıfı.
    }

    @NonNull
    @Override
    public IlacViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Her bir liste elemanının görünümünü (layout) bağlar.
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ilac, parent, false);  // `item_ilac` layout dosyasını bağlar.
        return new IlacViewHolder(view); // Yeni bir ViewHolder döndürür.
    }

    @Override
    public void onBindViewHolder(@NonNull IlacViewHolder holder, int position) {
        // Mevcut pozisyondaki ilaç bilgilerini alır.
        Ilac ilac = ilacList.get(position);
        holder.ilacIsmi.setText("" + ilac.getIsim());  // İlaç ismini ayarlar.
        holder.ilacTarihi.setText("Tarih: " + ilac.getTarih());  // İlaç tarihini ayarlar.
        holder.ilacSaatleri.setText("Saatler: " + String.join(", ", ilac.getSaatler()));  // İlaç saatlerini virgülle ayırarak gösterir.

        // Silme butonu için tıklama işlemi
        holder.buttonDeleteIlac.setOnClickListener(v -> {
            // Veritabanından mevcut ilaç kaydını siler.
            databaseHelper.deleteIlac(ilac.getId());

            // İlaç listesinde ilgili elemanı kaldırır.
            ilacList.remove(position);
            notifyItemRemoved(position);  // Silinen eleman için RecyclerView'i günceller.
            notifyItemRangeChanged(position, ilacList.size()); // Liste boyutunu günceller.
        });
    }

    @Override
    public int getItemCount() {
        // İlaç listesindeki eleman sayısını döndürür.
        return ilacList.size();
    }

    // RecyclerView için özel bir ViewHolder sınıfı.
    public static class IlacViewHolder extends RecyclerView.ViewHolder {
        TextView ilacIsmi, ilacTarihi, ilacSaatleri;  // Görüntülenecek metin alanları.
        ImageButton buttonDeleteIlac; // Silme butonu

        public IlacViewHolder(@NonNull View itemView) {
            super(itemView);
            // `item_ilac` layout dosyasındaki bileşenleri bağlar.
            ilacIsmi = itemView.findViewById(R.id.textIlacIsmi);  // İlaç ismi metin alanı.
            ilacTarihi = itemView.findViewById(R.id.textIlacTarihi);  // İlaç tarihi metin alanı.
            ilacSaatleri = itemView.findViewById(R.id.textIlacSaatleri);  // İlaç saatleri metin alanı.
            buttonDeleteIlac = itemView.findViewById(R.id.buttonDeleteIlac); // Silme butonunu bağla
        }
    }
}

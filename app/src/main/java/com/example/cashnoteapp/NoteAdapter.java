package com.example.cashnoteapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cashnoteapp.model.DataItem;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder> {
    public static final String DATA_KETERANGAN = "keterangan";
    public static final String DATA_PENGELUARAN = "pengeluaran";
    public static final String DATA_TANGGAL = "tanggal" ;
    public static final String DATA_IDE = "id";
    Context context;
    List<DataItem> noteModels = new ArrayList<>();

    //constructor -> mengirim data
    //klik kanan -> generete -> constractor


    public NoteAdapter(Context context, List<DataItem> noteModels) {
        this.context = context;
        this.noteModels = noteModels;
    }

    //sambungkan ke layout item
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    //set data
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {

        final String keterangan = noteModels.get(i).getKeterangan();
        myViewHolder.tvKeterangan.setText(keterangan);
        final String pengeluaran = noteModels.get(i).getPengeluaran();
        myViewHolder.tvPengeluaran.setText("Rp "+ pengeluaran);
        final String tanggal = noteModels.get(i).getTanggal();
        myViewHolder.tvTanggal.setText(tanggal);
        Glide.with(context).load("https://banner2.kisspng.com/20180328/tse/kisspng-money-bag-computer-icons-coin-tax-market-5abbb0febf56f2.7630683415222499827837.jpg").into(myViewHolder.imageView);

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pindah = new Intent(context, UpdateDataActivity.class);

                //kirim data
                pindah.putExtra(DATA_IDE, noteModels.get(i).getId());
                pindah.putExtra(DATA_KETERANGAN, keterangan);
                pindah.putExtra(DATA_PENGELUARAN, pengeluaran);
                pindah.putExtra(DATA_TANGGAL, tanggal);
                context.startActivity(pindah);

            }
        });

    }

    @Override
    public int getItemCount() {
        return noteModels.size();
    }

    //mengenalkan komponen dalam item
    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView ;
        TextView tvKeterangan, tvTanggal, tvPengeluaran;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivItemGambar);
            tvKeterangan = itemView.findViewById(R.id.tvItemKeterangan);
            tvPengeluaran = itemView.findViewById(R.id.tvItemPengeluaran);
            tvTanggal = itemView.findViewById(R.id.tvItemTanggal);
        }
    }

    //extends class ke RecyclerView.Adapter
}

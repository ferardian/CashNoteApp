package com.example.cashnoteapp;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.cashnoteapp.database.RealmHelper;
import com.example.cashnoteapp.model.DataItem;
import com.example.cashnoteapp.model.ResponseInsert;
import com.example.cashnoteapp.retrofit.ApiConfig;
import com.shagi.materialdatepicker.date.DatePickerFragmentDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TambahDataActivity extends AppCompatActivity {

    private TextInputEditText edtPengeluaran;
    private TextInputEditText edtKeterangan;
    private TextInputEditText edtTanggal;
    private Button btnTambah;

    RealmHelper realmHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_data);
        initView();

        //inisalisasi Realm
        realmHelper = new RealmHelper(this);

        edtTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragmentDialog dialog = DatePickerFragmentDialog.newInstance(new DatePickerFragmentDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerFragmentDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        edtTanggal.setText(format.format(calendar.getTime()));
                    }
                });
                dialog.show(getSupportFragmentManager(), "tag");
            }
        });

        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()){
                    addData();
                } else {
                    addDataOffline();
                }


            }
        });
    }

    private void addData() {
        Call<ResponseInsert> request = ApiConfig.getApiService().tambahData(
                edtKeterangan.getText().toString(),
                edtPengeluaran.getText().toString(),
                edtTanggal.getText().toString()
        );

        request.enqueue(new Callback<ResponseInsert>() {
            @Override
            public void onResponse(Call<ResponseInsert> call, Response<ResponseInsert> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TambahDataActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                    if (response.body().getResult().equals("true")){
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseInsert> call, Throwable t) {
                Toast.makeText(TambahDataActivity.this, "Response Failure", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void initView() {
        edtPengeluaran = (TextInputEditText) findViewById(R.id.edtPengeluaran);
        edtKeterangan = (TextInputEditText) findViewById(R.id.edtKeterangan);
        edtTanggal = (TextInputEditText) findViewById(R.id.edtTanggal);
        btnTambah = (Button) findViewById(R.id.btnTambah);
    }

    private boolean isOnline() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info!= null && info.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    private void addDataOffline() {
        DataItem dataItem = new DataItem();
        dataItem.setKeterangan(edtKeterangan.getText().toString());
        dataItem.setPengeluaran(edtPengeluaran.getText().toString());
        dataItem.setTanggal(edtTanggal.getText().toString());

        realmHelper.addOneData(dataItem);
        finish();
    }


}

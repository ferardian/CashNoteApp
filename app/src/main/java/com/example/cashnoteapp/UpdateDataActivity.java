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
import com.example.cashnoteapp.worker.DeleteWorker;
import com.example.cashnoteapp.worker.UpdateWorker;
import com.shagi.materialdatepicker.date.DatePickerFragmentDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateDataActivity extends AppCompatActivity {

    private TextInputEditText edtPengeluaran;
    private TextInputEditText edtKeterangan;
    private TextInputEditText edtTanggal;
    private Button btnDelete;
    private Button btnUpdate;
    private String dataId;

    RealmHelper realmHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_data);

        //inisialisasi Realm
        realmHelper = new RealmHelper(this);

        //terima data
        dataId = getIntent().getStringExtra(NoteAdapter.DATA_IDE);
        String dataKeterangan = getIntent().getStringExtra(NoteAdapter.DATA_KETERANGAN);
        String dataPengeluaran = getIntent().getStringExtra(NoteAdapter.DATA_PENGELUARAN);
        String dataTanggal = getIntent().getStringExtra(NoteAdapter.DATA_TANGGAL);

        initView();

        edtKeterangan.setText(dataKeterangan);
        edtPengeluaran.setText(dataPengeluaran);
        edtTanggal.setText(dataTanggal);

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

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    updateData();
                } else {
                    updateDataOffline();
                    updateDataWorker();
                }

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()){
                    deleteData();
                } else {
                    deleteDataOffline();
                    deleteDataWorker();
                }

            }
        });
    }

    private void deleteDataWorker() {
        WorkManager manager = WorkManager.getInstance();
        Data.Builder data = new Data.Builder();
        data.putString(NoteAdapter.DATA_IDE,dataId);
        Data newdata = data.build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(DeleteWorker.class)
                .setInputData(newdata)
                .setConstraints(constraints)
                .build();

        manager.enqueue(request);
    }

    private void updateDataWorker() {
        WorkManager manager = WorkManager.getInstance();
        Data.Builder data = new Data.Builder();
        data.putString(NoteAdapter.DATA_IDE,dataId);
        data.putString(NoteAdapter.DATA_KETERANGAN,edtKeterangan.getText().toString());
        data.putString(NoteAdapter.DATA_PENGELUARAN,edtPengeluaran.getText().toString());
        data.putString(NoteAdapter.DATA_TANGGAL,edtTanggal.getText().toString());
        Data newdata = data.build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(UpdateWorker.class)
                .setInputData(newdata)
                .setConstraints(constraints)
                .build();

        manager.enqueue(request);
    }

    private void updateDataOffline() {
        DataItem dataItem = new DataItem();
        dataItem.setId(dataId);
        dataItem.setKeterangan(edtKeterangan.getText().toString());
        dataItem.setPengeluaran(edtPengeluaran.getText().toString());
        dataItem.setTanggal(edtTanggal.getText().toString());

        realmHelper.updateData(dataItem);
        finish();
    }


    private void deleteDataOffline() {
        DataItem dataItem = new DataItem();
        dataItem.setId(dataId);

        realmHelper.deleteOneData(dataId);
        finish();
    }




    private void deleteData() {
        Call<ResponseInsert> request = ApiConfig.getApiService().deleteData(
                dataId
        );

        request.enqueue(new Callback<ResponseInsert>() {
            @Override
            public void onResponse(Call<ResponseInsert> call, Response<ResponseInsert> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UpdateDataActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                    if (response.body().getResult().equals("true")){
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseInsert> call, Throwable t) {
                Toast.makeText(UpdateDataActivity.this, "Response Failure", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void updateData() {

            Call<ResponseInsert> request = ApiConfig.getApiService().updateData(
                    dataId,
                    edtKeterangan.getText().toString(),
                    edtPengeluaran.getText().toString(),
                    edtTanggal.getText().toString()
            );

            request.enqueue(new Callback<ResponseInsert>() {
                @Override
                public void onResponse(Call<ResponseInsert> call, Response<ResponseInsert> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(UpdateDataActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                        if (response.body().getResult().equals("true")){
                            finish();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseInsert> call, Throwable t) {
                    Toast.makeText(UpdateDataActivity.this, "Response Failure", Toast.LENGTH_SHORT).show();

                }
            });

    }

    private void initView() {
        edtPengeluaran = (TextInputEditText) findViewById(R.id.edtPengeluaran);
        edtKeterangan = (TextInputEditText) findViewById(R.id.edtKeterangan);
        edtTanggal = (TextInputEditText) findViewById(R.id.edtTanggal);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
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
}

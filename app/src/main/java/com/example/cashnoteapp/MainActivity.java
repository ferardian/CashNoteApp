package com.example.cashnoteapp;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.cashnoteapp.database.RealmHelper;
import com.example.cashnoteapp.model.DataItem;
import com.example.cashnoteapp.model.ResponseNote;
import com.example.cashnoteapp.retrofit.ApiConfig;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {

    List<DataItem> listData = new ArrayList<>();
    private RecyclerView recyclerView;
    RealmHelper realmHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                startActivity(new Intent(MainActivity.this, TambahDataActivity.class));
            }
        });

        //layout item (list)
        //model data
//        DataItem noteModel = new DataItem();
//        noteModel.setId("1");
//        noteModel.setKeterangan("Jajan");
//        noteModel.setPengeluaran("30.000");
//        noteModel.setTanggal("26-03-2019");
//
//        for (int i = 0; i < 10; i++) {
//            listData.add(noteModel);
//        }

        realmHelper = new RealmHelper(MainActivity.this);

        initView();

        //cek koneksi
        if (isOnline()) {
            getDataOnline();
        } else {
            getDataOffline();
        }


        //adapter

        //layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));



    }

    private void getDataOffline() {
        listData = realmHelper.tampilkanData();

        //adapter
        NoteAdapter adapter = new NoteAdapter(MainActivity.this, listData);
        recyclerView.setAdapter(adapter);

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

    private void getDataOnline() {
        Call<ResponseNote> request = ApiConfig.getApiService().tampilDataCatatan();
        request.enqueue(new Callback<ResponseNote>() {
            @Override
            public void onResponse(Call<ResponseNote> call, retrofit2.Response<ResponseNote> response) {
                if (response.isSuccessful()) {
                    if (response.body().getResult().equals("true")) {
                        //masukkan list
                        listData = response.body().getData();

                        //hapus data lama
                        realmHelper.deleteData();
                        //insert to db
                        for (int i = 0; i < listData.size(); i++) {
                            DataItem data = listData.get(i);
                            realmHelper.insertData(data);
                        }


                        NoteAdapter noteAdapter = new NoteAdapter(MainActivity.this, listData);
                        recyclerView.setAdapter(noteAdapter);

                    } else {
                        Toast.makeText(MainActivity.this,"Respon Not Success",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseNote> call, Throwable t) {
                Toast.makeText(MainActivity.this,"Respon Failure",Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isOnline()){
            getDataOnline();
        } else {
            getDataOffline();
        }

    }
}

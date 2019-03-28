package com.example.cashnoteapp.database;

import android.content.Context;
import android.widget.Toast;

import com.example.cashnoteapp.model.DataItem;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class RealmHelper {
    private Context context;
    private Realm realm;
    private RealmResults<DataItem> results;

    public RealmHelper(Context context) {
        this.context = context;

        Realm.init(context);
        realm = Realm.getDefaultInstance();
    }

    //insert
    public void insertData(DataItem data) {
        realm.beginTransaction();
        realm.copyToRealm(data);
        realm.commitTransaction();
    }

    //upadate
    public void updateData(DataItem data) {
        realm.beginTransaction();
        //DataItem databaru = realm.where(DataItem.class).equalTo("id", data.getId()).findFirst();
        realm.copyToRealmOrUpdate(data);
//        realm.addChangeListener(new RealmChangeListener<Realm>() {
//            @Override
//            public void onChange(Realm realm) {
//                Toast.makeText(context, "Data berhasil diupdate", Toast.LENGTH_SHORT).show();
//            }
//        });
        realm.commitTransaction();
    }

    //delete Offline
    public void deleteOneData(String id) {
        realm.beginTransaction();
        DataItem databaru = realm.where(DataItem.class).equalTo("id", id).findFirst();
        databaru.deleteFromRealm(databaru);
        realm.addChangeListener(new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm realm) {
                Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
            }
        });
        realm.commitTransaction();
    }

    //add Offline
    public void addOneData(DataItem data) {
        realm.beginTransaction();
        //DataItem databaru = realm.where(DataItem.class).equalTo("id", data.getId()).findFirst();
        realm.copyToRealm(data);
        realm.addChangeListener(new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm realm) {
                Toast.makeText(context, "Data berhasil ditambah", Toast.LENGTH_SHORT).show();
            }
        });
        realm.commitTransaction();
    }

    //delete
    public void deleteData() {
        realm.beginTransaction();
        RealmResults<DataItem> data = realm.where(DataItem.class).findAll();
        data.deleteAllFromRealm();
        realm.commitTransaction();
    }

    public List<DataItem> tampilkanData(){
        results = realm.where(DataItem.class).findAll();
        List<DataItem> data = new ArrayList<>();
        data.addAll(realm.copyFromRealm(results));
        return data;
    }

}

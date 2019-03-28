package com.example.cashnoteapp.retrofit;

import com.example.cashnoteapp.model.ResponseInsert;
import com.example.cashnoteapp.model.ResponseNote;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @GET("show_cash_note.php")
    Call<ResponseNote> tampilDataCatatan();

    //post
    @FormUrlEncoded
    @POST("add_note.php")
    Call<ResponseInsert> tambahData(
      @Field("keterangan") String keterangan,
      @Field("pengeluaran") String pengeluaran,
      @Field("tanggal") String tanggal
    );

    //update
    @FormUrlEncoded
    @POST("update_note.php")
    Call<ResponseInsert> updateData(
      @Field("id") String id,
      @Field("keterangan") String keterangan,
      @Field("pengeluaran") String pengeluaran,
      @Field("tanggal") String tanggal
    );

    //delete
    @FormUrlEncoded
    @POST("delete_note.php")
    Call<ResponseInsert> deleteData(
      @Field("id") String id
    );

}

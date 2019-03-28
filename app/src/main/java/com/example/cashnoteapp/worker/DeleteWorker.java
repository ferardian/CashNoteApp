package com.example.cashnoteapp.worker;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.cashnoteapp.MainActivity;
import com.example.cashnoteapp.NoteAdapter;
import com.example.cashnoteapp.model.ResponseInsert;
import com.example.cashnoteapp.retrofit.ApiConfig;

import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteWorker extends Worker {
    private Context context;

    public DeleteWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        try {

            String dataId = getInputData().getString(NoteAdapter.DATA_IDE);

            Call<ResponseInsert> request = ApiConfig.getApiService().deleteData(
                    dataId
            );

            request.enqueue(new Callback<ResponseInsert>() {
                @Override
                public void onResponse(Call<ResponseInsert> call, Response<ResponseInsert> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                        if (response.body().getResult().equals("true")){
                            context.startActivity(new Intent(context, MainActivity.class));
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseInsert> call, Throwable t) {
                    Toast.makeText(context, "Response Failure", Toast.LENGTH_SHORT).show();

                }
            });

            return Worker.Result.success();
        } catch (Exception e) {
            return Worker.Result.failure();
        }
    }
}

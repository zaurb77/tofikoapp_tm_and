package com.mangalhousemanager.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.JsonSyntaxException;
import com.mangalhousemanager.R;
import com.mangalhousemanager.databinding.ActivityChnagePasswordBinding;
import com.mangalhousemanager.databinding.ActivityForgotPasswordBinding;
import com.mangalhousemanager.utils.Constants;
import com.mangalhousemanager.utils.RetrofitHelper;
import com.mangalhousemanager.utils.StoreUserData;
import com.mangalhousemanager.utils.Utils;

import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    ActivityChnagePasswordBinding binding;
    AppCompatActivity activity;
    StoreUserData storeUserData;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_chnage_password);


        binding.submit.setOnClickListener(v -> {
            if (Utils.isEmpty(binding.newPassword)){
                Utils.showTopMessageError(activity,Constants.PROVIDE_PASS);
            }else if (Utils.isEmpty(binding.confirmPassword)){
                Utils.showTopMessageError(activity,Constants.CONFIRM_PASS);
            }else {
                if (Objects.requireNonNull(binding.newPassword.getText()).toString().equalsIgnoreCase(Objects.requireNonNull(binding.confirmPassword.getText()).toString())){
                    changePasswordApi(binding.confirmPassword.getText().toString().trim());
                    Utils.hideKB(activity,binding.submit);
                }else {
                    Utils.showTopMessageError(activity,Constants.PASS_NOT_MATCH);
                }
            }
        });

        binding.back.setOnClickListener(view -> finish());
    }



    private void changePasswordApi(String password) {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().changePassword(
                storeUserData.getString(Constants.res_id),
                storeUserData.getString(Constants.token),
                password,
                "1"
        );

        retrofitHelper.callApi(activity, call, new RetrofitHelper.ConnectionCallBack() {
            @Override
            public void onSuccess(Response<ResponseBody> body) {
                Utils.dismissProgress();
                try {
                    if (body.code() != 200) {
                        Utils.serverError(activity, body.code());
                        return;
                    }
                    String response = body.body().string();
                    Log.i("Change_Password", "" + response);
                    Utils.showTopMessageSuccess(activity,"Password changed successfully.");
                    new Handler().postDelayed(() -> finish(),2000);


                } catch (IOException | NullPointerException | JsonSyntaxException e) {
                    e.printStackTrace();
                    Utils.dismissProgress();
                }
            }

            @Override
            public void onError(int code, String error) {
                Utils.dismissProgress();
                Log.e("ERROR", error);
            }
        });
    }

}

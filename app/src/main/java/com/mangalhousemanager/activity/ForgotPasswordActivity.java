package com.mangalhousemanager.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.JsonSyntaxException;
import com.mangalhousemanager.R;
import com.mangalhousemanager.databinding.ActivityForgotPasswordBinding;
import com.mangalhousemanager.utils.Constants;
import com.mangalhousemanager.utils.RetrofitHelper;
import com.mangalhousemanager.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    ActivityForgotPasswordBinding binding;
    AppCompatActivity activity;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_forgot_password);

        binding.email.setHint(Constants.EMAIL_ADDRESS);
        binding.submit.setText(Constants.SUBMIT);
        binding.tvOne.setText(Constants.SEND_PASS);


        binding.submit.setOnClickListener(view -> {
            if (Utils.isEmpty(binding.email)) {
                Utils.showTopMessageError(activity, Constants.PROVIDE_EMAIL);
            } else {
                if (!Utils.isValidEmail(binding.email)) {
                    Utils.showTopMessageError(activity,  Constants.VAILD_EMAIL);
                } else {
                    forgotPasswordApi();
                }
            }
        });
        binding.back.setOnClickListener(view -> finish());
    }


    //TODO :DO FORGOT PASSWORD
    private void forgotPasswordApi() {
        Utils.hideKB(activity, binding.submit);
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().forgotPassword(
                binding.email.getText().toString().trim()
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
                    Log.i("FORGOT_RESPONSE", "onSuccess: " + response);

                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("status") == 0) {
                        Utils.showTopMessageError(activity, jsonObject.getString("message"));
                    } else {
                        binding.email.setText("");
                        Utils.showTopMessageError(activity, Constants.EMAIL_SENT_SUCCESS + binding.email.getText().toString());
                    }
                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int code, String error) {
                Utils.dismissProgress();
            }
        });
    }
}

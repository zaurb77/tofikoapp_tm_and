package com.mangalhousemanager.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.JsonSyntaxException;
import com.mangalhousemanager.R;
import com.mangalhousemanager.databinding.ActivityNotificationBinding;
import com.mangalhousemanager.utils.Constants;
import com.mangalhousemanager.utils.RetrofitHelper;
import com.mangalhousemanager.utils.StoreUserData;
import com.mangalhousemanager.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class PushNotificationActivity extends AppCompatActivity {


    ActivityNotificationBinding binding;
    AppCompatActivity activity;
    StoreUserData storeUserData;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_notification);
        binding.backNotification.setOnClickListener(view -> finish());

        binding.tvOrderNotification.setText(Constants.ORDER_NOTIFICATION.substring(0, 1).toUpperCase() + Constants.ORDER_NOTIFICATION.substring(1).toLowerCase());
        binding.tvPushNotification.setText( Constants.PUSH_NOTIFICATION.substring(0, 1).toUpperCase() + Constants.PUSH_NOTIFICATION.substring(1).toLowerCase());
        binding.tvEmailNotification.setText(Constants.EMAIL_NOTIFICATION.substring(0, 1).toUpperCase() + Constants.EMAIL_NOTIFICATION.substring(1).toLowerCase());
        binding.tvTitle.setText( Constants.PUSH_NOTIFICATION.substring(0, 1).toUpperCase() + Constants.PUSH_NOTIFICATION.substring(1).toLowerCase());

        if (storeUserData.getInt(Constants.push_notification) == 1) {
            binding.pushNotification.setImageResource(R.drawable.switchon);
        } else {
            binding.pushNotification.setImageResource(R.drawable.switchoff);
        }

        if (storeUserData.getInt(Constants.email_notification) == 1) {
            binding.emailNotification.setImageResource(R.drawable.switchon);
        } else {
            binding.emailNotification.setImageResource(R.drawable.switchoff);
        }

        binding.pushNotification.setOnClickListener(view -> {
            if (storeUserData.getInt(Constants.push_notification) == 1) {
                changeNotificationApi(0,"push_notification");
            } else {
                changeNotificationApi(1,"push_notification");
            }
        });

        binding.emailNotification.setOnClickListener(view -> {
            if (storeUserData.getInt(Constants.email_notification) == 1) {
                changeNotificationApi(0,"email_notification");
            } else {
                changeNotificationApi(1,"email_notification");
            }
        });
    }

    //TODO :Change Notification status
    private void changeNotificationApi(int status, String type) {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().setNotification(
                storeUserData.getString(Constants.res_id),
                storeUserData.getString(Constants.token),
                type,
                status,
                storeUserData.getString(Constants.LANG_ID)
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
                    Log.i("changeNotiStatus", "onSuccess: " + response);

                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("status") == 0) {
                        Utils.showTopMessageError(activity, jsonObject.getString("message"));
                    } else {

                        if (type.equalsIgnoreCase("push_notification")){

                            if (status == 1) {
                                binding.pushNotification.setImageResource(R.drawable.switchon);
                            } else {
                                binding.pushNotification.setImageResource(R.drawable.switchoff);
                            }
                            storeUserData.setInt(Constants.push_notification, status);

                        }else {
                            if (status == 1) {
                                binding.emailNotification.setImageResource(R.drawable.switchon);
                            } else {
                                binding.emailNotification.setImageResource(R.drawable.switchoff);
                            }
                            storeUserData.setInt(Constants.email_notification, status);
                        }

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

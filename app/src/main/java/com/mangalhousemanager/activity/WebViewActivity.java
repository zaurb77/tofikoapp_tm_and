package com.mangalhousemanager.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.JsonSyntaxException;
import com.mangalhousemanager.R;
import com.mangalhousemanager.databinding.ActivityNotificationBinding;
import com.mangalhousemanager.databinding.ActivityTermsAndConditionBinding;
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

public class WebViewActivity  extends AppCompatActivity{

    ActivityTermsAndConditionBinding binding;
    AppCompatActivity activity;
    StoreUserData storeUserData;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_terms_and_condition);
        binding.backCondition.setOnClickListener(view -> finish());
        binding.title.setText(getIntent().getStringExtra("name"));
        getPageApi(getIntent().getStringExtra("type"));
    }


    //TODO : GET TERMS AND CONDITION FROM WEB
    private void getPageApi(String pageName) {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call = retrofitHelper.api().staticPage(pageName);
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
                    Log.i("TAG", "onSuccess: " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1){
                        JSONObject jsonObject1 = jsonObject.getJSONObject("responsedata");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            binding.text.setText(Html.fromHtml(jsonObject1.getString("content"), Html.FROM_HTML_MODE_COMPACT));
                        }else {
                            binding.text.setText(Html.fromHtml(jsonObject1.getString("content")));
                        }
                    }
                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int code, String error) {
                try {
                    Utils.dismissProgress();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

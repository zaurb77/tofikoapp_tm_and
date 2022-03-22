package com.mangalhousemanager.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mangalhousemanager.R;
import com.mangalhousemanager.databinding.ActivityAddPointBinding;
import com.mangalhousemanager.pojo.OrderDetailPojo;
import com.mangalhousemanager.pojo.UserInfoPojo;
import com.mangalhousemanager.utils.Constants;
import com.mangalhousemanager.utils.RetrofitHelper;
import com.mangalhousemanager.utils.StoreUserData;
import com.mangalhousemanager.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Modifier;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class AddPointActivity extends AppCompatActivity {


    ActivityAddPointBinding binding;
    AppCompatActivity activity;
    StoreUserData storeUserData;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity,R.layout.activity_add_point);


        binding.title.setText(Constants.ADD_POINT);
        binding.tvConfirmInformation.setText("Confirm Information");
        binding.enterMangalTV.setText(Constants.ENTER_MANGALS);
        binding.btnSubmit.setText(Constants.SUBMIT);


        binding.back.setOnClickListener(view ->{
            startActivity(new Intent(activity,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        });

        Glide.with(activity)
                .load(R.drawable.ic_app)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.image);

        binding.btnSubmit.setOnClickListener(view -> {
            if (binding.mangals.getText().toString().length()>0){
                addPoints();
            }else {
                binding.mangals.setError("Please enter your points.");
            }
        });

        getUserInformation();

    }


    private void getUserInformation() {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().getUserDetails(
                storeUserData.getString(Constants.res_id),
                storeUserData.getString(Constants.token),
                getIntent().getStringExtra("code"),
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
                    Log.i("getUserDetails", "getUserDetails: " + response);

                    Reader reader = new StringReader(response);
                    Utils.dismissProgress();
                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    UserInfoPojo pojo = gson.fromJson(reader, UserInfoPojo.class);

                    if (pojo.status == 1){

                        binding.name.setText(pojo.responsedata.name);
                        binding.emailAddress.setText(pojo.responsedata.email);
                        binding.mobileNo.setText(pojo.responsedata.mobile_no);
                        userId = pojo.responsedata.id;

                        Glide.with(activity)
                                .load(pojo.responsedata.image)
                                .into(binding.infoImage);

                    }

                } catch (IOException | NullPointerException | JsonSyntaxException  e) {
                    e.printStackTrace();
                    Utils.dismissProgress();
                }
            }

            @Override
            public void onError(int code, String error) {
                Utils.dismissProgress();
                if (code == -1) {
                    Utils.showTopMessageError(activity, error);
                }
                Log.e("Notification Error", error);
            }
        });
    }



    private void addPoints() {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().addPoints(
                storeUserData.getString(Constants.res_id),
                storeUserData.getString(Constants.token),
                userId,
                Objects.requireNonNull(binding.mangals.getText()).toString(),
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
                    Log.i("getUserDetails", "getUserDetails: " + response);

                    Reader reader = new StringReader(response);


                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("status") == 1){
                        Toast.makeText(activity, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(activity,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }

                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
                    e.printStackTrace();
                    Utils.dismissProgress();
                }
            }

            @Override
            public void onError(int code, String error) {
                Utils.dismissProgress();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(activity,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}

package com.mangalhousemanager.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.gson.JsonSyntaxException;
import com.mangalhousemanager.R;
import com.mangalhousemanager.databinding.ActivityRegisterBinding;
import com.mangalhousemanager.utils.Constants;
import com.mangalhousemanager.utils.RetrofitHelper;
import com.mangalhousemanager.utils.StoreUserData;
import com.mangalhousemanager.utils.Utils;
import com.ybs.countrypicker.CountryPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private AppCompatActivity activity;
    private StoreUserData storeUserData;
    private CountryPicker picker;
    private ArrayList<String> companyList = new ArrayList<>();
    private ArrayList<String> companyIds = new ArrayList<>();
    private ArrayList<String> storeList = new ArrayList<>();
    private ArrayList<String> storeIds = new ArrayList<>();
    private String selectedStoreId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_register);
        binding.back.setOnClickListener(v -> finish());

        binding.edtFirstName.setHint(Constants.FNAME);
        binding.edtLastName.setHint(Constants.LNAME);
        binding.edtEmailAddress.setHint(Constants.EMAIL_ADDRESS);
        binding.edtPassword.setHint(Constants.PASSWORD);
        binding.edtPhoneNumber.setHint(Constants.PHONE_NUMBER);
        binding.tvCompany.setHint(Constants.COMPANY);
        binding.tvStore.setHint(Constants.STORE);
        binding.tvRegister.setText(Constants.REGISTER);
        binding.alreadyLoginText.setText(Constants.LOGIN_FLOW);
        binding.title.setText(Constants.JOIN_US);

        binding.alreadyLoginText.setOnClickListener(v -> finish());

        picker = CountryPicker.newInstance("Country Code");
        picker.setListener((name, code, dialCode, flagDrawableResID) -> {
            binding.tvCountryCode.setText(dialCode);
            picker.dismiss();
        });

        binding.tvCompany.setOnClickListener(v -> getCompaniesApi());

        binding.tvStore.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity, R.layout.questionlist, storeList);
            builder.setAdapter(dataAdapter, (dialog, which) -> {
                binding.tvStore.setText(storeList.get(which));
                selectedStoreId = storeIds.get(which);
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        binding.tvCountryCode.setOnClickListener(v -> openPicker(binding.tvCountryCode));

        binding.tvRegister.setOnClickListener(v -> {
            if (Objects.requireNonNull(binding.edtFirstName.getText()).toString().length() == 0){
                Utils.showTopMessageError(activity,Constants.PROVIDE_FNAME);
            }else if (Objects.requireNonNull(binding.edtLastName.getText()).toString().length() == 0){
                Utils.showTopMessageError(activity,Constants.PROVIDE_LNAME);
            }else if (Objects.requireNonNull(binding.edtEmailAddress.getText()).toString().length() == 0){
                Utils.showTopMessageError(activity,Constants.PROVIDE_EMAIL);
            }else if (Objects.requireNonNull(binding.edtPassword.getText()).toString().length() == 0){
                Utils.showTopMessageError(activity,Constants.PROVIDE_PASS);
            }else if (binding.tvCountryCode.getText().toString().length() == 0){
                Utils.showTopMessageError(activity,"Please select your country code.");
            }else if (Objects.requireNonNull(binding.edtPhoneNumber.getText()).toString().length() == 0){
                Utils.showTopMessageError(activity,Constants.PROVIDE_PH_NO);
            }else if (binding.tvCompany.getText().toString().length() == 0){
                Utils.showTopMessageError(activity,Constants.SELECT_COMPANY);
            }else if (binding.tvStore.getText().toString().length() == 0){
                Utils.showTopMessageError(activity,Constants.SELECT_STORE);
            }else {
                if (Utils.isValidEmail(binding.edtEmailAddress)){
                    registerApi();
                }else {
                    Utils.showTopMessageError(activity,Constants.VAILD_EMAIL);
                }
            }
        });
    }


    public void openPicker(View view) {
        picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
    }


    private void getCompaniesApi() {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().getCompanies(
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
                    Log.i("getCompanies", "" + response);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        companyIds.clear();
                        companyList.clear();
                        JSONArray companyData = jsonObject.getJSONArray("responsedata");
                        for (int i = 0; i < companyData.length(); i++) {
                            JSONObject dataObject = companyData.getJSONObject(i);
                            companyList.add(dataObject.getString("name"));
                            companyIds.add(dataObject.getString("id"));
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity, R.layout.questionlist, companyList);
                        builder.setAdapter(dataAdapter, (dialog, which) -> {
                            binding.tvCompany.setText(companyList.get(which));
                            binding.tvStore.setText("");
                            getStoreByCompanyApi(companyIds.get(which));
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
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


    private void getStoreByCompanyApi(String companyId) {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().getStoreByCompany(
                "1",
                companyId
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
                    Log.i("getStoreByCompany", "" + response);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        storeList.clear();
                        storeIds.clear();
                        JSONArray companyData = jsonObject.getJSONArray("responsedata");
                        for (int i = 0; i < companyData.length(); i++) {
                            JSONObject dataObject = companyData.getJSONObject(i);
                            storeList.add(dataObject.getString("name"));
                            storeIds.add(dataObject.getString("id"));
                        }
                    }
                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
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


    private void registerApi() {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().userRegister(
                "1",
                binding.edtFirstName.getText().toString().trim(),
                binding.edtLastName.getText().toString().trim(),
                binding.tvCountryCode.getText().toString().trim(),
                binding.edtPhoneNumber.getText().toString().trim(),
                selectedStoreId,
                binding.edtEmailAddress.getText().toString().trim(),
                binding.edtPassword.getText().toString().trim(),
                storeUserData.getString(Constants.USER_FCM)
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
                    Log.i("Register", "" + response);

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1){
                        Utils.showTopMessageSuccess(activity,jsonObject.getString("message"));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },2000);
                    }else{
                        Utils.showTopMessageSuccess(activity,jsonObject.getString("message"));
                    }
                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
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
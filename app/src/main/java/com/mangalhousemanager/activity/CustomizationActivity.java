package com.mangalhousemanager.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mangalhousemanager.R;
import com.mangalhousemanager.adapter.FreeCustAdapter;
import com.mangalhousemanager.adapter.OrdersDetailsAdapter;
import com.mangalhousemanager.adapter.PaidCustAdapter;
import com.mangalhousemanager.databinding.ActivityCustomizationBinding;
import com.mangalhousemanager.pojo.CustonizationPojo;
import com.mangalhousemanager.pojo.OrderDetailPojo;
import com.mangalhousemanager.utils.Constants;
import com.mangalhousemanager.utils.RetrofitHelper;
import com.mangalhousemanager.utils.StoreUserData;
import com.mangalhousemanager.utils.Utils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class CustomizationActivity extends AppCompatActivity {

    ActivityCustomizationBinding binding;
    AppCompatActivity activity;
    StoreUserData storeUserData;

    ArrayList<CustonizationPojo.ResponseData.FreeCust> freeCustomization = new ArrayList<>();
    ArrayList<CustonizationPojo.ResponseData.PaidCust> paidCustomization = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity,R.layout.activity_customization);

        binding.title.setText(Constants.CUSTOMIZATION);
        binding.tvFree.setText(Constants.FREE);
        binding.tvPaid.setText(Constants.PAID);

        binding.rvCustomization.setLayoutManager(new LinearLayoutManager(activity));
        binding.rvCustomization.setNestedScrollingEnabled(false);
        binding.rvCustomization.setHasFixedSize(true);

        binding.tvFree.setOnClickListener(view -> {
            binding.tvPaid.setBackground(null);
            binding.tvFree.setBackgroundResource(R.drawable.bottom_line_order);
            binding.rvCustomization.setAdapter(new FreeCustAdapter(activity, freeCustomization/*, itemClickListener*/));
        });

        binding.tvPaid.setOnClickListener(view -> {
            binding.tvFree.setBackground(null);
            binding.tvPaid.setBackgroundResource(R.drawable.bottom_line_order);
            binding.rvCustomization.setAdapter(new PaidCustAdapter(activity, paidCustomization/*, itemClickListener*/));

        });

        binding.backCustomization.setOnClickListener(view -> finish());

        getCustomizationList(getIntent().getStringExtra("itemID"));
    }



    private void getCustomizationList(String orderId) {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().getCustomization(
                storeUserData.getString(Constants.res_id),
                storeUserData.getString(Constants.token),
                orderId
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
                    Log.i("CUSTOMIZATION", "onSuccess: " + response);


                    Reader reader = new StringReader(response);
                    Utils.dismissProgress();
                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CustonizationPojo pojo = gson.fromJson(reader, CustonizationPojo.class);

                    if (pojo.status == 1) {
                        binding.tvMessage.setVisibility(View.GONE);
                        freeCustomization = pojo.responsedata.free_cust;
                        paidCustomization = pojo.responsedata.paid_cust;
                        binding.rvCustomization.setAdapter(new FreeCustAdapter(activity, pojo.responsedata.free_cust/*, itemClickListener*/));
                        binding.llTab.setVisibility(View.VISIBLE);
                    }else {
                        binding.tvMessage.setVisibility(View.VISIBLE);
                        binding.tvMessage.setText(pojo.message);
                        binding.llTab.setVisibility(View.GONE);
                    }
                } catch (IOException | NullPointerException | JsonSyntaxException e) {
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

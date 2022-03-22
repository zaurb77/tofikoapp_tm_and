package com.mangalhousemanager.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mangalhousemanager.R;
import com.mangalhousemanager.adapter.MenuAdapter;
import com.mangalhousemanager.adapter.ProductListAdapter;
import com.mangalhousemanager.databinding.ActivityLoginBinding;
import com.mangalhousemanager.databinding.ActivityProductListBinding;
import com.mangalhousemanager.pojo.ItemListPojo;
import com.mangalhousemanager.pojo.MenuPojo;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity {

    ActivityProductListBinding binding;
    AppCompatActivity activity;
    StoreUserData storeUserData;
    private ItemClickListener itemClickListener;
    int itemStatus =-1;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_product_list);
        binding.backProductList.setOnClickListener(view -> finish());
        binding.title.setText(Constants.PRODUCT_LIST);
        binding.enableDisable.setText(Constants.ALL_ENABLE_DISABLE);

        binding.rvProductList.setLayoutManager(new LinearLayoutManager(activity));
        binding.rvProductList.setNestedScrollingEnabled(false);
        binding.rvProductList.setHasFixedSize(true);

        itemClickListener = new ItemClickListener() {
            @Override
            public void onClick(String id, String status) {
                changeItemStatus(id,status,"item");
            }
        };

        binding.switchStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemStatus == 1){
                    changeItemStatus(getIntent().getStringExtra("MENU_ITEM_ID"),"0","category");
                }else {
                    changeItemStatus(getIntent().getStringExtra("MENU_ITEM_ID"),"1","category");
                }
            }
        });

        getItemList(false);
    }


    //TODO :GET MENU LIST
    private void getItemList(boolean progress) {

        if (!progress){
            Utils.showProgress(activity);
        }
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().itemList(
                storeUserData.getString(Constants.res_id),
                storeUserData.getString(Constants.token),
                getIntent().getStringExtra("MENU_ITEM_ID"),
                "0",
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
                    Log.i("ProductList", "onSuccess: " + response);

                    Reader reader = new StringReader(response);
                    Utils.dismissProgress();
                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    ItemListPojo pojo = gson.fromJson(reader, ItemListPojo.class);

                    binding.categoryName.setText(pojo.responsedata.cat_name);
                    binding.categoryItem.setText(pojo.responsedata.items.size()+" Items");

                    if (pojo.status == 1) {

                        binding.rvProductList.setAdapter(new ProductListAdapter(activity, pojo.responsedata.items, itemClickListener));
                        binding.message.setText("");
                        binding.llMessage.setVisibility(View.GONE);

                        binding.llCategoryItem.setVisibility(View.VISIBLE);
                        binding.llSwitch.setVisibility(View.VISIBLE);

                        if (pojo.responsedata.items_status == 1){
                            binding.switchStatus.setImageResource(R.drawable.switchon);
                        }else {
                            binding.switchStatus.setImageResource(R.drawable.switchoff);
                        }

                        itemStatus = pojo.responsedata.items_status;

                    } else {
                        binding.message.setText(pojo.message);
                        binding.llMessage.setVisibility(View.VISIBLE);
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



    //TODO :Change Item status
    private void changeItemStatus(String id, String status,String type) {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().chanegItemStatus(
                storeUserData.getString(Constants.res_id),
                storeUserData.getString(Constants.token),
                id,
                status,
                type,
                storeUserData.getString(Constants.LANG_ID)
        );

        retrofitHelper.callApi(activity, call, new RetrofitHelper.ConnectionCallBack() {
            @Override
            public void onSuccess(Response<ResponseBody> body) {
                try {
                    if (body.code() != 200) {
                        Utils.serverError(activity, body.code());
                        return;
                    }
                    String response = body.body().string();
                    Log.i("changeItemStatus", "onSuccess: " + response);

                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("status") == 0) {
                        Utils.showTopMessageError(activity, jsonObject.getString("message"));
                    } else {
                       getItemList(true);
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


    public interface ItemClickListener {
        void onClick(String id,String Status);
    }
}

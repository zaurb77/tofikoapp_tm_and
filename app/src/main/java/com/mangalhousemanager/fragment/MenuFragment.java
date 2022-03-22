package com.mangalhousemanager.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mangalhousemanager.R;
import com.mangalhousemanager.adapter.MenuAdapter;
import com.mangalhousemanager.databinding.FragmentMenuBinding;
import com.mangalhousemanager.pojo.MenuPojo;
import com.mangalhousemanager.utils.Constants;
import com.mangalhousemanager.utils.RetrofitHelper;
import com.mangalhousemanager.utils.StoreUserData;
import com.mangalhousemanager.utils.Utils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Modifier;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class MenuFragment extends Fragment {

    FragmentMenuBinding binding;
    FragmentActivity activity;
    StoreUserData storeUserData;
    //private ItemClickListener itemClickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_menu, container, false);
        activity = getActivity();
        storeUserData = new StoreUserData(activity);



        binding.rvMenu.setLayoutManager(new LinearLayoutManager(activity));
        binding.rvMenu.setNestedScrollingEnabled(false);
        binding.rvMenu.setHasFixedSize(true);

/*
        itemClickListener = new ItemClickListener() {
            @Override
            public void onClick(String id) {
                //TODO : Menu dialog
            }
        };*/

        menu();
        return binding.getRoot();
    }


    //TODO :GET MENU LIST
    private void menu() {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().getMenu(
                storeUserData.getString(Constants.res_id),
                storeUserData.getString(Constants.token),
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
                    Log.i("MENU_LIST", "onSuccess: " + response);

                    Reader reader = new StringReader(response);
                    Utils.dismissProgress();
                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    MenuPojo pojo = gson.fromJson(reader, MenuPojo.class);

                    if (pojo.status == 1) {
                        binding.rvMenu.setAdapter(new MenuAdapter(activity, pojo.responsedata/*, itemClickListener*/));
                        binding.tvMessage.setVisibility(View.GONE);
                    } else {
                        binding.tvMessage.setVisibility(View.VISIBLE);
                        binding.tvMessage.setText(pojo.message);
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

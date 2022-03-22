package com.mangalhousemanager.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

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
import com.mangalhousemanager.adapter.OrderHistoryAdapter;
import com.mangalhousemanager.databinding.ActivityPastOrderBinding;
import com.mangalhousemanager.databinding.FragmentMenuBinding;
import com.mangalhousemanager.pojo.MenuPojo;
import com.mangalhousemanager.pojo.OrderHistoryPojo;
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

public class OrderHistoryFragment extends Fragment {

    ActivityPastOrderBinding binding;
    FragmentActivity activity;
    StoreUserData storeUserData;
    //private ItemClickListener itemClickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_past_order, container, false);
        activity = getActivity();
        storeUserData = new StoreUserData(activity);

        binding.tvFilterType.setText(Constants.ALL);
        binding.tvCompleted.setText(Constants.COMPLETED);
        binding.tvCompletedAmount.setText(Constants.COMPLETED_AMOUNT);
        binding.tvDeclined.setText(Constants.DECLINE);
        binding.tvDeclinedAmount.setText(Constants.DECLINED_AMOUNT);

        binding.rvPastOrder.setLayoutManager(new LinearLayoutManager(activity));
        binding.rvPastOrder.setNestedScrollingEnabled(false);
        binding.rvPastOrder.setHasFixedSize(true);

        ImageView filterData = getActivity().findViewById(R.id.imgFilter);
        filterData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> filterLIst = new ArrayList<>();
                filterLIst.add(Constants.ALL);
                filterLIst.add(Constants.TODAY);
                filterLIst.add(Constants.YESTERDAY);
                filterLIst.add(Constants.LAST_WEEK);
                filterLIst.add(Constants.CURRENT_MONTH);
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity, R.layout.questionlist, filterLIst);
                builder.setAdapter(dataAdapter, (dialog, which) -> {
                    binding.tvFilterType.setText(filterLIst.get(which));
                    if (which == 0){
                        getOrderHistory("all");
                    }else if (which == 1){
                        getOrderHistory("today");
                    }else if (which == 2){
                        getOrderHistory("yesterday");
                    }else if (which == 3){
                        getOrderHistory("last_week");
                    }else if (which == 4){
                        getOrderHistory("cur_month");
                    }
                    dialog.dismiss();
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        getOrderHistory("all");

        return binding.getRoot();
    }


    //TODO :GET ORDER HISTORY
    private void getOrderHistory(String type) {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().orderHistory(
                storeUserData.getString(Constants.res_id),
                storeUserData.getString(Constants.token),
                "0",
                type,
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
                    Log.i("ORDER_HISTORY", "onSuccess: " + response);

                    Reader reader = new StringReader(response);
                    Utils.dismissProgress();
                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    OrderHistoryPojo pojo = gson.fromJson(reader, OrderHistoryPojo.class);


                    if (pojo.status == 1) {
                        binding.tvMessage.setVisibility(View.GONE);
                        binding.rvPastOrder.setAdapter(new OrderHistoryAdapter(activity, pojo.responsedata.orders/*, itemClickListener*/));
                        binding.rvPastOrder.setVisibility(View.VISIBLE);
                        binding.totalCompleteOrder.setText(pojo.responsedata.completed_total_orders);
                        binding.totalCompleteOrderPrice.setText("€" + pojo.responsedata.completed_total_price);
                        binding.totalDeclinedOrder.setText(pojo.responsedata.declined_total_orders);
                        binding.totalDeclinedOrderPrice.setText("€" + pojo.responsedata.declined_total_price);
                    } else {
                        binding.tvMessage.setVisibility(View.VISIBLE);
                        binding.tvMessage.setText(pojo.message);
                        binding.rvPastOrder.setVisibility(View.GONE);
                        binding.totalCompleteOrder.setText("0");
                        binding.totalCompleteOrderPrice.setText("€0.00");
                        binding.totalDeclinedOrder.setText("0");
                        binding.totalDeclinedOrderPrice.setText("€0.00");
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

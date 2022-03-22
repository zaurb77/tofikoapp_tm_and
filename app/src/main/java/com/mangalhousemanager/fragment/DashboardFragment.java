package com.mangalhousemanager.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mangalhousemanager.R;
import com.mangalhousemanager.adapter.OrderHistoryAdapter;
import com.mangalhousemanager.adapter.OrdersAdapter;
import com.mangalhousemanager.databinding.FragmentDashboardBinding;
import com.mangalhousemanager.databinding.RowCancelReasionBinding;
import com.mangalhousemanager.pojo.OrdersPojo;
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
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    FragmentDashboardBinding binding;
    FragmentActivity activity;
    StoreUserData storeUserData;
    String tabType = "upcoming";
    private ItemClickListener itemClickListener;
    ArrayList<OrdersPojo.ResponseData> data = new ArrayList<>();
    BottomSheetDialog dialog;
    FrameLayout bottomSheet;
    RowCancelReasionBinding rowCancelReasionBinding;
    public String reason = "";
    private Handler apiHandler = new Handler();
    Runnable runnablApiCall = () -> getOrderList(tabType, false);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false);
        activity = getActivity();
        storeUserData = new StoreUserData(activity);


        binding.tvWoops.setText(Constants.WOOPS);

        itemClickListener = new ItemClickListener() {
            @Override
            public void onClick(int orderId, String type) {

                if (type.equalsIgnoreCase("decline")) {

                    dialog = new BottomSheetDialog(activity, R.style.CustomBottomSheetDialogTheme);
                    View deliveryTimeSheetView = getLayoutInflater().inflate(R.layout.row_cancel_reasion, null);
                    dialog.setContentView(deliveryTimeSheetView);
                    dialog.setCancelable(true);
                    rowCancelReasionBinding = DataBindingUtil.bind(deliveryTimeSheetView);
                    bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
                    if (bottomSheet != null) {
                        BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                    if (bottomSheet != null) {
                        BottomSheetBehavior.from(bottomSheet).setSkipCollapsed(true);
                    }
                    if (bottomSheet != null) {
                        BottomSheetBehavior.from(bottomSheet).setHideable(true);
                    }


                    ImageView one = dialog.findViewById(R.id.imgOne);
                    ImageView two = dialog.findViewById(R.id.imgTwo);
                    ImageView three = dialog.findViewById(R.id.imgTree);
                    ImageView four = dialog.findViewById(R.id.imgFour);
                    EditText otherIssue = dialog.findViewById(R.id.otherIssue);


                    LinearLayout llReasonOne = dialog.findViewById(R.id.llReasonOne);
                    Objects.requireNonNull(llReasonOne).setOnClickListener(v -> {
                        Objects.requireNonNull(one).setImageResource(R.drawable.radio_on);
                        Objects.requireNonNull(two).setImageResource(R.drawable.radio_blank);
                        Objects.requireNonNull(three).setImageResource(R.drawable.radio_blank);
                        Objects.requireNonNull(four).setImageResource(R.drawable.radio_blank);
                        Objects.requireNonNull(otherIssue).setVisibility(View.GONE);
                        reason = "Product not available";
                    });


                    LinearLayout llReasonTwo = dialog.findViewById(R.id.llReasonTwo);
                    Objects.requireNonNull(llReasonTwo).setOnClickListener(v -> {
                        Objects.requireNonNull(one).setImageResource(R.drawable.radio_blank);
                        Objects.requireNonNull(two).setImageResource(R.drawable.radio_on);
                        Objects.requireNonNull(three).setImageResource(R.drawable.radio_blank);
                        Objects.requireNonNull(four).setImageResource(R.drawable.radio_blank);
                        Objects.requireNonNull(otherIssue).setVisibility(View.GONE);
                        reason = "Extraordinary closure";

                    });


                    LinearLayout llReasonThree = dialog.findViewById(R.id.llReasonThree);
                    Objects.requireNonNull(llReasonThree).setOnClickListener(v -> {
                        Objects.requireNonNull(one).setImageResource(R.drawable.radio_blank);
                        Objects.requireNonNull(two).setImageResource(R.drawable.radio_blank);
                        Objects.requireNonNull(three).setImageResource(R.drawable.radio_on);
                        Objects.requireNonNull(four).setImageResource(R.drawable.radio_blank);
                        Objects.requireNonNull(otherIssue).setVisibility(View.GONE);
                        reason = "Technical problems";

                    });


                    LinearLayout llReasonFour = dialog.findViewById(R.id.llReasonFour);
                    Objects.requireNonNull(llReasonFour).setOnClickListener(v -> {
                        Objects.requireNonNull(one).setImageResource(R.drawable.radio_blank);
                        Objects.requireNonNull(two).setImageResource(R.drawable.radio_blank);
                        Objects.requireNonNull(three).setImageResource(R.drawable.radio_blank);
                        Objects.requireNonNull(four).setImageResource(R.drawable.radio_on);
                        Objects.requireNonNull(otherIssue).setVisibility(View.VISIBLE);
                        reason = "";
                    });

                    TextView decline = dialog.findViewById(R.id.decline);
                    decline.setOnClickListener(v -> {

                        if (reason.length() > 0) {
                            apiHandler.removeCallbacks(runnablApiCall);
                            changeStatus(orderId, type, reason);
                            dialog.dismiss();
                        } else {
                            if (otherIssue.getText().toString().length() > 0) {
                                apiHandler.removeCallbacks(runnablApiCall);
                                changeStatus(orderId, type, otherIssue.getText().toString());
                                dialog.dismiss();
                            } else {
                                Utils.showTopMessageError(activity, Constants.SELECT_REASON_TO_CANCEL);
                            }
                        }
                    });
                    dialog.show();
                } else {
                    changeStatus(orderId, type, reason);
                }
            }

            @Override
            public void changeTime(String orderTime, String orderId) {
                changeTimeApi(orderTime,orderId);
            }
        };


        binding.rvOrderHistory.setLayoutManager(new LinearLayoutManager(activity));
        binding.rvOrderHistory.setNestedScrollingEnabled(false);
        binding.rvOrderHistory.setHasFixedSize(true);


        binding.tvPending.setText(Constants.UPCOMING);
        binding.tvPrepare.setText(Constants.IN_PREPARE);
        binding.tvCompleted.setText(Constants.DELIVER);

//        changeBg(binding.tvPending, tabType);


        if (Constants.open_upcoming.equalsIgnoreCase( "1" )){
            changeBg(binding.tvPrepare, tabType);
            tabType = "in_prepare";
        }else {
            changeBg(binding.tvPending, tabType);
        }

        binding.llPending.setOnClickListener(view -> {
            tabType = "upcoming";
            changeBg(binding.tvPending, tabType);
        });

        binding.llPrepare.setOnClickListener(view -> {
            tabType = "in_prepare";
            changeBg(binding.tvPrepare, tabType);
            apiHandler.removeCallbacks(runnablApiCall);

        });

        binding.llDelivery.setOnClickListener(view -> {
            tabType = "delivery";
            changeBg(binding.tvCompleted, tabType);
            apiHandler.removeCallbacks(runnablApiCall);
        });


        return binding.getRoot();
    }


    //TODO :GET ORDER HISTORY
    private void getOrderList(String type, boolean callProgress) {
        if (callProgress) {
            Utils.showProgress(activity);
        }

        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().orderList(
                storeUserData.getString(Constants.res_id),
                storeUserData.getString(Constants.token),
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
                    Log.i("ORDER_LIST", "onSuccess: " + response);

                    Reader reader = new StringReader(response);
                    Utils.dismissProgress();
                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    OrdersPojo pojo = gson.fromJson(reader, OrdersPojo.class);

                    if (pojo.status == 1) {
                        binding.tvMessage.setText("");
                        binding.llMessage.setVisibility(View.GONE);
                    } else {
                        binding.tvMessage.setText(pojo.message);
                        binding.llMessage.setVisibility(View.VISIBLE);
                    }

                    binding.rvOrderHistory.setAdapter(new OrdersAdapter(activity, type, pojo.responsedata, itemClickListener));

                    if (type == "upcoming") {
                        apiHandler.postDelayed(runnablApiCall, 10000);
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

    //TODO :GET ORDER STATUS
    private void changeStatus(int orderId, String type, String declin) {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().changeOrderStatus(
                storeUserData.getString(Constants.res_id),
                storeUserData.getString(Constants.token),
                type,
                orderId,
                declin,
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
                    Log.i("ORDER_STATUS", "onSuccess: " + response);

                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("status") == 1) {

                        if (type.equalsIgnoreCase("in_prepare")) {
                            binding.llPrepare.performClick();
                        } else if (type.equalsIgnoreCase("delivery")) {
                            binding.llDelivery.performClick();
                        } else {
                            getOrderList(tabType, false);
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


    private void changeTimeApi(String orderTime,String orderId) {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().changeOrderTime(
                storeUserData.getString(Constants.res_id),
                storeUserData.getString(Constants.token),
                orderTime,
                orderId,
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
                    Log.i("CHANGE_TIME", "CHANGE_TIME: " + response);

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

    public void changeBg(TextView textView, String type) {
        binding.tvPending.setBackground(null);
        binding.tvPrepare.setBackground(null);
        binding.tvCompleted.setBackground(null);
        textView.setBackgroundResource(R.drawable.bottom_line_order);
        getOrderList(type, true);
    }

    public interface ItemClickListener {
        void onClick(int orderId, String type);
        void changeTime(String orderTime, String orderId);
    }

}

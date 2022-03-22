package com.mangalhousemanager.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mangalhousemanager.R;
import com.mangalhousemanager.adapter.OrdersAdapter;
import com.mangalhousemanager.adapter.OrdersDetailsAdapter;
import com.mangalhousemanager.databinding.ActivityOrderDetailBinding;
import com.mangalhousemanager.databinding.RowCancelReasionBinding;
import com.mangalhousemanager.pojo.OrderDetailPojo;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    ActivityOrderDetailBinding binding;
    AppCompatActivity activity;
    StoreUserData storeUserData;
    OrderDetailPojo pojo;
    BottomSheetDialog dialog;
    FrameLayout bottomSheet;
    RowCancelReasionBinding rowCancelReasionBinding;
    String reason = "";

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_order_detail);
        binding.backOrderDetail.setOnClickListener(view -> finish());

        binding.title.setText(Constants.ORDER_DETAIL);
        binding.accept.setText(Constants.ACCEPT);
        binding.decline.setText(Constants.DECLINE);
        binding.declineInPrepare.setText(Constants.DECLINE);
        binding.llDelivery.setText(Constants.COMPLETE);
        binding.llInPrepare.setText(Constants.DELIVER);

        binding.rvOrderDetail.setLayoutManager(new LinearLayoutManager(activity));
        binding.rvOrderDetail.setNestedScrollingEnabled(false);
        binding.rvOrderDetail.setHasFixedSize(true);

        binding.btnCall.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + pojo.responsedata.customer_no));
            startActivity(callIntent);
        });

        binding.accept.setOnClickListener(view ->
                //TODO : ACCEPT ORDER
                new AlertDialog.Builder(activity)
                        .setTitle("Accept Order")
                        .setMessage("Are you sure you want to accept this order?")
                        .setPositiveButton(Constants.YES_LABEL, (dialog, which) -> {
                            changeStatus(pojo.responsedata.order_id, "in_prepare", "");
                        })
                        .setNegativeButton(Constants.NO_LABEL, null)
                        .setIcon(R.mipmap.ic_launcher)
                        .show()
        );

        binding.llDelivery.setOnClickListener(view ->
                //TODO : COMPLETE ORDER
                new AlertDialog.Builder(activity)
                        .setTitle("Complete Order")
                        .setMessage("Are you sure you want to complete this order?")
                        .setPositiveButton(Constants.YES_LABEL, (dialog, which) -> {
                            changeStatus(pojo.responsedata.order_id, "completed", "");
                        })
                        .setNegativeButton(Constants.NO_LABEL, null)
                        .setIcon(R.mipmap.ic_launcher)
                        .show()
        );


        binding.llInPrepare.setOnClickListener(view ->
                //TODO : DELIVERY ORDER
                new AlertDialog.Builder(activity)
                        .setTitle("Deliver Order")
                        .setMessage("Are you sure you want to deliver this order?")
                        .setPositiveButton(Constants.YES_LABEL, (dialog, which) -> {
                            changeStatus(pojo.responsedata.order_id, "delivery", "'");
                        })
                        .setNegativeButton(Constants.NO_LABEL, null)
                        .setIcon(R.mipmap.ic_launcher)
                        .show()

        );



        binding.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                llReasonOne.setOnClickListener(v1 -> {
                    Objects.requireNonNull(one).setImageResource(R.drawable.radio_on);
                    Objects.requireNonNull(two).setImageResource(R.drawable.radio_blank);
                    Objects.requireNonNull(three).setImageResource(R.drawable.radio_blank);
                    Objects.requireNonNull(four).setImageResource(R.drawable.radio_blank);
                    Objects.requireNonNull(otherIssue).setVisibility(View.GONE);
                    reason = "Product not available";
                });


                LinearLayout llReasonTwo = dialog.findViewById(R.id.llReasonTwo);
                llReasonTwo.setOnClickListener(v12 -> {
                    Objects.requireNonNull(one).setImageResource(R.drawable.radio_blank);
                    Objects.requireNonNull(two).setImageResource(R.drawable.radio_on);
                    Objects.requireNonNull(three).setImageResource(R.drawable.radio_blank);
                    Objects.requireNonNull(four).setImageResource(R.drawable.radio_blank);
                    Objects.requireNonNull(otherIssue).setVisibility(View.GONE);
                    reason = "Extraordinary closure";
                });


                LinearLayout llReasonThree = dialog.findViewById(R.id.llReasonThree);

                llReasonThree.setOnClickListener(v13 -> {
                    Objects.requireNonNull(one).setImageResource(R.drawable.radio_blank);
                    Objects.requireNonNull(two).setImageResource(R.drawable.radio_blank);
                    Objects.requireNonNull(three).setImageResource(R.drawable.radio_on);
                    Objects.requireNonNull(four).setImageResource(R.drawable.radio_blank);
                    Objects.requireNonNull(otherIssue).setVisibility(View.GONE);
                    reason = "Technical problems";
                });


                LinearLayout llReasonFour = dialog.findViewById(R.id.llReasonFour);
                llReasonFour.setOnClickListener(v14 -> {
                    Objects.requireNonNull(one).setImageResource(R.drawable.radio_blank);
                    Objects.requireNonNull(two).setImageResource(R.drawable.radio_blank);
                    Objects.requireNonNull(three).setImageResource(R.drawable.radio_blank);
                    Objects.requireNonNull(four).setImageResource(R.drawable.radio_on);
                    Objects.requireNonNull(otherIssue).setVisibility(View.VISIBLE);
                    reason = "";
                });

                TextView decline = dialog.findViewById(R.id.decline);

                decline.setOnClickListener(v15 -> {

                    if (reason.length() > 0) {
                        changeStatus(pojo.responsedata.order_id, "decline", reason);
                        dialog.dismiss();
                    } else {
                        if (otherIssue.getText().toString().length() > 0) {
                            changeStatus(pojo.responsedata.order_id, "decline", otherIssue.getText().toString());
                            dialog.dismiss();
                        } else {
                            Utils.showTopMessageError(activity, Constants.SELECT_REASON_TO_CANCEL);
                        }
                    }
                });

                dialog.show();
            }
        });

        binding.declineInPrepare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                llReasonOne.setOnClickListener(v1 -> {
                    Objects.requireNonNull(one).setImageResource(R.drawable.radio_on);
                    Objects.requireNonNull(two).setImageResource(R.drawable.radio_blank);
                    Objects.requireNonNull(three).setImageResource(R.drawable.radio_blank);
                    Objects.requireNonNull(four).setImageResource(R.drawable.radio_blank);
                    Objects.requireNonNull(otherIssue).setVisibility(View.GONE);
                    reason = "Product not available";
                });


                LinearLayout llReasonTwo = dialog.findViewById(R.id.llReasonTwo);
                llReasonTwo.setOnClickListener(v12 -> {
                    Objects.requireNonNull(one).setImageResource(R.drawable.radio_blank);
                    Objects.requireNonNull(two).setImageResource(R.drawable.radio_on);
                    Objects.requireNonNull(three).setImageResource(R.drawable.radio_blank);
                    Objects.requireNonNull(four).setImageResource(R.drawable.radio_blank);
                    Objects.requireNonNull(otherIssue).setVisibility(View.GONE);
                    reason = "Extraordinary closure";
                });


                LinearLayout llReasonThree = dialog.findViewById(R.id.llReasonThree);

                llReasonThree.setOnClickListener(v13 -> {
                    Objects.requireNonNull(one).setImageResource(R.drawable.radio_blank);
                    Objects.requireNonNull(two).setImageResource(R.drawable.radio_blank);
                    Objects.requireNonNull(three).setImageResource(R.drawable.radio_on);
                    Objects.requireNonNull(four).setImageResource(R.drawable.radio_blank);
                    Objects.requireNonNull(otherIssue).setVisibility(View.GONE);
                    reason = "Technical problems";
                });


                LinearLayout llReasonFour = dialog.findViewById(R.id.llReasonFour);
                llReasonFour.setOnClickListener(v14 -> {
                    Objects.requireNonNull(one).setImageResource(R.drawable.radio_blank);
                    Objects.requireNonNull(two).setImageResource(R.drawable.radio_blank);
                    Objects.requireNonNull(three).setImageResource(R.drawable.radio_blank);
                    Objects.requireNonNull(four).setImageResource(R.drawable.radio_on);
                    Objects.requireNonNull(otherIssue).setVisibility(View.VISIBLE);
                    reason = "";
                });

                TextView decline = dialog.findViewById(R.id.decline);

                decline.setOnClickListener(v15 -> {

                    if (reason.length() > 0) {
                        changeStatus(pojo.responsedata.order_id, "decline", reason);
                        dialog.dismiss();
                    } else {
                        if (otherIssue.getText().toString().length() > 0) {
                            changeStatus(pojo.responsedata.order_id, "decline", otherIssue.getText().toString());
                            dialog.dismiss();
                        } else {
                            Utils.showTopMessageError(activity, Constants.SELECT_REASON_TO_CANCEL);
                        }
                    }
                });

                dialog.show();
            }
        });




        orderDetails(getIntent().getStringExtra("orderId"));

    }


    public String parseDateToddMMyyyy(String time) {

        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd MMM yyyy h:mm a";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }


    //TODO :GET ORDER DETAILS
    private void orderDetails(String orderId) {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().orderDetail(
                storeUserData.getString(Constants.res_id),
                storeUserData.getString(Constants.token),
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
                    Log.i("ORDER_DETAIL", "onSuccess: " + response);


                    Reader reader = new StringReader(response);
                    Utils.dismissProgress();
                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    pojo = gson.fromJson(reader, OrderDetailPojo.class);

                    if (pojo.status == 1) {


                        if (pojo.responsedata.address.length() > 0) {
                            binding.address.setText(Constants.ADDRESS + " : " + pojo.responsedata.address);
                        } else {
                            binding.address.setVisibility(View.GONE);
                        }

                        if (pojo.responsedata.payment_type.equalsIgnoreCase("cod")) {
                            binding.paymentType.setText(Constants.PAYMENT_TYPE + " : " + Constants.CASH_ON_DELIVERY);
                        } else {
                            binding.paymentType.setText(Constants.PAYMENT_TYPE + " : " + pojo.responsedata.payment_type);
                        }

                        if (pojo.responsedata.fidelity_points.equalsIgnoreCase("0")){
                            binding.fidelityPoints.setVisibility(View.GONE);
                        }else {
                            binding.fidelityPoints.setText(Constants.CUSTOMER_RECEIVE_THIS_POINTS1+" "+pojo.responsedata.fidelity_points+" "+Constants.CUSTOMER_RECEIVE_THIS_POINTS2);
                        }



                        binding.total.setText(Constants.ITEM_TOTAL + " : €" + pojo.responsedata.sub_total);
                        binding.orderId.setText(Constants.ORDER_NUMBER + " : #" + pojo.responsedata.order_number);
                        binding.delivery.setText(Constants.DELIVERY + " : €" + pojo.responsedata.delivery_charge);
                        binding.customerName.setText(Constants.CUSTOMER_NAME + " : " + pojo.responsedata.customer_name);
                        binding.customerNumber.setText(Constants.CUSTOMER_NUMBER + " : " + pojo.responsedata.customer_no);
                        binding.payableAmount.setText(Constants.TOTAL_PAYABLE_AMT + " : €" + pojo.responsedata.order_total);
                        binding.itemsText.setText(Constants.ITEMS);


                        if (pojo.responsedata.special_request.length() > 0) {
                            binding.specialNote.setVisibility(View.VISIBLE);
                            binding.specialNote.setText(Constants.SPE_NOTE + ": " + pojo.responsedata.special_request);
                        } else {
                            binding.specialNote.setVisibility(View.GONE);
                        }

                        if (pojo.responsedata.past_order_cnt.equalsIgnoreCase("0")) {
                            binding.pastOrderCount.setText(Constants.NEW_CUSTOMER);
                            binding.pastOrderCount.setTextColor(Color.parseColor("#d50000"));
                        } else {
                            binding.pastOrderCount.setText(Constants.NUMBER_OF_PAST_ORDER + " : " + pojo.responsedata.past_order_cnt);
                            binding.pastOrderCount.setTextColor(Color.parseColor("#000000"));
                        }

                        binding.lastOrderDate.setText(Constants.LAST_ORDER_ON + " : " + parseDateToddMMyyyy(pojo.responsedata.last_order_date));
                        binding.orderTime.setText(Constants.ORDER_TIME + " : " + parseDateToddMMyyyy(pojo.responsedata.order_date));
                        binding.orderType.setText(Constants.ORDER_TYPE + " : " + pojo.responsedata.order_type.substring(0, 1).toUpperCase() + pojo.responsedata.order_type.substring(1).toLowerCase());

                        if (pojo.responsedata.delivery_time.length()>0){
                            binding.deliveryType.setText(Constants.DELIVERY_TYPE + " : ( " + pojo.responsedata.delivery_type.substring(0, 1).toUpperCase() +  pojo.responsedata.delivery_type.substring(1).toLowerCase() +" "+pojo.responsedata.delivery_time+" )");
                        }else {
                            binding.deliveryType.setText(Constants.DELIVERY_TYPE + " : ( " + pojo.responsedata.delivery_type.substring(0, 1).toUpperCase() +  pojo.responsedata.delivery_type.substring(1).toLowerCase()+" )");
                        }


                        if (pojo.responsedata.is_invoice.equalsIgnoreCase("1")){
                            binding.invoiceDetail.setVisibility(View.VISIBLE);
                            binding.invoiceDetail.setText(Constants.INVOICE_DETAIL);
                            binding.invoiceDetailTv.setText(pojo.responsedata.invoice_detail);
                        }else {
                            binding.invoiceDetail.setVisibility(View.GONE);
                            binding.invoiceDetailTv.setVisibility(View.GONE);
                        }


                        binding.rvOrderDetail.setAdapter(new OrdersDetailsAdapter(activity, pojo.responsedata.cart_items/*, itemClickListener*/));

                        if (getIntent().getStringExtra("ORDER_TYPE").equalsIgnoreCase("in_prepare")) {
                            binding.llInPrepareLayout.setVisibility(View.VISIBLE);
                        } else if (getIntent().getStringExtra("ORDER_TYPE").equalsIgnoreCase("delivery")) {
                            binding.llDelivery.setVisibility(View.VISIBLE);
                        } else {
                            binding.llUpcoming.setVisibility(View.VISIBLE);
                        }


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
                        finish();
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

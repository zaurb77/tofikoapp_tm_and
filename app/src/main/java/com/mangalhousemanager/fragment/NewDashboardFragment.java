package com.mangalhousemanager.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mangalhousemanager.R;
import com.mangalhousemanager.activity.MainActivity;
import com.mangalhousemanager.activity.TableBookingActivity;
import com.mangalhousemanager.databinding.FragmentNewDashboardBinding;
import com.mangalhousemanager.pojo.DashBoardPojo;
import com.mangalhousemanager.pojo.LoginPojo;
import com.mangalhousemanager.utils.Constants;
import com.mangalhousemanager.utils.RetrofitHelper;
import com.mangalhousemanager.utils.StoreUserData;
import com.mangalhousemanager.utils.Utils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;


public class NewDashboardFragment extends Fragment {

    FragmentActivity activity;
    FragmentNewDashboardBinding binding;
    StoreUserData storeUserData;
    String selectedDate = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        binding = DataBindingUtil.inflate( inflater, R.layout.fragment_new_dashboard, container, false );
        storeUserData = new StoreUserData( activity );

        binding.tvTableBooking.setText(Constants.TABLE_BOOKING);
        binding.tvNewTitle.setText(Constants.NEW);
        binding.tvAcceptTitle.setText(Constants.ACCEPTED);
        binding.tvArriveTitle.setText(Constants.ARRIVED);
        binding.tvCompletedTitle.setText(Constants.COMPLETED);
        binding.tvRejectTitle.setText(Constants.REJECT);
        binding.tvOrders.setText(Constants.ORDERS);
        binding.tvUpcomingTitle.setText(Constants.UPCOMING);
        binding.tvOngoingTitle.setText(Constants.ONGOING);
        binding.tvCompletedOrderTitle.setText(Constants.COMPLETED);
        binding.tvCompletedAmountTitle.setText(Constants.COMPLETED_AMOUNT);
        binding.tvDeclineTitle.setText(Constants.DECLINE);
        binding.tvDeclineAmountTitle.setText(Constants.DECLINED_AMOUNT);

        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd" );
        selectedDate = formatter.format( today );

        binding.ivBookings.setOnClickListener( v -> startActivity( new Intent(activity , TableBookingActivity.class ).putExtra( "BooingTpe" , "all" ) ) );
        binding.llNew.setOnClickListener( v -> startActivity( new Intent(activity , TableBookingActivity.class ).putExtra( "BooingTpe" , "pending" ) ) );
        binding.llAccept.setOnClickListener( v -> startActivity( new Intent(activity , TableBookingActivity.class ).putExtra( "BooingTpe" , "approved" ) ) );
        binding.llArrive.setOnClickListener( v -> startActivity( new Intent(activity , TableBookingActivity.class ).putExtra( "BooingTpe" , "arrived" ) ) );
        binding.llComplete.setOnClickListener( v -> startActivity( new Intent(activity , TableBookingActivity.class ).putExtra( "BooingTpe" , "left" ) ) );
        binding.llReject.setOnClickListener( v -> startActivity( new Intent(activity , TableBookingActivity.class ).putExtra( "BooingTpe" , "rejected" ) ) );

        binding.ivOrders.setOnClickListener( v -> {
            Constants.open_upcoming = "0";
            MainActivity.linearLayoutOrder.performClick();
        } );

        binding.llUpcoming.setOnClickListener( v -> {
            Constants.open_upcoming = "0";
            MainActivity.linearLayoutOrder.performClick();
        } );

        binding.llOngoing.setOnClickListener( v -> {
            Constants.open_upcoming = "1";
            MainActivity.linearLayoutOrder.performClick();
        } );

        binding.llCompleted.setOnClickListener( v -> { MainActivity.llorderHistory.performClick(); } );
        binding.llDecline.setOnClickListener( v -> { MainActivity.llorderHistory.performClick(); } );
        getDashBoard();
        return binding.getRoot();
    }

    private void getDashBoard() {
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().dashBoard(
                storeUserData.getString(Constants.res_id),
                storeUserData.getString(Constants.token),
                storeUserData.getString(Constants.LANG_ID),
                selectedDate
//                "2021-11-01"
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
                    Log.i("GET_DASHBOARD", "onSuccess: " + response);

                    Reader reader = new StringReader(response);
                    Utils.dismissProgress();
                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers( Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    DashBoardPojo pojo = gson.fromJson(reader, DashBoardPojo.class);

                    if (pojo.status == 1) {
                        binding.tvNew.setText( pojo.responsedata.bookings.pending );
                        binding.tvAccept.setText( pojo.responsedata.bookings.approved );
                        binding.tvArrive.setText( pojo.responsedata.bookings.arrived );
                        binding.tvReject.setText( pojo.responsedata.bookings.rejected );
                        //***************************************************************
                        binding.tvCompleted.setText( pojo.responsedata.bookings.left );



                        binding.tvUpcoming.setText( pojo.responsedata.orders.upcoming );
                        binding.tvOngoing.setText( pojo.responsedata.orders.ongoing );
                        binding.tvCompleteOrder.setText( pojo.responsedata.orders.completed );
                        binding.tvCompletedAmount.setText( pojo.responsedata.orders.completd_amount );
                        binding.tvDecline.setText( pojo.responsedata.orders.declined );
                        binding.tvDeclineAmount.setText( pojo.responsedata.orders.declined_amount );


                    } else {

                    }

                } catch (IOException | NullPointerException | JsonSyntaxException e) {
                    e.printStackTrace();
                    Log.i("GET_DASHBOARD", "onError: " + e.getMessage());
                    Utils.dismissProgress();
                }
            }

            @Override
            public void onError(int code, String error) {
                Utils.dismissProgress();
            }
        });
    }
}
package com.mangalhousemanager.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mangalhousemanager.R;
import com.mangalhousemanager.databinding.ActivityBookingSettingBinding;
import com.mangalhousemanager.pojo.BookingDatesPojo;
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

public class BookingSettingActivity extends AppCompatActivity {

    AppCompatActivity activity;
    ActivityBookingSettingBinding binding;
    StoreUserData storeUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        activity = this;
        binding = DataBindingUtil.setContentView( activity, R.layout.activity_booking_setting );
        storeUserData = new StoreUserData( activity );
        binding.tvTitle.setText( Constants.TBL_BOOKING_SETTING );

        binding.tvHour.setText(      Constants.AVG_MEAL_TIME_HR);
        binding.tvMinute.setText(      Constants.AVG_MEAL_TIME_MIN);
        binding.tvCapacity.setText(      Constants.TOT_SEAT_CAPACITY);

        binding.etHour.setHint(      Constants.ENTER_HR);
        binding.etMinute.setHint(    Constants.ENTER_MIN );
        binding.etCapacity.setHint(  Constants.ENTER_SEAT_CAPACITY   );


        binding.etHour.setText( storeUserData.getString( Constants.hour ) );
        binding.etMinute.setText( storeUserData.getString( Constants.minute ) );
        binding.etCapacity.setText( storeUserData.getString( Constants.capacity ) );

        binding.btnSave.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isEmpty( binding.etHour )) {
                    Utils.showTopMessageError( activity, "Enter Hour" );
                } else if (Utils.isEmpty( binding.etMinute )) {
                    Utils.showTopMessageError( activity, "Enter Minute" );
                } else if (Utils.isEmpty( binding.etCapacity )) {
                    Utils.showTopMessageError( activity, "Enter Capacity" );
                }else {
                    changeBookingSetting();
                }
            }
        } );

    }

    private void changeBookingSetting() {
        Utils.showProgress( activity );
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().changeBookingTime(
                storeUserData.getString( Constants.res_id ),
                storeUserData.getString( Constants.token ),
                binding.etHour.getText().toString(),
                binding.etMinute.getText().toString(),
                binding.etCapacity.getText().toString()
        );

        retrofitHelper.callApi( activity, call, new RetrofitHelper.ConnectionCallBack() {
            @Override
            public void onSuccess(Response<ResponseBody> body) {
                Utils.dismissProgress();
                try {
                    if (body.code() != 200) {
                        Utils.serverError( activity, body.code() );
                        return;
                    }
                    String response = body.body().string();
                    Log.i( "DATE_RESPONSE", "onSuccess: " + response );

                    Reader reader = new StringReader( response );
                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers( Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC )
                            .serializeNulls()
                            .create();

                    BookingDatesPojo pojo = gson.fromJson( reader, BookingDatesPojo.class );

                    if (pojo.status == 1) {
                        storeUserData.setString( Constants.hour, binding.etHour.getText().toString() );
                        storeUserData.setString( Constants.minute, binding.etMinute.getText().toString() );
                        storeUserData.setString( Constants.capacity, binding.etCapacity.getText().toString() );
                        finish();
                    }

                } catch (IOException | NullPointerException | JsonSyntaxException e) {
                    e.printStackTrace();
                    Utils.dismissProgress();
                }
            }

            @Override
            public void onError(int code, String error) {
                Utils.dismissProgress();
            }
        } );
    }
}
package com.mangalhousemanager.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mangalhousemanager.R;
import com.mangalhousemanager.adapter.BookingListAdapter;
import com.mangalhousemanager.databinding.ActivityTableBookingBinding;
import com.mangalhousemanager.databinding.BookingDetailDialogBinding;
import com.mangalhousemanager.databinding.BookingRejectBinding;
import com.mangalhousemanager.databinding.SelectDatePopupBinding;
import com.mangalhousemanager.pojo.BookingDatesPojo;
import com.mangalhousemanager.pojo.BookingListPojo;
import com.mangalhousemanager.utils.Constants;
import com.mangalhousemanager.utils.RetrofitHelper;
import com.mangalhousemanager.utils.StoreUserData;
import com.mangalhousemanager.utils.Utils;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class TableBookingActivity extends AppCompatActivity {

    AppCompatActivity activity;
    ActivityTableBookingBinding binding;
    StoreUserData storeUserData;
    BookingListPojo bookingListPojo;
    String selectedDate, type, slotTime, bookingType;
    Dialog detailDialog, rejectDialog, dateDialog;
    private ArrayList<BookingListPojo.ResponseData> arrayList = new ArrayList<>();
    private ArrayList<String> arrSelectService = new ArrayList<>();
    private ArrayList<String> arrService = new ArrayList<>();
    private ArrayList<String> arrBookingType = new ArrayList<>();
    private ArrayList<String> arrType = new ArrayList<>();
    ItemClickListener itemClickListener;
    BookingDetailDialogBinding popupBinding;
    BookingRejectBinding rejectBinding;
    BookingListAdapter bookingListAdapter;
    SelectDatePopupBinding dateBinding;
    boolean _run = true;

    private String toDate, nextDate, calenderMode = "Month";
    private String todayDate, currentDate;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
            _run = false;
            finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        activity = this;
        binding = DataBindingUtil.setContentView( activity, R.layout.activity_table_booking );
        binding.backOne.setOnClickListener( v -> onBackPressed() );
        storeUserData = new StoreUserData( activity );
        type = "all service";
        slotTime = "all";

        bookingType = getIntent().getStringExtra( "BooingTpe" );

        binding.title.setText( Constants.TABLE_BOOKING );
        binding.tvAllService.setText( Constants.ALL_SERVICE );
        binding.tvNext.setText( Constants.NEXT );
        binding.tvName.setText( Constants.NAME );
        binding.tvStatus.setText( Constants.STATUS );

        detailDialog = new Dialog( activity );
        View dialogBinding = getLayoutInflater().inflate( R.layout.booking_detail_dialog, null );
        detailDialog.setContentView( dialogBinding );
        Objects.requireNonNull( detailDialog.getWindow() ).setLayout( LinearLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT );
        detailDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        popupBinding = DataBindingUtil.bind( dialogBinding );

        popupBinding.tvSpecial.setText( Constants.SPECIAL_REQ + ":" );

        popupBinding.tvSpecial.setText( Constants.SPECIAL_REQ + ":" );
        popupBinding.btnArrived.setText( Constants.ARRIVED );
        popupBinding.btnAccept.setText( Constants.ACCEPT );
        popupBinding.btnReject.setText( Constants.REJECT );
        popupBinding.btnLeft.setText( Constants.LEFT );

        dateDialog = new Dialog( activity );
        View dialogDateBinding = getLayoutInflater().inflate( R.layout.select_date_popup, null );
        dateDialog.setContentView( dialogDateBinding );
        Objects.requireNonNull( dateDialog.getWindow() ).setLayout( LinearLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT );
        dateDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dateBinding = DataBindingUtil.bind( dialogDateBinding );
        dateBinding.calendarViewSingle.addDecorator( new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return true;
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.setDaysDisabled( true );
                //view.addSpan(new DotSpan(10.0f, getResources().getColor( R.color.ColorGrey99 )));
            }
        } );


        binding.tvDate.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateDialog.show();
            }
        } );

        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd" );
        SimpleDateFormat formatter2 = new SimpleDateFormat( "EEE dd MMM" );
        selectedDate = formatter.format( today );
        binding.tvDate.setText( formatter2.format( today ) );

        dateBinding.calendarViewSingle.setOnDateChangedListener( new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                DecimalFormat mFormat = new DecimalFormat( "00" );
                selectedDate = mFormat.format( date.getYear() ) + "-" + mFormat.format( date.getMonth() + 1 ) + "-" + mFormat.format( date.getDay() );
                try {
                    binding.tvDate.setText( formatter2.format( formatter.parse( selectedDate ) ) );
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dateDialog.dismiss();
                getBooking();
            }
        } );

        Log.i( "USER_AUTH     ", storeUserData.getString( Constants.res_id ) + "     " + storeUserData.getString( Constants.token ) + "    " + storeUserData.getString( Constants.LANG_ID ) );

//        storeUserData.getString(Constants.res_id),
//                storeUserData.getString(Constants.token),
//                storeUserData.getString(Constants.storeId),
//                storeUserData.getString(Constants.LANG_ID)

        arrBookingType.add( "All Service" );
        arrBookingType.add( "Lunch" );
        arrBookingType.add( "Dinner" );

        arrType.add( "all service" );
        arrType.add( "lunch" );
        arrType.add( "dinner" );

//        arrSelectService.add( "All" );
//        arrSelectService.add( "Next" );
//        arrSelectService.add( "Current" );
//
//        arrService.add( "all" );
//        arrService.add( "next" );
//        arrService.add( "current" );

        arrSelectService.add( "All" );
        arrSelectService.add( "Pending" );
        arrSelectService.add( "Accepted" );
        arrSelectService.add( "Arrived" );
        arrSelectService.add( "Left" );
        arrSelectService.add( "Rejected" );

        arrService.add( "all" );
        arrService.add( "pending" );
        arrService.add( "approved" );
        arrService.add( "arrived" );
        arrService.add( "left" );
        arrService.add( "rejected" );






        binding.llBookingType.setOnClickListener( v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder( activity );
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>( activity, R.layout.raw_dropdown, arrBookingType );
            builder.setAdapter( dataAdapter, (dialog, which) -> {
                binding.tvAllService.setText( arrBookingType.get( which ) );
                type = arrType.get( which );
                if (arrSelectService.get( which ).equalsIgnoreCase( "All Service" )){
                    binding.tvTime.setText( "12:30 - 22:30" );
                }else if (arrSelectService.get( which ).equalsIgnoreCase( "Lunch" )){
                    binding.tvTime.setText( "12:30 - 15:00" );
                }else if (arrSelectService.get( which ).equalsIgnoreCase( "Dinner" )){
                    binding.tvTime.setText( "18:00 - 22:30" );
                }
                getBooking();
            } );
            AlertDialog dialog = builder.create();
            dialog.show();
        } );

        binding.llFilter.setOnClickListener( v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder( activity );
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>( activity, R.layout.raw_dropdown, arrSelectService );
            builder.setAdapter( dataAdapter, (dialog, which) -> {
                        binding.tvNext.setText( arrSelectService.get( which ) );
                        slotTime = arrService.get( which );
                        getBooking();
                    }
            );
            AlertDialog dialog = builder.create();
            dialog.show();
        } );

        getBooking();

        Date date = Calendar.getInstance().getTime();
        Calendar c = Calendar.getInstance();
        c.setTime( date );
        Calendar cn = Calendar.getInstance();
        SimpleDateFormat formate = new SimpleDateFormat( "dd-MM-yyyy" );
        SimpleDateFormat formatteer = new SimpleDateFormat( "yyyy-MM-dd" );
        currentDate = formate.format( date );
        nextDate = formatteer.format( date );
        todayDate = formatteer.format( date );

        SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd" );
        final String[] formattedDate = {df.format( c.getTime() )};

        binding.ivLeft.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.add( Calendar.DATE, -1 );
                formattedDate[0] = df.format( c.getTime() );
                try {
                    todayDate = df.format( c.getTime() );
                    toDate = df.format( c.getTime() );
                } catch (Exception e) {
                    Log.e( "formattedDateFromString", "Exception in formateDateFromstring(): " + e.getMessage() );
                }
                selectedDate = formattedDate[0];
                try {
                    binding.tvDate.setText( formatter2.format( formatter.parse( selectedDate ) ) );
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                getBooking();
            }
        } );

        binding.ivRight.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.add( Calendar.DATE, 1 );
                formattedDate[0] = df.format( c.getTime() );
                try {
                    todayDate = df.format( c.getTime() );
                    toDate = df.format( c.getTime() );
                } catch (Exception e) {
                    Log.e( "formattedDateFromString", "Exception in formateDateFromstring(): " + e.getMessage() );
                }
                selectedDate = formattedDate[0];
                try {
                    binding.tvDate.setText( formatter2.format( formatter.parse( selectedDate ) ) );
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                getBooking();
            }
        } );

        itemClickListener = new ItemClickListener() {
            @Override
            public void openDialog(BookingListPojo.ResponseData data, int position) {
                popupBinding.btnSubmit.setVisibility( View.GONE );
                popupBinding.etReplay.setVisibility( View.GONE );
                Glide.with( activity ).load( data.customer_image ).circleCrop().into( popupBinding.ivUser );
                popupBinding.tvName.setText( data.customer_name );
                popupBinding.tvContact.setText( data.customer_no );
                popupBinding.tvMail.setText( data.customer_email );
                popupBinding.tvSpecialRequest.setText( data.notes );
                popupBinding.btnCancel.setText( Constants.CANCEL );
                popupBinding.btnPerson.setText( data.guests );
                popupBinding.btnSeat.setText( data.available_capacity );
                popupBinding.tvCancel.setText( Constants.CANCELLATION_NOTE + ":" );
                popupBinding.tvNoOfPerson.setText( Constants.NO_OF_PERSON_COME  );
                popupBinding.tvNoOfSeat.setText( Constants.NO_OF_SEAT_AVAILABLE  );
                popupBinding.tvAvgTime.setText( Constants.AVG_MEAL_TIME  );
                popupBinding.btnReplay.setText( Constants.REPLY  );
                popupBinding.btnSubmit.setText( Constants.SUBMIT  );
                popupBinding.btnAvgTime.setText(bookingListPojo.avg_meal_hour + " hour " +bookingListPojo.avg_meal_hour + " minutes" );



                if(data.notes.length() > 0){
                    popupBinding.btnReplay.setVisibility( View.VISIBLE );
                }else {
                    popupBinding.btnReplay.setVisibility( View.GONE );
                }

                popupBinding.btnReplay.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupBinding.btnReplay.setVisibility( View.GONE );
                        popupBinding.btnSubmit.setVisibility( View.VISIBLE );
                        popupBinding.etReplay.setVisibility( View.VISIBLE );
                    }
                } );

                popupBinding.btnSubmit.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (popupBinding.etReplay.getText().toString().length() > 0){
                            BookingReplay(popupBinding.etReplay.getText().toString() , data.id);
                        }else {

                        }
                    }
                } );

                popupBinding.btnArrived.setVisibility( View.GONE );
                popupBinding.btnAccept.setVisibility( View.GONE );
                popupBinding.btnReject.setVisibility( View.GONE );
                popupBinding.btnLeft.setVisibility( View.GONE );
                popupBinding.tvCancel.setVisibility( View.GONE );
                popupBinding.tvCancelNote.setVisibility( View.GONE );

                if (data.status.equalsIgnoreCase( "pending" )) {
                    popupBinding.btnAccept.setVisibility( View.VISIBLE );
                    popupBinding.btnReject.setVisibility( View.VISIBLE );
                } else if (data.status.equalsIgnoreCase( "approved" )) {
                    popupBinding.btnArrived.setVisibility( View.VISIBLE );
                } else if (data.status.equalsIgnoreCase( "arrived" )) {
                    popupBinding.btnLeft.setVisibility( View.VISIBLE );
                } else if (data.status.equalsIgnoreCase( "reject" )) {
                    popupBinding.tvCancel.setVisibility( View.VISIBLE );
                    popupBinding.tvCancelNote.setVisibility( View.VISIBLE );
                    popupBinding.tvCancelNote.setText( data.reason );
                }

                popupBinding.btnCancel.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        detailDialog.dismiss();
                    }
                } );

                popupBinding.btnAccept.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        detailDialog.dismiss();
                        changeBookingStatus( "approved", "", data.id );
                    }
                } );

                popupBinding.btnArrived.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        detailDialog.dismiss();
                        changeBookingStatus( "arrived", "", data.id );
                    }
                } );

                popupBinding.btnLeft.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        detailDialog.dismiss();
                        changeBookingStatus( "left", "", data.id );
                    }
                } );

                popupBinding.btnReject.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        detailDialog.dismiss();
                        bookingRejectDialog( data.id );
                    }
                } );

                detailDialog.show();
            }
        };

        binding.rvList.setLayoutManager( new LinearLayoutManager( activity ) );
        binding.rvList.setNestedScrollingEnabled( false );
        binding.rvList.setHasFixedSize( true );
        bookingListAdapter = new BookingListAdapter( activity, arrayList, itemClickListener );
        binding.rvList.setAdapter( bookingListAdapter );

        if (this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("bookingDate")) {
            selectedDate = getIntent().getStringExtra( "bookingDate" );
        }

        getBookingDate();
        constantRun();
    }

    public void bookingRejectDialog(String id) {
        rejectDialog = new Dialog( activity );
        View rejectDialogBinding = getLayoutInflater().inflate( R.layout.booking_reject, null );
        rejectDialog.setContentView( rejectDialogBinding );
        Objects.requireNonNull( rejectDialog.getWindow() ).setLayout( LinearLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT );
        rejectDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        rejectBinding = DataBindingUtil.bind( rejectDialogBinding );
        rejectDialog.show();

        rejectBinding.tvName.setText( Constants.REJECT_TABLE_RESERVATION );
        rejectBinding.tvSpecial.setText( Constants.SPECIAL_REQ + ":" );
        rejectBinding.btnReject.setText( Constants.REJECT );
        rejectBinding.etReason.setHint( Constants.WRITE_QUERY_HERE );

        rejectBinding.btnReject.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isEmpty( rejectBinding.etReason )) {
                    Utils.showTopMessageError( activity, Constants.PROVIDE_PASS );
                } else {
                    rejectDialog.dismiss();
                    changeBookingStatus( "reject", rejectBinding.etReason.getText().toString(), id );
                }
            }
        } );

    }

    private void getBooking() {
        _run = false;
        //Utils.showProgress( activity );
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().getBookings(
                storeUserData.getString( Constants.res_id ),
                storeUserData.getString( Constants.token ),
                storeUserData.getString( Constants.LANG_ID ),
                selectedDate,
                type,
                slotTime,
                bookingType,
                "Asia/Kolkata"
        );

        Log.i( "API_DATA_DATA" ,                 storeUserData.getString( Constants.res_id )+ "     " +
                storeUserData.getString( Constants.token )+ "     " +
                storeUserData.getString( Constants.LANG_ID )+ "     " +
                selectedDate+ "     " +
                type+ "     " +
                slotTime+ "     " +
                "Asia/Kolkata" );

        retrofitHelper.callApi( activity, call, new RetrofitHelper.ConnectionCallBack() {
            @Override
            public void onSuccess(Response<ResponseBody> body) {
                _run = true;
                try {
                    if (body.code() != 200) {
                        Utils.serverError( activity, body.code() );
                        return;
                    }
                    String response = body.body().string();
                    Log.i( "LOGIN_RESPONSE", "onSuccess: " + response );

                    Reader reader = new StringReader( response );
                    Utils.dismissProgress();
                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers( Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC )
                            .serializeNulls()
                            .create();

                     bookingListPojo = gson.fromJson( reader, BookingListPojo.class );

                    if (bookingListPojo.status == 1) {
                        arrayList.clear();
                        arrayList.addAll( bookingListPojo.responsedata );
                        bookingListAdapter.notifyDataSetChanged();
                    }else {
                        arrayList.clear();
                        bookingListAdapter.notifyDataSetChanged();
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

    private void changeBookingStatus(String status, String reason, String booking) {
        Utils.showProgress( activity );
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().changeBookingStatus(
                storeUserData.getString( Constants.storeId ),
                storeUserData.getString( Constants.res_id ),
                storeUserData.getString( Constants.token ),
                storeUserData.getString( Constants.LANG_ID ),
                booking,
                status,
                reason
        );

        retrofitHelper.callApi( activity, call, new RetrofitHelper.ConnectionCallBack() {
            @Override
            public void onSuccess(Response<ResponseBody> body) {

                try {
                    if (body.code() != 200) {
                        Utils.serverError( activity, body.code() );
                        return;
                    }
                    String response = body.body().string();
                    Log.i( "LOGIN_RESPONSE", "onSuccess: " + response );

                    Reader reader = new StringReader( response );
                    Utils.dismissProgress();
                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers( Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC )
                            .serializeNulls()
                            .create();

                    BookingListPojo pojo = gson.fromJson( reader, BookingListPojo.class );

                    if (pojo.status == 1) {

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

    private void getBookingDate() {
        //Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().getBookingDates(
                storeUserData.getString( Constants.storeId ),
                storeUserData.getString( Constants.res_id ),
                storeUserData.getString( Constants.token ),
                storeUserData.getString( Constants.LANG_ID )
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
                        setCalendarHoliday( pojo.responsedata.holidays, pojo.responsedata.bookings );
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

    private void constantRun() {
        final Handler handler = new Handler();
        handler.postDelayed( new Runnable() {
            @Override
            public void run() {
                if (_run) {
                    getBooking();
                }
                handler.postDelayed( this, 3000 );
            }
        }, 3000 );
    }

    public void setCalendar(ArrayList<String> arrDateList) {

        dateBinding.calendarViewSingle.setSelectionMode( MaterialCalendarView.SELECTION_MODE_SINGLE );
        dateBinding.calendarViewSingle.addDecorator( new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                String month = (day.getMonth() + 1) + "";
                if (month.length() == 1) {
                    month = "0" + month;
                }

                String date = (day.getDay()) + "";
                if (date.length() == 1) {
                    date = "0" + date;
                }

                String curDate = day.getYear() + "-" + month + "-" + date;
                Log.i( "CURRENT_DATE", curDate );
                return arrDateList.contains( curDate );
            }

            @Override
            public void decorate(DayViewFacade view) {
                try {
//                    view.setSelectionDrawable( activity.getResources().getDrawable( R.drawable.background_enable_date ) );
//                    view.setBackgroundDrawable( activity.getResources().getDrawable( R.drawable.background_enable_date ) );
                    view.setDaysDisabled( false );
                    view.addSpan( new DotSpan( 10.0f, getResources().getColor( R.color.colorPrimary ) ) );
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } );
    }

    public void setCalendarHoliday(ArrayList<String> arrDateList, ArrayList<String> booking) {

        dateBinding.calendarViewSingle.setSelectionMode( MaterialCalendarView.SELECTION_MODE_SINGLE );
        dateBinding.calendarViewSingle.addDecorator( new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                String month = (day.getMonth() + 1) + "";
                if (month.length() == 1) {
                    month = "0" + month;
                }

                String date = (day.getDay()) + "";
                if (date.length() == 1) {
                    date = "0" + date;
                }

                String curDate = day.getYear() + "-" + month + "-" + date;
                Log.i( "CURRENT_DATE", curDate );
                return arrDateList.contains( curDate );
            }

            @Override
            public void decorate(DayViewFacade view) {
                try {
//                    view.setSelectionDrawable( activity.getResources().getDrawable( R.drawable.background_enable_date ) );
//                    view.setBackgroundDrawable( activity.getResources().getDrawable( R.drawable.background_enable_date ) );
                    view.setDaysDisabled( true );
                    view.addSpan( new DotSpan( 10.0f, getResources().getColor( R.color.colorRed ) ) );
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } );
        setCalendar( booking );
    }

    public interface ItemClickListener {
        void openDialog(BookingListPojo.ResponseData data, int position);
    }

    private void BookingReplay(String replay , String bookingId) {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().bookingReplay(
                storeUserData.getString( Constants.res_id ),
                storeUserData.getString( Constants.token ),
                replay,
                bookingId
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
                    detailDialog.dismiss();
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
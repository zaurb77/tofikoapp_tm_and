package com.mangalhousemanager.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mangalhousemanager.R;
import com.mangalhousemanager.databinding.DateDialogBinding;
import com.mangalhousemanager.databinding.FragmentReservationBinding;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.function.ObjIntConsumer;

public class ReservationFragment extends Fragment {

    FragmentActivity activity;
    FragmentReservationBinding binding;
    String tabType = "all",selectedDate;
    private Dialog dateDialog;
    Date date;
    DateDialogBinding timeBinding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reservation, container, false );

        date = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd EEEE");
        selectedDate = formatter.format(date);
        binding.tvCalendar.setText( selectedDate );
        calendarDialog();

        changeBg(binding.tvAll, tabType);

        binding.tvAll.setOnClickListener(view -> {
            tabType = "all";
            changeBg(binding.tvAll, tabType);
        });

        binding.tvMorning.setOnClickListener(view -> {
            tabType = "morning";
            changeBg(binding.tvMorning, tabType);
        });

        binding.tvEvening.setOnClickListener(view -> {
            tabType = "evening";
            changeBg(binding.tvEvening, tabType);
        });

        binding.tvCalendar.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateDialog.show();
            }
        } );

        return binding.getRoot();
    }

    public void changeBg(TextView textView, String type) {
        binding.tvAll.setBackground(null);
        binding.tvMorning.setBackground(null);
        binding.tvEvening.setBackground(null);
        textView.setBackgroundResource(R.drawable.bottom_line_order);
    }

    public void calendarDialog(){
        dateDialog = new Dialog( activity );
        View datedialog = getLayoutInflater().inflate( R.layout.date_dialog, null );
        dateDialog.setContentView( datedialog );
        Objects.requireNonNull( dateDialog.getWindow() ).setLayout( LinearLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT );
        dateDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        timeBinding = DataBindingUtil.bind( datedialog );


        timeBinding.calendarViewSingle.setSelectedDate( Calendar.getInstance().getTime() );

        timeBinding.calendarViewSingle.setOnDateChangedListener( new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                DecimalFormat mFormat = new DecimalFormat( "00" );

                date = date;
                selectedDate = mFormat.format( date.getYear() ) + "-" + mFormat.format(date.getMonth() + 1) + "-" + mFormat.format( date.getDay() );
                setDate( selectedDate );
            }
        } );
    }

    public void  setDate(String date) {
        SimpleDateFormat format1 = new SimpleDateFormat( "yyyy-MM-dd" );
        DateFormat format2 = new SimpleDateFormat( "MMM dd EEEE" );
        try {
            binding.tvCalendar.setText((format2.format( format1.parse( date ) )));
            dateDialog.dismiss();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
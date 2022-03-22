package com.mangalhousemanager.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mangalhousemanager.R;
import com.mangalhousemanager.activity.OrderDetailActivity;
import com.mangalhousemanager.databinding.RowOrderHistoryBinding;
import com.mangalhousemanager.databinding.RowScheduleBinding;
import com.mangalhousemanager.fragment.DashboardFragment;
import com.mangalhousemanager.fragment.ScheduleFragment;
import com.mangalhousemanager.pojo.OpenCloaseTimePojo;
import com.mangalhousemanager.pojo.OrdersPojo;
import com.mangalhousemanager.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;

public class OpenCloseTimeAdapter extends RecyclerView.Adapter<OpenCloseTimeAdapter.ViewHolder> {

    Activity activity;
    int breakTime, selectSwitch = -1;
    private ArrayList<OpenCloaseTimePojo.ResponseData.OpenCloseTime> arrayList;
    private ScheduleFragment.ItemClickListener itemClickListener;

    public OpenCloseTimeAdapter(Activity activity, int breakTime, ArrayList<OpenCloaseTimePojo.ResponseData.OpenCloseTime> arrayList, ScheduleFragment.ItemClickListener itemClickListener) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.breakTime = breakTime;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_schedule, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final OpenCloaseTimePojo.ResponseData.OpenCloseTime pojo = arrayList.get(position);

        String cap = pojo.day.substring(0, 1).toUpperCase() + pojo.day.substring(1);
        holder.binding.dayName.setText(cap);

        holder.binding.opningTime.setText(pojo.open_time);
        holder.binding.closingTime.setText(pojo.close_time);
        holder.binding.openingTimeBreak.setText(pojo.open_time1);
        holder.binding.closingTimeBreak.setText(pojo.close_time1);
        holder.binding.sTime.setText(pojo.open_time + " - " + pojo.close_time);


        holder.binding.imgDown.setOnClickListener(view -> {

            if (holder.binding.layoutMain.getVisibility() == View.VISIBLE) {
                holder.binding.imgDown.setImageResource(R.drawable.down);
                holder.binding.layoutMain.setVisibility(View.GONE);
                holder.binding.llWithBreak.setVisibility(View.GONE);

            } else {
                holder.binding.imgDown.setImageResource(R.drawable.up);
                holder.binding.layoutMain.setVisibility(View.VISIBLE);

                if (breakTime == 0) {
                    holder.binding.llWithBreak.setVisibility(View.VISIBLE);
                } else {
                    holder.binding.llWithBreak.setVisibility(View.GONE);
                }

            }



        });





        if (pojo.isopen == 1) {
            holder.binding.imgSwitch.setImageResource(R.drawable.switchon);
        } else {
            holder.binding.imgSwitch.setImageResource(R.drawable.switchoff);
        }


        holder.binding.imgSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pojo.isopen == 1) {
                    holder.binding.imgSwitch.setImageResource(R.drawable.switchoff);


                    itemClickListener.setOpenCloseTime(
                            holder.binding.opningTime.getText().toString(),
                            holder.binding.closingTime.getText().toString(),
                            holder.binding.openingTimeBreak.getText().toString(),
                            holder.binding.closingTimeBreak.getText().toString(),
                            "0",
                            pojo.day
                    );

                } else {
                    holder.binding.imgSwitch.setImageResource(R.drawable.switchon);

                    itemClickListener.setOpenCloseTime(
                            holder.binding.opningTime.getText().toString(),
                            holder.binding.closingTime.getText().toString(),
                            holder.binding.openingTimeBreak.getText().toString(),
                            holder.binding.closingTimeBreak.getText().toString(),
                            "1",
                            pojo.day
                    );
                }
            }
        });

        /*holder.binding.openTime.setOnClickListener(view -> Utils.selectTime(activity, holder.binding.opningTime));*/

        /*holder.binding.closeTime.setOnClickListener(view -> Utils.selectTime(activity, holder.binding.closingTime));*/


        holder.binding.openTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(activity, (timePicker, selectedHour, selectedMinute) -> {
                    holder.binding.opningTime.setText(selectedHour + ":" + selectedMinute);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                    calendar.set(Calendar.MINUTE, selectedMinute);

                    itemClickListener.setOpenCloseTime(
                            holder.binding.opningTime.getText().toString(),
                            holder.binding.closingTime.getText().toString(),
                            holder.binding.openingTimeBreak.getText().toString(),
                            holder.binding.closingTimeBreak.getText().toString(),
                            ""+pojo.isopen,
                            pojo.day
                    );

                }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true).show(); //is24HourView
            }
        });


        holder.binding.closeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(activity, (timePicker, selectedHour, selectedMinute) -> {
                    holder.binding.closingTime.setText(selectedHour + ":" + selectedMinute);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                    calendar.set(Calendar.MINUTE, selectedMinute);

                    itemClickListener.setOpenCloseTime(
                            holder.binding.opningTime.getText().toString(),
                            holder.binding.closingTime.getText().toString(),
                            holder.binding.openingTimeBreak.getText().toString(),
                            holder.binding.closingTimeBreak.getText().toString(),
                            ""+pojo.isopen,
                            pojo.day
                    );

                }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true).show(); //is24HourView
            }
        });



    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowScheduleBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
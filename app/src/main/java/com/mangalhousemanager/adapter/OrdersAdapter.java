package com.mangalhousemanager.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mangalhousemanager.R;
import com.mangalhousemanager.activity.OrderDetailActivity;
import com.mangalhousemanager.databinding.RowOrderHistoryBinding;
import com.mangalhousemanager.fragment.DashboardFragment;
import com.mangalhousemanager.pojo.OrdersPojo;
import com.mangalhousemanager.utils.Constants;
import com.mangalhousemanager.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<OrdersPojo.ResponseData> arrayList;
    private DashboardFragment.ItemClickListener itemClickListener;
    private String type;

    public OrdersAdapter(Activity activity, String type, ArrayList<OrdersPojo.ResponseData> arrayList, DashboardFragment.ItemClickListener itemClickListener) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.itemClickListener = itemClickListener;
        this.type = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_order_history, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final OrdersPojo.ResponseData pojo = arrayList.get(position);

        holder.binding.orderId.setText("#" + pojo.order_number);
        holder.binding.customerName.setText(Constants.NAME + " : " + pojo.cust_name);
        holder.binding.customerNumber.setText(Constants.PHONE_NUMBER + " : " + pojo.cust_no);
        holder.binding.price.setText(Constants.PRICE + " : €" + pojo.price);

        if (pojo.address.length() > 0) {
            String upperString = Constants.ADDRESS.substring(0, 1).toUpperCase() + Constants.ADDRESS.substring(1).toLowerCase();
            holder.binding.address.setText(upperString + ": " + pojo.address);
            holder.binding.address.setVisibility(View.VISIBLE);
        } else {
            holder.binding.address.setVisibility(View.GONE);
        }

        if (pojo.delivery_type.equalsIgnoreCase("Later")) {

            if (type.equalsIgnoreCase("upcoming")) {
                holder.binding.editTime.setVisibility(View.VISIBLE);
            } else {
                holder.binding.editTime.setVisibility(View.GONE);
            }
            holder.binding.orderType.setText(Constants.DELIVERY_TYPE + " : " + pojo.order_drop_type.substring(0, 1).toUpperCase() + pojo.order_drop_type.substring(1).toLowerCase() + " ( "+Constants.LATER+": " + pojo.delivery_time + " )");

        } else if (pojo.delivery_type.equalsIgnoreCase("Now")) {

            if (type.equalsIgnoreCase("upcoming")) {
                holder.binding.editTime.setVisibility(View.VISIBLE);
            } else {
                holder.binding.editTime.setVisibility(View.GONE);
            }

            if (pojo.delivery_time.length() > 0) {
                holder.binding.orderType.setText(Constants.DELIVERY_TYPE + " : " + pojo.order_drop_type.substring(0, 1).toUpperCase() + pojo.order_drop_type.substring(1).toLowerCase() + " ( "+Constants.NOW+": " + pojo.delivery_time + " )");
            } else {
                holder.binding.orderType.setText(Constants.DELIVERY_TYPE + " : " + pojo.order_drop_type.substring(0, 1).toUpperCase() + pojo.order_drop_type.substring(1).toLowerCase() + " ( "+Constants.NOW+")");
            }
        } else {


            holder.binding.editTime.setVisibility(View.GONE);
        }

        holder.binding.editTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pojo.delivery_time.length() > 0) {
                    selectItem(pojo.delivery_time, "" + pojo.order_id);
                } else {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat deeet = new SimpleDateFormat("kk:mm");
                    selectItem(" " + deeet.format(calendar.getTime()), "" + pojo.order_id);
                }
            }
        });


        //change_order_time // res_id,auth_)token ,order_time,order_id


        holder.itemView.setOnClickListener(view -> activity.startActivity(new Intent(activity, OrderDetailActivity.class)
                .putExtra("orderId", "" + pojo.order_id)
                .putExtra("ORDER_TYPE", "" + pojo.order_type)

        ));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowOrderHistoryBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }


    public void selectItem(String time, String orderId) {

        ArrayList<String> arrayList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat formatter1 = new SimpleDateFormat("dd/M/yyyy hh:mm");

        Calendar calendar1 = Calendar.getInstance();
        String currentDate = formatter1.format(calendar1.getTime());
        String[] arrCurrentDate = currentDate.split(" ");
        String dtStart = arrCurrentDate[0] + " " + time + "";


        Date openDate;
        int all = 0;
        Long t = Long.valueOf(0);
        try {
            Date curDate = formatter1.parse(currentDate);
            openDate = formatter1.parse(dtStart);
            if (curDate.compareTo(openDate) >= 0) {
                long duration = curDate.getTime() - openDate.getTime();
                long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
                Log.i("diffInMin", diffInMinutes + "");
                if (diffInMinutes >= 40) {
                    all = 0;
                    t = calendar.getTimeInMillis();
                } else {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(openDate);
                    cal.add(Calendar.MINUTE, 40);
                    t = cal.getTimeInMillis();
                }
            } else {
                Calendar cal = Calendar.getInstance();
                cal.setTime(openDate);
                cal.add(Calendar.MINUTE, 40);
                t = cal.getTimeInMillis();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat dt = new SimpleDateFormat("hh:mm");
        Date dt2 = null;
        try {
            dt2 = dt.parse(Objects.requireNonNull(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar.setTimeInMillis(Objects.requireNonNull(dt2).getTime());

        long timeInMin = TimeUnit.MILLISECONDS.toMinutes(t);
        for (int i = 0; i < 5; i++) {
            if (timeInMin % 5 == 0) {
                break;
            }
            timeInMin += 1;
        }
        long t1 = TimeUnit.MINUTES.toMillis(timeInMin);

        for (int i = 0; i < 12; i++) {
            Date afterAddingTenMins = new Date(t1 + all * 60000);
            SimpleDateFormat sdf = new SimpleDateFormat("kk:mm");
            String time1 = sdf.format(afterAddingTenMins);
            arrayList.add(time1);
            all += 5;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity, R.layout.questionlist, arrayList);
        builder.setAdapter(dataAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemClickListener.changeTime(arrayList.get(which), orderId);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
package com.mangalhousemanager.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mangalhousemanager.R;
import com.mangalhousemanager.activity.OrderDetailActivity;
import com.mangalhousemanager.activity.ProductListActivity;
import com.mangalhousemanager.databinding.RowMenuBinding;
import com.mangalhousemanager.databinding.RowPastOrderBinding;
import com.mangalhousemanager.fragment.ReviewFragment;
import com.mangalhousemanager.pojo.MenuPojo;
import com.mangalhousemanager.pojo.OrderHistoryPojo;
import com.mangalhousemanager.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<OrderHistoryPojo.ResponseData.Orders> arrayList;
    private ReviewFragment.ItemClickListener itemClickListener;

    public OrderHistoryAdapter(Activity activity, ArrayList<OrderHistoryPojo.ResponseData.Orders> arrayList/*, ReviewFragment.ItemClickListener itemClickListener*/) {
        this.activity = activity;
        this.arrayList = arrayList;
        //this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_past_order, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final OrderHistoryPojo.ResponseData.Orders pojo = arrayList.get(position);

        holder.binding.orderId.setText("#"+pojo.order_number);
        holder.binding.orderDate.setText(Constants.ORDER_TIME+" : "+parseDateToddMMyyyy(pojo.order_date));
        holder.binding.customerName.setText(Constants.NAME+" : "+pojo.cust_name);
        holder.binding.customerNumber.setText(Constants.PHONE_NUMBER+" : "+pojo.cust_no);
        holder.binding.price.setText("€"+pojo.price);


        if (pojo.address.length()>0){
            holder.binding.address.setText(Constants.ADDRESS+" : "+pojo.address);
            holder.binding.address.setVisibility(View.VISIBLE);
        }else {
            holder.binding.address.setVisibility(View.GONE);
        }

        if (pojo.cancel_note.length()>0){
            holder.binding.cancelNote.setVisibility(View.VISIBLE);
            holder.binding.cancelNote.setText(Constants.CANCEL_NOTE+" : "+pojo.cancel_note);
        }else {
            holder.binding.cancelNote.setVisibility(View.GONE);
        }

        if (pojo.order_type.equalsIgnoreCase("decline")){
            holder.binding.status.setText(Constants.DECLINE);
            holder.binding.status.setBackgroundResource(R.drawable.round_corner_status_cancelled);
        }else if (pojo.order_type.equalsIgnoreCase("completed")){
            holder.binding.status.setText(Constants.ACCEPTED);
            holder.binding.status.setBackgroundResource(R.drawable.round_corner_status_cpmplete);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, OrderDetailActivity.class)
                .putExtra("orderId",""+pojo.order_id)
                );
            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowPastOrderBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
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
}
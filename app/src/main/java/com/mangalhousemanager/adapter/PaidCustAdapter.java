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
import com.mangalhousemanager.activity.ProductListActivity;
import com.mangalhousemanager.databinding.RowCustomizationBinding;
import com.mangalhousemanager.databinding.RowMenuBinding;
import com.mangalhousemanager.fragment.ReviewFragment;
import com.mangalhousemanager.pojo.CustonizationPojo;
import com.mangalhousemanager.pojo.MenuPojo;

import java.util.ArrayList;

public class PaidCustAdapter extends RecyclerView.Adapter<PaidCustAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<CustonizationPojo.ResponseData.PaidCust> arrayList;
    private ReviewFragment.ItemClickListener itemClickListener;

    public PaidCustAdapter(Activity activity, ArrayList<CustonizationPojo.ResponseData.PaidCust> arrayList/*, ReviewFragment.ItemClickListener itemClickListener*/) {
        this.activity = activity;
        this.arrayList = arrayList;
        //this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_customization, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CustonizationPojo.ResponseData.PaidCust pojo = arrayList.get(position);
        holder.binding.priceCustomization.setVisibility(View.VISIBLE);

        holder.binding.custName.setText(pojo.name);

        if (!pojo.price.equalsIgnoreCase("")){
            holder.binding.priceCustomization.setText("€"+pojo.price);
        }else {
            holder.binding.priceCustomization.setText("");
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowCustomizationBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
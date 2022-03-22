package com.mangalhousemanager.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mangalhousemanager.R;
import com.mangalhousemanager.databinding.RowOrderDetailBinding;
import com.mangalhousemanager.pojo.OrderDetailPojo;
import com.mangalhousemanager.utils.Constants;

import java.util.ArrayList;

public class OrdersDetailsAdapter extends RecyclerView.Adapter<OrdersDetailsAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<OrderDetailPojo.ResponseData.CartItems> arrayList;
    //private DashboardFragment.ItemClickListener itemClickListener;

    public OrdersDetailsAdapter(Activity activity, ArrayList<OrderDetailPojo.ResponseData.CartItems> arrayList/*, DashboardFragment.ItemClickListener itemClickListener*/) {
        this.activity = activity;
        this.arrayList = arrayList;
        //this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_order_detail, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final OrderDetailPojo.ResponseData.CartItems pojo = arrayList.get(position);

        holder.binding.itemName.setText(pojo.item_name);
        holder.binding.itemPrice.setText("€"+pojo.price);
        holder.binding.itemQty.setText(pojo.quantity);
        holder.binding.itemTotal.setText("€" + pojo.item_total_price);

        holder.binding.tvCookingLevel.setText(Constants.COOKING_LEVEL);
        holder.binding.tvTaste.setText(Constants.TASTE);

        holder.binding.taste.setText(pojo.taste_customization);
        holder.binding.cookingLevel.setText(pojo.cooking_customization);


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowOrderDetailBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
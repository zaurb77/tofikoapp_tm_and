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
import com.mangalhousemanager.databinding.RowMenuBinding;
import com.mangalhousemanager.fragment.ReviewFragment;
import com.mangalhousemanager.pojo.MenuPojo;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<MenuPojo.ResponseData> arrayList;
    private ReviewFragment.ItemClickListener itemClickListener;

    public MenuAdapter(Activity activity, ArrayList<MenuPojo.ResponseData> arrayList/*, ReviewFragment.ItemClickListener itemClickListener*/) {
        this.activity = activity;
        this.arrayList = arrayList;
        //this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_menu, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MenuPojo.ResponseData pojo = arrayList.get(position);

        Glide.with(activity)
                .load(pojo.image)
                .placeholder(R.drawable.notfound)
                .error(R.drawable.notfound)
                .into(holder.binding.img);

        holder.binding.itemName.setText(pojo.name);

        holder.itemView.setOnClickListener(view ->
                activity.startActivity(new Intent(activity, ProductListActivity.class)
                .putExtra("MENU_ITEM_ID", pojo.id)
        ));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowMenuBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
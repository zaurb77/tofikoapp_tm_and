package com.mangalhousemanager.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mangalhousemanager.R;
import com.mangalhousemanager.activity.AddToCartActivity;
import com.mangalhousemanager.activity.ProductListActivity;
import com.mangalhousemanager.databinding.RowMenuBinding;
import com.mangalhousemanager.databinding.RowProductListBinding;
import com.mangalhousemanager.fragment.ReviewFragment;
import com.mangalhousemanager.pojo.ItemListPojo;
import com.mangalhousemanager.pojo.MenuPojo;
import com.mangalhousemanager.utils.Constants;

import java.util.ArrayList;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<ItemListPojo.ResponseData.Items> arrayList;
    private ProductListActivity.ItemClickListener itemClickListener;


    public ProductListAdapter(Activity activity, ArrayList<ItemListPojo.ResponseData.Items> arrayList, ProductListActivity.ItemClickListener itemClickListener) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_product_list, parent, false);
        return new ViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ItemListPojo.ResponseData.Items pojo = arrayList.get(position);

        holder.binding.itemName.setText(pojo.name);
        holder.binding.price.setText("€"+pojo.price);

        if (pojo.image_enable.equalsIgnoreCase("1")){
            holder.binding.img.setVisibility(View.VISIBLE);
        }else {
            holder.binding.img.setVisibility(View.GONE);
        }

        Glide.with(activity)
                .load(pojo.image)
                .into(holder.binding.img);

        if (pojo.ingredients.length() > 0){
            holder.binding.description.setVisibility(View.VISIBLE);
            holder.binding.description.setText("Ingredients : "+Html.fromHtml(pojo.ingredients, Html.FROM_HTML_MODE_COMPACT));
        }else {
            holder.binding.description.setVisibility(View.GONE);
        }

        if (pojo.status == 1){
            holder.binding.switchStatus.setImageResource(R.drawable.switchon);
        }else {
            holder.binding.switchStatus.setImageResource(R.drawable.switchoff);
        }

        holder.binding.switchStatus.setOnClickListener(view -> {
            if (pojo.status == 1){
                itemClickListener.onClick(pojo.id,"0");
            }else {
                itemClickListener.onClick(pojo.id,"1");
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, AddToCartActivity.class)
                        .putExtra("productId",pojo.id)
                );
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowProductListBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
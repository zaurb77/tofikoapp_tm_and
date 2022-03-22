package com.mangalhousemanager.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mangalhousemanager.R;
import com.mangalhousemanager.activity.OrderDetailActivity;
import com.mangalhousemanager.databinding.RowLanguageBinding;
import com.mangalhousemanager.databinding.RowOrderDetailBinding;
import com.mangalhousemanager.fragment.DashboardFragment;
import com.mangalhousemanager.fragment.SettingFragmenty;
import com.mangalhousemanager.pojo.LanguagePojo;
import com.mangalhousemanager.pojo.OrderDetailPojo;
import com.mangalhousemanager.utils.Constants;
import com.mangalhousemanager.utils.StoreUserData;

import java.util.ArrayList;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<LanguagePojo.ResponseData> arrayList;
    int pos = -1;
    private SettingFragmenty.ItemClickListener itemClickListener;

    public LanguageAdapter(Activity activity, ArrayList<LanguagePojo.ResponseData> arrayList, SettingFragmenty.ItemClickListener itemClickListener) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_language, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final LanguagePojo.ResponseData pojo = arrayList.get(position);

        String upperString = pojo.full_name.substring(0, 1).toUpperCase() + pojo.full_name.substring(1).toLowerCase();
        holder.binding.tvLanguage.setText(upperString);

        if (pos == -1) {
            if (pojo.is_selected == 1) {
                holder.binding.checkBox.setImageResource(R.drawable.selected);
            } else {
                holder.binding.checkBox.setImageResource(R.drawable.unselected);
            }
        } else {
            if (pos == position) {
                holder.binding.checkBox.setImageResource(R.drawable.selected);
            } else {
                holder.binding.checkBox.setImageResource(R.drawable.unselected);
            }
        }


        holder.itemView.setOnClickListener(view -> {
            pos = position;
            notifyDataSetChanged();
            itemClickListener.onClick("" + pojo.id);
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowLanguageBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
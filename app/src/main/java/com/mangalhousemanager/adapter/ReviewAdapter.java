package com.mangalhousemanager.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mangalhousemanager.R;
import com.mangalhousemanager.databinding.RowReviewBinding;
import com.mangalhousemanager.fragment.ReviewFragment;
import com.mangalhousemanager.pojo.ReviewPojo;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<ReviewPojo.ResponseData> arrayList;
    private ReviewFragment.ItemClickListener itemClickListener;


    public ReviewAdapter(Activity activity, ArrayList<ReviewPojo.ResponseData> arrayList, ReviewFragment.ItemClickListener itemClickListener) {
        this.activity = activity;
        this.arrayList = arrayList;
         this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_review, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ReviewPojo.ResponseData pojo = arrayList.get(position);

        if (pojo.reply_text.length()>0){
            holder.binding.replay.setVisibility(View.GONE);
            holder.binding.commentAns.setVisibility(View.VISIBLE);
        }else {
            holder.binding.replay.setVisibility(View.VISIBLE);
            holder.binding.commentAns.setVisibility(View.GONE);
        }

        holder.binding.comment.setText(pojo.review_text);
        holder.binding.commentAns.setText("-"+pojo.reply_text);
        holder.binding.name.setText(pojo.user_name);
        holder.binding.dateTv.setText(pojo.added_date);
        holder.binding.rating.setRating(Float.parseFloat(pojo.rating));

        holder.binding.replay.setOnClickListener(view -> itemClickListener.onClick(pojo.id));

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowReviewBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
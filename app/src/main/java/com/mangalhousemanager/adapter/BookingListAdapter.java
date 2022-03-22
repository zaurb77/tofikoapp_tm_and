package com.mangalhousemanager.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mangalhousemanager.R;
import com.mangalhousemanager.activity.TableBookingActivity;
import com.mangalhousemanager.databinding.RowBookingListBinding;
import com.mangalhousemanager.databinding.RowReviewBinding;
import com.mangalhousemanager.fragment.ReviewFragment;
import com.mangalhousemanager.pojo.BookingListPojo;
import com.mangalhousemanager.pojo.LoginPojo;
import com.mangalhousemanager.pojo.ReviewPojo;

import java.util.ArrayList;

public class BookingListAdapter extends RecyclerView.Adapter<BookingListAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<BookingListPojo.ResponseData> arrayList;
    private TableBookingActivity.ItemClickListener itemClickListener;

    public BookingListAdapter(AppCompatActivity activity, ArrayList<BookingListPojo.ResponseData> responsedata, TableBookingActivity.ItemClickListener itemClickListener) {
        this.activity = activity;
        this.arrayList = responsedata;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_booking_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final BookingListPojo.ResponseData data = arrayList.get(position);
        holder.binding.tvTime.setText( data.tm );
        holder.binding.tvUser.setText( data.customer_name );
        if (data.notes.length() > 0){
            holder.binding.tvNote.setVisibility( View.VISIBLE );
            holder.binding.tvNote.setText( data.notes );
        }
        holder.binding.tvUser.setText( data.customer_name );
        holder.binding.tvPerson.setText( data.guests );
        holder.binding.tvStatus.setText(  data.status );
        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.openDialog(data , position);
            }
        } );
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowBookingListBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
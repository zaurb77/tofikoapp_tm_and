package com.mangalhousemanager.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.mangalhousemanager.R;
import com.mangalhousemanager.databinding.ActivityTableDetailBinding;

public class TableDetailActivity extends AppCompatActivity {

    AppCompatActivity activity;
    ActivityTableDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        activity = this;
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_table_detail );
    }
}
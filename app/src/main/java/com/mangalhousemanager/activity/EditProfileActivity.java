package com.mangalhousemanager.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.mangalhousemanager.R;
import com.mangalhousemanager.databinding.ActivityLoginBinding;
import com.mangalhousemanager.databinding.ActivityProfileBinding;
import com.mangalhousemanager.utils.Constants;
import com.mangalhousemanager.utils.StoreUserData;
import com.mangalhousemanager.utils.Utils;

public class EditProfileActivity extends AppCompatActivity {


    ActivityProfileBinding binding;
    AppCompatActivity activity;
    StoreUserData storeUserData;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_profile);

        binding.title.setText(Constants.PROFILE);


        binding.managerName.setHint("Manager Name");
        binding.streetNo.setHint("Street No");
        binding.pinCode.setHint(Constants.ZIPCODE);
        binding.province.setHint(Constants.PROVINCE);
        binding.country.setHint(Constants.PROVIDE_COUNTRY);
        binding.website.setHint(Constants.website);
        binding.email.setHint(Constants.EMAIL_ADDRESS);
        binding.changePassword.setText(Constants.CHANGE_PASS);


        binding.changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity,ChangePasswordActivity.class));
            }
        });


        binding.backProfile.setOnClickListener(view -> finish());

        binding.managerName.setText(storeUserData.getString(Constants.name));
        binding.country.setText(storeUserData.getString(Constants.country));
        binding.province.setText(storeUserData.getString(Constants.province));
        binding.pinCode.setText(storeUserData.getString(Constants.zip_code));
        binding.email.setText(storeUserData.getString(Constants.email));
        binding.website.setText(storeUserData.getString(Constants.website));
        binding.streetNo.setText(storeUserData.getString(Constants.street));
        binding.buildingNo.setText(storeUserData.getString(Constants.building_no));

    }
}

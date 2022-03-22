package com.mangalhousemanager.fragment;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.mangalhousemanager.R;
import com.mangalhousemanager.activity.AddPointActivity;
import com.mangalhousemanager.activity.MainActivity;
import com.mangalhousemanager.databinding.LayoutScannerBinding;
import com.mangalhousemanager.utils.Constants;
import com.mangalhousemanager.utils.StoreUserData;

import java.util.Objects;

public class FragmentScanner extends Fragment implements QRCodeReaderView.OnQRCodeReadListener {

    private QRCodeReaderView qrCodeReaderView;
    LayoutScannerBinding binding;
    FragmentActivity activity;
    StoreUserData storeUserData;
    boolean isScanned = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_scanner, container, false);
        activity = getActivity();
        storeUserData = new StoreUserData(activity);

        binding.tvApproch.setText(Constants.APPROACH_QR_CODE);

        qrCodeReaderView = binding.qrdecoderview;
        qrCodeReaderView.setOnQRCodeReadListener(this);
        qrCodeReaderView.setQRDecodingEnabled(true);
        qrCodeReaderView.setAutofocusInterval(1500L);
        qrCodeReaderView.setBackCamera();
        qrCodeReaderView.startCamera();

        return binding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        if (isScanned){
            isScanned = false;
            startActivity(new Intent(activity, AddPointActivity.class).putExtra("code", text).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}

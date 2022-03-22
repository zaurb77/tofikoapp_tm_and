package com.mangalhousemanager.fragment;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mangalhousemanager.R;
import com.mangalhousemanager.adapter.ReviewAdapter;
import com.mangalhousemanager.databinding.RestaurantReviewFragmentBinding;
import com.mangalhousemanager.pojo.ReviewPojo;
import com.mangalhousemanager.utils.Constants;
import com.mangalhousemanager.utils.RetrofitHelper;
import com.mangalhousemanager.utils.StoreUserData;
import com.mangalhousemanager.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Modifier;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ReviewFragment extends Fragment {

    RestaurantReviewFragmentBinding binding;
    FragmentActivity activity;
    StoreUserData storeUserData;
    private ItemClickListener itemClickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.restaurant_review_fragment, container, false);
        activity = getActivity();
        storeUserData = new StoreUserData(activity);

        binding.rvReview.setLayoutManager(new LinearLayoutManager(activity));
        binding.rvReview.setNestedScrollingEnabled(false);
        binding.rvReview.setHasFixedSize(true);


        itemClickListener = new ItemClickListener() {
            @Override
            public void onClick(String id) {
                //TODO : Menu dialog
                ViewGroup viewGroup = activity.findViewById(android.R.id.content);
                View view = LayoutInflater.from(activity).inflate(R.layout.replay_dialog, viewGroup, false);
                view.setBackgroundDrawable(null);
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setView(view);
                AlertDialog alertDialog = builder.create();
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.setCancelable(false);
                alertDialog.show();

                ImageView close = view.findViewById(R.id.close);
                close.setOnClickListener(view1 -> alertDialog.dismiss());
                EditText message = view.findViewById(R.id.messageReply);
                TextView send = view.findViewById(R.id.sendReplay);

                send.setOnClickListener(view12 -> {
                    Utils.hideKB(activity,message);
                    addReplayApi(id, message.getText().toString());
                    alertDialog.dismiss();

                });
            }
        };

        reviews(false);

        return binding.getRoot();
    }

    //TODO :GET REVIEW LIST
    private void reviews(boolean progress) {
        if (!progress) {
            Utils.showProgress(activity);
        }
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().getReview(
                storeUserData.getString(Constants.res_id),
                storeUserData.getString(Constants.token),
                "0",
                storeUserData.getString(Constants.LANG_ID)
        );

        retrofitHelper.callApi(activity, call, new RetrofitHelper.ConnectionCallBack() {
            @Override
            public void onSuccess(Response<ResponseBody> body) {
                Utils.dismissProgress();
                try {
                    if (body.code() != 200) {
                        Utils.serverError(activity, body.code());
                        return;
                    }
                    String response = body.body().string();
                    Log.i("LOGIN_RESPONSE", "onSuccess: " + response);

                    Reader reader = new StringReader(response);
                    Utils.dismissProgress();
                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    ReviewPojo pojo = gson.fromJson(reader, ReviewPojo.class);

                    if (pojo.status == 1) {
                        binding.rvReview.setAdapter(new ReviewAdapter(activity, pojo.responsedata, itemClickListener));
                        binding.tvMessage.setVisibility(View.GONE);
                    } else {
                        binding.tvMessage.setVisibility(View.VISIBLE);
                        binding.tvMessage.setText(pojo.message);
                    }

                } catch (IOException | NullPointerException | JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int code, String error) {
                Utils.dismissProgress();
            }
        });
    }

    //TODO :AddReplay Review
    private void addReplayApi(String id, String message) {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().addReplay(
                storeUserData.getString(Constants.res_id),
                storeUserData.getString(Constants.token),
                id,
                message,
                storeUserData.getString(Constants.LANG_ID)
        );

        retrofitHelper.callApi(activity, call, new RetrofitHelper.ConnectionCallBack() {
            @Override
            public void onSuccess(Response<ResponseBody> body) {
                try {
                    if (body.code() != 200) {
                        Utils.serverError(activity, body.code());
                        return;
                    }
                    String response = body.body().string();
                    Log.i("AddReplay", "onSuccess: " + response);

                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("status") == 0) {
                        Utils.showTopMessageError(activity, jsonObject.getString("message"));
                    } else {
                        reviews(true);
                    }

                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int code, String error) {
                Utils.dismissProgress();
            }
        });
    }

    public interface ItemClickListener {
        void onClick(String revId);
    }
}

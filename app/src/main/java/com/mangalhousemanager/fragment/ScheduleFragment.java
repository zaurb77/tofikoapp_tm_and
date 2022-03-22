package com.mangalhousemanager.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mangalhousemanager.R;
import com.mangalhousemanager.activity.LoginActivity;
import com.mangalhousemanager.adapter.FreeCustAdapter;
import com.mangalhousemanager.adapter.OpenCloseTimeAdapter;
import com.mangalhousemanager.caldroid.roomorama.caldroid.CaldroidFragment;
import com.mangalhousemanager.caldroid.roomorama.caldroid.CaldroidListener;
import com.mangalhousemanager.caldroid.roomorama.caldroid.CalendarHelper;
import com.mangalhousemanager.databinding.ScheduleFragmentBinding;
import com.mangalhousemanager.pojo.CustonizationPojo;
import com.mangalhousemanager.pojo.OpenCloaseTimePojo;
import com.mangalhousemanager.utils.Constants;
import com.mangalhousemanager.utils.OnBackClickListener;
import com.mangalhousemanager.utils.RetrofitHelper;
import com.mangalhousemanager.utils.StoreUserData;
import com.mangalhousemanager.utils.Utils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import hirondelle.date4j.DateTime;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ScheduleFragment extends Fragment implements OnBackClickListener {

    ScheduleFragmentBinding binding;
    Activity activity;
    StoreUserData storeUserData;
    private ItemClickListener itemClickListener;

    private Calendar cal;
    private CaldroidFragment caldroidFragment;

    private ArrayList<String> datesData = new ArrayList<>();

    private ArrayList<Date> selectedDates = new ArrayList<>();
    private ArrayList<Boolean> selectedDatesBoolean = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.schedule_fragment, container, false);
        activity = getActivity();
        storeUserData = new StoreUserData(activity);
        Utils.getDay(activity, Calendar.getInstance().get(Calendar.DAY_OF_WEEK));

        FragmentTransaction t = getChildFragmentManager().beginTransaction();
        binding.tvOne.setText("Holidays in restaurant.");


        caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();



        ArrayList<Date> disabledDates = new ArrayList<Date>();


        final CaldroidListener listener1 = new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                if (selectedDates.size() > 0) {
                    for (int i = 0; i < selectedDates.size(); i++) {
                        if (selectedDates.get(i) == date) {
                            view.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            selectedDates.remove(i);
                        } else {
                            selectedDates.add(date);
                            view.setBackgroundColor(Color.parseColor("#008000"));
                            break;
                        }
                    }
                } else {
                    selectedDates.add(date);
                    view.setBackgroundColor(Color.parseColor("#008000"));
                }
            }

            @Override
            public void onChangeMonth(int month, int year) {
                String text = "month: " + month + " year: " + year;
            }

            @Override
            public void onLongClickDate(Date date, View view) {
                //caldroidFragment.setBackgroundDrawableForDate(primary, date);
            }

            @Override
            public void onCaldroidViewCreated() {
            }

        };

        caldroidFragment.setCaldroidListener(listener1);


        binding.cancel.setOnClickListener(view -> binding.llCalender.setVisibility(View.INVISIBLE));

        binding.submit.setOnClickListener(view -> {
            binding.llCalender.setVisibility(View.INVISIBLE);
            t.remove(caldroidFragment);
        });

        binding.addHoliday.setOnClickListener(view -> {
            binding.llCalender.setVisibility(View.VISIBLE);
            cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            caldroidFragment.setArguments(args);
            t.replace(R.id.container_caldroid, caldroidFragment);
            t.commit();

            //holidayDateUpdate();
        });

        binding.closeCalendar.setOnClickListener(view -> {
            binding.llCalender.setVisibility(View.GONE);
        });


        itemClickListener = new ItemClickListener() {
            @Override
            public void onClick() {

            }

            @Override
            public void setOpenCloseTime(String openTime, String closeTime, String openTime2, String
                    closeTime2, String isOpen, String day) {
                openCloseTime(openTime, closeTime, openTime2, closeTime2, isOpen, day);
            }
        };

        binding.rvTimeList.setLayoutManager(new LinearLayoutManager(activity));
        binding.rvTimeList.setNestedScrollingEnabled(false);
        binding.rvTimeList.setHasFixedSize(true);


        timeList(true);


        return binding.getRoot();
    }


    //TODO :OPEN LOSE TIME LIST
    private void timeList(boolean progress) {
        if (progress) {
            Utils.showProgress(activity);
        }
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().openCloseTime(
                storeUserData.getString(Constants.res_id),
                storeUserData.getString(Constants.token),
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
                    Log.i("TIME_LIST", "onSuccess: " + response);

                    Reader reader = new StringReader(response);
                    Utils.dismissProgress();
                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    OpenCloaseTimePojo pojo = gson.fromJson(reader, OpenCloaseTimePojo.class);

                    if (pojo.status == 1) {

                        datesData.clear();
                        datesData = pojo.responsedata.holiday_dates;

                        binding.rvTimeList.setAdapter(new OpenCloseTimeAdapter(activity, pojo.responsedata.without_break, pojo.responsedata.open_close_time, itemClickListener));

                        if (pojo.responsedata.holiday_dates.size() > 0){
                            for (int i = 0; i < pojo.responsedata.holiday_dates.size(); i++) {

                                LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View orderView = layoutInflater.inflate(R.layout.row_holi_res_data, null);

                                TextView tvLanguage = orderView.findViewById(R.id.holiResTime);
                                ImageView delete = orderView.findViewById(R.id.close);
                                int finalI = i;
                                delete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        datesData.remove(finalI).toString();
                                        holidayDateUpdate(android.text.TextUtils.join(",", datesData));
                                        Log.i("DTA_REMOVE", "" + android.text.TextUtils.join(",", datesData));

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                binding.llHoliRes.removeAllViews();
                                            }
                                        }, 1500);
                                    }
                                });

                                tvLanguage.setText(pojo.responsedata.holiday_dates.get(i).toString());
                                binding.llHoliRes.addView(orderView);

                            }
                        }else {
                            binding.llHoliRes.setVisibility(View.GONE);
                        }


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

    @Override
    public boolean onBackClick() {
        getActivity().getFragmentManager().popBackStack();
        //Objects.requireNonNull(getActivity()).getFragmentManager().popBackStack();
        return true;
    }

    public interface ItemClickListener {
        void onClick();
        void setOpenCloseTime(String openTime, String closeTime, String openTime2, String closeTime2, String isOpen, String Day);

    }


    private void holidayDateUpdate(String dates) {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().addHolidayDates(
                storeUserData.getString(Constants.res_id),
                storeUserData.getString(Constants.token),
                dates,
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
                    Log.i("REMOVE_DATE", "" + response);
                    timeList(false);
                } catch (IOException | NullPointerException | JsonSyntaxException e) {
                    e.printStackTrace();
                    Utils.dismissProgress();
                }
            }

            @Override
            public void onError(int code, String error) {
            }
        });
    }


    private void openCloseTime(String openTime, String closeTime, String openTime2, String closeTime2, String isOpen, String day) {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().setopenCloseTime(
                storeUserData.getString(Constants.res_id),
                storeUserData.getString(Constants.token),
                day,
                openTime,
                closeTime,
                openTime2,
                closeTime2,
                isOpen,
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
                    Log.i("SET_OPEN_CLOSE_TIME", "" + response);


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

}

package com.mangalhousemanager.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.mangalhousemanager.R;

import org.aviran.cookiebar2.CookieBar;

import java.util.ArrayList;
import java.util.Calendar;

import controls.CalenderDialog;
import controls.CustomProgressDialog;

public class Utils {

    static CustomProgressDialog dialog;

    public static void showProgress(Activity activity) {
        dialog = new CustomProgressDialog(activity);
        dialog.show();
    }


    public static void dismissProgress() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        else
            Log.i("Dialog", "already dismissed");
    }

    public void listDialog(AppCompatActivity activity , ArrayList arrayList, TextView textView) {
        textView.setOnClickListener( view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder( activity );
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>( activity, R.layout.raw_dropdown, arrayList );
            builder.setAdapter( dataAdapter, (dialog, which) -> {
                textView.setText( (CharSequence) arrayList.get( which ) );
            } );
            AlertDialog dialog = builder.create();
            dialog.show();
        } );
    }


    public static void serverError(Activity activity, int code) {
        dismissProgress();
        String message = "";
        switch (code) {
            case 400:
                message = "400 - Bad Request";
                break;
            case 401:
                message = "401 - Unauthorized";
                break;
            case 404:
                message = "404 - Not Found";
                break;
            case 500:
                message = "500 - Internal Server Error";
                break;
            default:
                message = "Server error";
        }
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }


    public static void showTopMessageError(Activity activity, String message) {

        CookieBar.build(activity)
                .setTitle("Error")
                .setTitleColor(R.color.white)
                .setIcon( R.drawable.close_cookie_bar)
                .setMessage(message)
                .setBackgroundColor(R.color.appColor)
                .setDuration(2000) // 2 seconds
                .show();

        new Handler().postDelayed(() -> {
            CookieBar.dismiss(activity);
        }, 2000);
    }


    public static void showTopMessageSuccess(Activity activity, String message) {

        CookieBar.build(activity)
                .setTitle("Success")
                .setTitleColor(R.color.white)
                .setIcon(R.drawable.success_cookie_bar)
                .setMessage(message)
                .setBackgroundColor(R.color.appColor)
                .setDuration(2000) // 5 seconds
                .show();
        new Handler().postDelayed(() -> CookieBar.dismiss( activity ),2000 );

    }

    public static void showAlert(final Activity activity, String title,
                                 String message) {
        new AlertDialog.Builder(activity).setTitle(title).setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                }).show();
    }

    public static void showAlert(Activity activity, String message) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("Ok", null)
                .create()
                .show();
    }

    public static void showAlert(final Activity activity, String message, final boolean finish) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (finish) {
                            activity.finish();
                        }
                    }
                })
                .create()
                .show();
    }

    public static void internetAlert(Activity activity) {
        new AlertDialog.Builder(activity)
                .setMessage("Please check internet connection.")
                .setPositiveButton("Ok", null)
                .create()
                .show();
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public static boolean isValidEmail(EditText email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches();
    }

    public static void selectDate(Activity activity, final TextView btnDate) {
        new DatePickerDialog(activity, (view, year, monthOfYear, dayOfMonth) -> {
            btnDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DATE, dayOfMonth);
            //calendar.getTimeInMillis() + "";
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show();
    }

    public static void selectTime(Activity activity, final TextView btnTime) {
        new TimePickerDialog(activity, (timePicker, selectedHour, selectedMinute) -> {

            btnTime.setText(selectedHour + ":" + selectedMinute);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
            calendar.set(Calendar.MINUTE, selectedMinute);
        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true).show(); //is24HourView
    }

    public static boolean isEmpty(View view) {
        if (view instanceof EditText) {
            if (((EditText) view).getText().toString().length() == 0) {
                return true;
            }
        } else if (view instanceof Button) {
            if (((Button) view).getText().toString().length() == 0) {
                return true;
            }
        }else if (view instanceof TextView) {
            if (((TextView) view).getText().toString().length() == 0) {
                return true;
            }
        }
        return false;
    }

    public static void hideKB(Activity activity, View view) {
        //View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public static void getDay(Activity activity,int dayOfWeek) {
        String weekDay = "";
        if (Calendar.MONDAY == dayOfWeek) {
            weekDay = "monday";
        } else if (Calendar.TUESDAY == dayOfWeek) {
            weekDay = "tuesday";
        } else if (Calendar.WEDNESDAY == dayOfWeek) {
            weekDay = "wednesday";
        } else if (Calendar.THURSDAY == dayOfWeek) {
            weekDay = "thursday";
        } else if (Calendar.FRIDAY == dayOfWeek) {
            weekDay = "friday";
        } else if (Calendar.SATURDAY == dayOfWeek) {
            weekDay = "saturday";
        } else if (Calendar.SUNDAY == dayOfWeek) {
            weekDay = "sunday";
        }
        new StoreUserData(activity).setString(Constants.DAY, weekDay);
    }
}
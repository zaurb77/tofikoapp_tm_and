package com.mangalhousemanager.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mangalhousemanager.R;
import com.mangalhousemanager.databinding.ActivitySupportBinding;
import com.mangalhousemanager.pojo.SupportQuePojo;
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
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class SupportActivity extends AppCompatActivity {

    ActivitySupportBinding binding;
    AppCompatActivity activity;
    StoreUserData storeUserData;
    ArrayList<String> questionlist = new ArrayList<>();
    String selectedId;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_support);

        binding.title.setText(Constants.SUPPORT);
        //binding.tvOne.setText(Constants.SUPPORT);
        binding.send.setText(Constants.SEND);

        binding.backSupport.setOnClickListener(view -> finish());
        binding.questionList.setOnClickListener(view -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity, R.layout.questionlist, questionlist);
                    builder.setAdapter(dataAdapter, (dialog, which) -> {
                                binding.llSelect.setText(questionlist.get(which));
                                selectedId = questionlist.get(which);
                            }
                    );
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
        );


        binding.send.setOnClickListener(view -> {
            if (Utils.isEmpty(binding.llSelect)) {
                Utils.showTopMessageError(activity, "Please select your question.");
            } else if (Utils.isEmpty(binding.messageSupport)) {
                Utils.showTopMessageError(activity, "Please select your message.");
            } else {
                addQuestionAns(selectedId);
            }
        });

        supportQuestion();
    }


    //TODO :GET MENU LIST
    private void supportQuestion() {

        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().supportQuestion(
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
                    Log.i("QuestionList", "onSuccess: " + response);

                    Reader reader = new StringReader(response);
                    Utils.dismissProgress();
                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    SupportQuePojo pojo = gson.fromJson(reader, SupportQuePojo.class);

                    if (pojo.status == 1) {
                        questionlist.clear();
                        for (int i = 0; i < pojo.responsedata.size(); i++) {
                            questionlist.add(pojo.responsedata.get(i).question);
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


    //TODO :Add QuestionAns
    private void addQuestionAns(String id) {

        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().addSupportQuestion(
                storeUserData.getString(Constants.res_id),
                storeUserData.getString(Constants.token),
                id,
                binding.messageSupport.getText().toString().trim(),
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
                    Log.i("addQuestion", "onSuccess: " + response);

                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("status") == 1) {
                        binding.llSelect.setText("");
                        selectedId = "";
                        binding.messageSupport.setText("");
                        Toast.makeText(activity, "" + jsonObject.getInt("message"), Toast.LENGTH_SHORT).show();
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

}

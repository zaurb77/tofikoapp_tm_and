package com.mangalhousemanager.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mangalhousemanager.R;
import com.mangalhousemanager.databinding.ActivityAddToCartBinding;
import com.mangalhousemanager.pojo.ProductDetailPojo;
import com.mangalhousemanager.pojo.UserInfoPojo;
import com.mangalhousemanager.utils.Constants;
import com.mangalhousemanager.utils.ImageFilePath;
import com.mangalhousemanager.utils.RetrofitHelper;
import com.mangalhousemanager.utils.StoreUserData;
import com.mangalhousemanager.utils.Utility;
import com.mangalhousemanager.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class AddToCartActivity extends AppCompatActivity {

    ActivityAddToCartBinding binding;
    AppCompatActivity activity;
    StoreUserData storeUserData;
    String itemId;
    private ItemClickListener itemClickListener;
    int hasStoragePermission, hasCameraPermission;
    String imagePath;
    File destination;
    private Bitmap bm;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String image1;
    ArrayList<Bitmap> images = new ArrayList<>();
    ArrayList<File> imagesDestination = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_add_to_cart);
        binding.backOne.setOnClickListener(view -> finish());

        binding.resName.setText(Constants.ITEM_DETAIL);
        binding.favourite.setText(Constants.SHOW_CUST);

        binding.ingredient.setText(Constants.INGREDIENT + " : ");
        binding.allergnes.setText(Constants.ALLERGENS + " : ");

        binding.favourite.setOnClickListener(view -> startActivity(new Intent(activity, CustomizationActivity.class)
                .putExtra("itemID", "" + itemId)));

        binding.resImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        itemDetails();

    }

    private void itemDetails() {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().getItemDetail(
                storeUserData.getString(Constants.res_id),
                storeUserData.getString(Constants.token),
                getIntent().getStringExtra("productId")
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
                    Log.i("getProductDetails", "getProductDetails: " + response);

                    Reader reader = new StringReader(response);
                    Utils.dismissProgress();
                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    ProductDetailPojo pojo = gson.fromJson(reader, ProductDetailPojo.class);

                    if (pojo.status == 1) {

                        itemId = pojo.responsedata.id;

                        binding.title.setText(pojo.responsedata.name);
                        binding.name.setText(pojo.responsedata.category);
                        binding.price.setText("€" + pojo.responsedata.price);

                        if (pojo.responsedata.ingredients.length() > 0) {

                            binding.ingredientDescription.setVisibility(View.VISIBLE);
                            binding.ingredient.setVisibility(View.VISIBLE);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                binding.ingredientDescription.setText(Html.fromHtml(pojo.responsedata.ingredients, Html.FROM_HTML_MODE_COMPACT));
                            }
                        }

                        if (pojo.responsedata.allergens.length() > 0) {

                            binding.allergnesText.setVisibility(View.VISIBLE);
                            binding.allergnes.setVisibility(View.VISIBLE);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                binding.allergnesText.setText(Html.fromHtml(pojo.responsedata.allergens, Html.FROM_HTML_MODE_COMPACT));
                            }
                        }

                        Glide.with(activity)
                                .load(pojo.responsedata.image)
                                .into(binding.backImage);
                    }

                } catch (IOException | NullPointerException | JsonSyntaxException e) {
                    e.printStackTrace();
                    Utils.dismissProgress();
                }
            }

            @Override
            public void onError(int code, String error) {
                Utils.dismissProgress();
            }
        });
    }


    private void uploadImage() {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        String image1;


        /*BitmapDrawable drawable = (BitmapDrawable) binding.backImage.getDrawable();
        Bitmap bitmap = drawable.getBitmap();*/
        image1 = convertImage64_1(bm);

        call = retrofitHelper.api().editItemImage(
                storeUserData.getString(Constants.res_id),
                storeUserData.getString(Constants.token),
                image1,
                getIntent().getStringExtra("productId"),
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
                    Log.i("IMAGE_UPLOADED", "IMAGE_UPLOADED" + response);




                } catch (IOException | NullPointerException | JsonSyntaxException e) {
                    e.printStackTrace();
                    Utils.dismissProgress();
                }
            }

            @Override
            public void onError(int code, String error) {
                Utils.dismissProgress();
            }
        });
    }



    //TODO : SELECT IMAGE FROM CAMERA OR GALLERY
    //Camera and Gallery==>
    private void selectImage() {
        hasStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasStoragePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 11);
        } else {
            final CharSequence[] items = {"Take Photo", "Choose from Gallery"};
            android.app.AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Set a photo");
            builder.setItems(items, (dialog, item) -> {
                boolean result = Utility.checkPermission(activity);
                if (items[item].equals("Take Photo")) {
                    hasCameraPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
                    if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 12);
                    } else {
                        if (result) {
                            cameraIntent();
                        }
                    }
                } else if (items[item].equals("Choose from Gallery")) {
                    if (result) {
                        galleryIntent();
                    }
                }
            });
            builder.show();
        }
    }

    private void cameraIntent() {
        Intent cameraIntent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == SELECT_FILE) {
                if (null == data) {
                    Log.i("data", "null");
                    return;
                }
                String selectedImagePath;
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    selectedImagePath = ImageFilePath.getPath(activity, selectedImageUri);
                    Log.i("Image File Path", "" + selectedImagePath);
                    imagePath = selectedImagePath;
                    destination = new File(imagePath);
                }
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        bm = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;

        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        images.clear();
        images.add(bm);
        imagesDestination.add(destination);
        setImage();


    }

    public void setImage() {
        Glide.with(activity)
                .load(images.get(0))
                .into(binding.backImage);

        uploadImage();
    }

    private void onSelectFromGalleryResult(Intent data) {
        bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        images.clear();
        images.add(bm);
        imagesDestination.add(destination);
        setImage();

    }
    //Camera and Gallery==>



    public String convertImage64_1(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }


    public interface ItemClickListener {
        void onCick(String productId);
    }
}

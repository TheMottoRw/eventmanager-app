package com.app.events.activities.business;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.events.R;
import com.app.events.activities.commons.Signin;
import com.app.events.adapters.commons.GalleryAdapter;
import com.app.events.utils.Helper;
import com.app.events.utils.RequestHandler;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadImages extends Activity {
    ProgressDialog prgDialog;
    private Helper helper;
    String encodedString;
    HashMap params = new HashMap<String, String>();
    String imgPath;
    Bitmap bitmap;
    private static int RESULT_LOAD_IMG = 1;
    private Spinner spnCategory;
    private Button btnImages, btnUpload;
    private ImageView imgUpload;
    private ArrayList dataName = new ArrayList(), dataId = new ArrayList();

    String imageEncoded;
    List<String> imagesEncodedList;
    private GridView gvGallery;
    private GalleryAdapter galleryAdapter;
    List path = new ArrayList();
    private String encodedImage, encodedImage1, encodeImage2;
    private static int STORAGE_PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_images);
        helper = new Helper(UploadImages.this);
        gvGallery = findViewById(R.id.grdView);
        prgDialog = new ProgressDialog(UploadImages.this);


        btnImages = findViewById(R.id.btnImages);
        btnUpload = findViewById(R.id.btnUpload);

        btnImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImagefromGallery(v);
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate product
                if(validateProductDetails())
                    triggerImageUpload();
            }
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
    }

    private boolean validateProductDetails(){
        String message = "";
        boolean isValid = true;
       if(path.size() >3)
            message = "Only 3 Images needed to describe your product";
        if(!message.equals("")){
            isValid = false;
            Snackbar.make(spnCategory,message,Snackbar.LENGTH_LONG).show();
        }
        return isValid;
    }

    public void loadImagefromGallery(View view) {

        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE,STORAGE_PERMISSION_CODE);

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);//= new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        galleryIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        galleryIntent.setType("image/*");

        startActivityForResult(Intent.createChooser(galleryIntent, "Select 3 products image"), RESULT_LOAD_IMG);
    }
    public void checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(UploadImages.this, new String[] { permission }, requestCode);
        }
        else {

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data
                ArrayList<Uri> mArrayUri = new ArrayList<Uri>();

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<String>();
                if (data.getData() != null) {

                    Uri mImageUri = data.getData();
                    Log.d("Image uri", mImageUri.toString());

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    cursor.close();

                    mArrayUri.add(mImageUri);
                    Log.d("Image uri", mArrayUri.toString());
                    galleryAdapter = new GalleryAdapter(UploadImages.this, mArrayUri);
                    gvGallery.setAdapter(galleryAdapter);
                    gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                            .getLayoutParams();
                    mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();

                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();

                            mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();

                            galleryAdapter = new GalleryAdapter(UploadImages.this, mArrayUri);
                            gvGallery.setAdapter(galleryAdapter);
                            gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
                            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                                    .getLayoutParams();
                            mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                        Log.v("LOG_IMAGE_URIs", mArrayUri.toString());

                    }
                }
                //get image path and convert into encoded string
                getPath(mArrayUri);
                uploadImage(gvGallery);
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.d("choose image gallery", e.getMessage());
            Toast.makeText(this, "Something went wrong s "+e.getMessage() , Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //method to get the file path from uri
    public List getPath(ArrayList<Uri> uri) {
        path = new ArrayList();
        for (int i = 0; i < uri.size(); i++) {
            Cursor cursor = getContentResolver().query(uri.get(i), null, null, null, null);
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = getContentResolver().query(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            path.add(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            cursor.close();
        }
        return path;
    }

    // When Upload button is clicked


    public void uploadImage(View v) {
        // When Image is selected from Gallery
        Log.d("Image counts", "Selected Images " + path.size());
        if (path.size() != 0) {
            // prgDialog.setMessage("Converting Image to Binary Data");
            //prgDialog.show();
            // Convert image to String using Base64
            encodeImagetoString();
            // When Image is not selected from Gallery
        } else {
            Toast.makeText(
                    UploadImages.this,
                    "You must select image from gallery before you try to upload",
                    Toast.LENGTH_LONG).show();
        }

    }


    public void encodeImagetoString() {
        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {

            };

            @Override

            protected String doInBackground(Void... params) {
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                for (int i = 0; i < path.size(); i++) {
                    bitmap = BitmapFactory.decodeFile(path.get(i).toString(),
                            options);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                    byte[] byte_arr = stream.toByteArray();
                    if (i == 0) encodedImage = Base64.encodeToString(byte_arr, 0);
                    else if (i == 1) encodedImage1 = Base64.encodeToString(byte_arr, 0);
                    else encodeImage2 = Base64.encodeToString(byte_arr, 0);
                }
                return "";
            }

            @Override

            protected void onPostExecute(String msg) {
                prgDialog.dismiss();
                //Toast.makeText(UploadImages.this,"Image decoded", Toast.LENGTH_SHORT).show();
                if (path.size() !=3){
                    AlertDialog alert = new AlertDialog.Builder(UploadImages.this).create();
                    alert.setMessage("You must select 3 images which describe your product");
                    alert.setButton(DialogInterface.BUTTON_NEGATIVE, "Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                }
            }
        }.execute(null, null, null);
    }

    public void triggerImageUpload() {
        if(helper.isNetworkConnected())
            requestHandlerUpload();
        else Toast.makeText(UploadImages.this,"You don't have internet",Toast.LENGTH_SHORT).show();
    }

    private void requestHandlerUpload(){
        class UploadImage extends AsyncTask<Void,Void,String>{
//            String url = "http://192.168.43.161/test/image.php";
            String url = "http://192.168.43.161:8000/upload/images";
            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(UploadImages.this, "Uploading product...", null,true,true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(UploadImages.this,"Image uploaded successfuls "+s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... pars) {
                HashMap<String, String> params = new HashMap<>();
                params.put("file1", encodedImage);
                params.put("file2", encodedImage1);
                params.put("file3", encodeImage2);
                params.put("user_id", "1");
                Log.d("URLPars",params.toString());
                String result = rh.sendPostRequest(url,params);


                return result;
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Dismiss the progress bar when application is closed
        if (prgDialog != null) {
            prgDialog.dismiss();
        }
    }

}
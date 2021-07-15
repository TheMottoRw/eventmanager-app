package com.app.events.activities.business;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.events.MainActivity;
import com.app.events.R;
import com.app.events.activities.commons.Signin;
import com.app.events.adapters.commons.GalleryAdapter;
import com.app.events.utils.Helper;
import com.app.events.utils.RequestHandler;
import com.app.events.utils.SwAlertHelper;
import com.app.events.utils.Validator;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddEvent extends AppCompatActivity {
    private EditText edtEventName,edtEventType,edtEventBriefDescription,edtReservationAllowed,edtFullAgenda,edtAddress;
    private Button btnKickOffDate,btnKickOffTime,btnKickOnDate,btnKickOnTime,btnBanners,btnNext,btnBack,btnCreate;
    private LinearLayout lnyEventInfo,lnyEventImages;
    private ProgressDialog pgdialog;
    private Helper helper;
    private SwAlertHelper swHelper;
    private Validator validator;
    int mYear,mMonth,mDay,mHour,mMinute;
    /*Image upload variables*/
    String encodedString;
    HashMap params = new HashMap<String, String>();
    String imgPath;
    Bitmap bitmap;
    private static int RESULT_LOAD_IMG = 1;
    private Spinner spnCategory;
    private Button btnImages, btnUpload;
    private ImageView imgUpload;
    private ArrayList dataName = new ArrayList(), dataId = new ArrayList();
    private Toolbar toolbar;
    String imageEncoded;
    List<String> imagesEncodedList;
    private GridView gvGallery;
    private GalleryAdapter galleryAdapter;
    List path = new ArrayList();
    private String encodedImage, encodedImage1, encodeImage2;
    private static int STORAGE_PERMISSION_CODE = 1001;

    /*end image uploading variables*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        helper = new Helper(getApplicationContext());
        swHelper = new SwAlertHelper(AddEvent.this);
        validator = new Validator();
        pgdialog = new ProgressDialog(this);
        pgdialog.setMessage(getString(R.string.senddata));
        initDefault();
    }
    public void initDefault(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name) + " - Add Event" );
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        edtEventName = findViewById(R.id.edtEventName);
        edtEventType = findViewById(R.id.edtEventType);
        edtEventBriefDescription = findViewById(R.id.edtEventBriefDescription);
        edtReservationAllowed = findViewById(R.id.edtReservationAllowed);
        edtFullAgenda = findViewById(R.id.edtFullAgenda);
        btnKickOffDate = findViewById(R.id.btnKickOffDate);
        btnKickOffTime = findViewById(R.id.btnKickOffTime);
        btnKickOnDate = findViewById(R.id.btnCloseDate);
        btnKickOnTime = findViewById(R.id.btnCloseTime);
        edtAddress = findViewById(R.id.edtAddress);
        btnBanners = findViewById(R.id.btnBanners);
        gvGallery = findViewById(R.id.grdView);
        lnyEventInfo = findViewById(R.id.lnyEventInfo);
        btnCreate = findViewById(R.id.btnNext);


        btnBanners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImagefromGallery();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                save(); //without image upload
//                triggerSaveWithImageUpload(); // with image uploading
                validateEventDetails();
            }

        });

        btnKickOffDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(btnKickOffDate);
            }

        });
        btnKickOffTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(btnKickOffTime);
            }
        });
        btnKickOnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(btnKickOnDate);
            }
        });
        btnKickOnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(btnKickOnTime);
            }
        });
    }
    void showDatePicker(Button btn){
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        btn.setText(year + "-" + ((monthOfYear + 1)<10?"0"+(monthOfYear + 1):(monthOfYear + 1)) + "-" + (dayOfMonth<10?"0"+dayOfMonth:dayOfMonth));

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
    void showTimePicker(Button btn){
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        btn.setText((hourOfDay<10?"0"+hourOfDay:hourOfDay) + ":" + (minute<10?"0"+minute:minute));
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }
    /* Preparing image to upload*/


    private boolean validateEventDetails(){
        String message = "";
        boolean isValid = true;
        String eventName =  edtEventName.getText().toString().trim(),
                eventType = edtEventType.getText().toString().trim(),
                brief = edtEventBriefDescription.getText().toString().trim(),
                seat = edtReservationAllowed.getText().toString().trim(),
                address = edtAddress.getText().toString().trim(),
                kikoffdate = btnKickOffDate.getText().toString(),
                kikofftime = btnKickOffTime.getText().toString();

        if(eventName.isEmpty() || eventType.isEmpty() || brief.isEmpty() || seat.isEmpty() || address.isEmpty() || kikoffdate.equals(getString(R.string.kickoffdate)) || kikofftime.equals(getString(R.string.kickofftime))){
            swHelper.failed(getString(R.string.all_field_and_time_required));
        }else if(path.size()<3){
            swHelper.failed(getString(R.string._3_images_required));
            } else{
                triggerSaveWithImageUpload();
            }

        return isValid;
    }

    public void loadImagefromGallery() {

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
            ActivityCompat.requestPermissions(AddEvent.this, new String[] { permission }, requestCode);
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
                    galleryAdapter = new GalleryAdapter(AddEvent.this, mArrayUri);
                    gvGallery.setVisibility(View.VISIBLE);
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

                            galleryAdapter = new GalleryAdapter(AddEvent.this, mArrayUri);
                            gvGallery.setVisibility(View.VISIBLE);
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
                    AddEvent.this,
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
                pgdialog.dismiss();
                //Toast.makeText(AddEvent.this,"Image decoded", Toast.LENGTH_SHORT).show();
                if (path.size() !=3){
                    AlertDialog alert = new AlertDialog.Builder(AddEvent.this).create();
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

    public void triggerSaveWithImageUpload() {
        if(helper.isNetworkConnected())
            requestHandlerUpload();
        else Toast.makeText(AddEvent.this,"You don't have internet",Toast.LENGTH_SHORT).show();
    }

    private void requestHandlerUpload(){
        class UploadImage extends AsyncTask<Void,Void,String>{
            //            String url = "http://192.168.43.161/test/image.php";
            String url = helper.host+"/events/";
            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(AddEvent.this, "Saving event...", null,true,true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONObject obj = new JSONObject(s);
                    edtEventName.setText("");edtEventType.setText("");edtEventBriefDescription.setText("");
                    edtAddress.setText("");
                    btnKickOffDate.setText(getString(R.string.kickoffdate));btnKickOffTime.setText(getString(R.string.kickofftime));
                    btnKickOnDate.setText(getString(R.string.closedate));btnKickOnTime.setText(getString(R.string.closetime));
                    edtReservationAllowed.setText("");
                    pgdialog.dismiss();
                        if(obj.getString("status").equals("ok")){
                            swHelper.success(obj.getString("message"));
                        }else{
                            swHelper.failed(obj.getString("message"));
                        }
                    }catch (JSONException ex){
                    Log.d("jsonerr","Error "+ex.getMessage()+"===="+s);
                }

            }

            @Override
            protected String doInBackground(Void... pars) {
                HashMap<String, String> params = new HashMap<>();
                params.put("file1", encodedImage);
                params.put("file2", encodedImage1);
                params.put("file3", encodeImage2);

                params.put("business_id", helper.getDataValue("id"));
                params.put("event_name", edtEventName.getText().toString().trim());
                params.put("event_type",edtEventType.getText().toString().trim());
                params.put("brief_description", edtEventBriefDescription.getText().toString().trim());
                params.put("full_description", "");//edtFullAgenda.getText().toString().trim());
//                params.put("images", "");
//                params.put("event_kikoff", btnKickOff.getText().toString().trim());
                params.put("event_kikoff", btnKickOffDate.getText().toString()+" "+btnKickOffTime.getText().toString());
                params.put("event_close", btnKickOnDate.getText().toString()+" "+btnKickOnTime.getText());
//                params.put("event_close", btnKickOn.getText().toString().trim());
                params.put("reservation_allowed", edtReservationAllowed.getText().toString().trim());
                params.put("address", edtAddress.getText().toString().trim());
                params.put("user_id", "1");
                Log.d("URLPars",params.toString());
                String result = rh.sendPostRequest(url,params);
                Log.d("Log resp","Resp "+result);


                return result;
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute();
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.business, menu);

        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        searchViewItem.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
        }
        if (id == R.id.logout) {
            helper.logout();
            finish();
            startActivity(new Intent(AddEvent.this, Signin.class));
        }


        return super.onOptionsItemSelected(item);
    }
}
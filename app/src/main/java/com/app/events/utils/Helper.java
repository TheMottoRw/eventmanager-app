package com.app.events.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Helper {
    public String host = "";
    public Context ctx;
    public static String API_KEY = "AIzaSyCoi2dyuerfdG6lv_cdVJAfdEKAW62ml28";
    public Helper(Context context){
        ctx = context;
        host = getHost();
    }
    //    public static String host = "https://www.mbwira.rw/Methode/armory/api/requests";
    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    public void toggleNetworkConnectivityTextView(TextView tv){
        if(isNetworkConnected()) tv.setVisibility(View.GONE);
        else tv.setVisibility(View.VISIBLE);
        tv.refreshDrawableState();
    }
    public void setHost(String host){
        SharedPreferences.Editor sh = this.getEditor().edit();
        sh.putString("host",host);
        sh.apply();
    }
    public String getHost(){
        String local = "http://192.168.43.161/RUT/Methode/api/requests",
                remote = "http://192.168.1.8/RUT/Methode/api/requests";
        return getEditor().getString("host","http://192.168.43.161:8000");
    }

    public void showToast(String message){
        Toast.makeText(ctx,message,Toast.LENGTH_LONG).show();
    }
    public void setSession(String sessid,String name,String phone,String email,String category,String created_at){
        SharedPreferences.Editor sh = this.getEditor().edit();
        sh.putString("id",sessid);
        sh.putString("name",name);
        sh.putString("user_type",category);
        sh.putString("phone",phone);
        sh.putString("email",email);
        sh.putString("created_at",created_at);
        sh.apply();
    }
    public void  logout(){
        SharedPreferences.Editor sh = getEditor().edit();
        sh.remove("id");
        sh.remove("user_type");
        sh.remove("phone");
        sh.remove("email");
        sh.remove("created_at");
        sh.apply();
    }
    public boolean hasSession(){
        return this.getEditor().contains("id");
    }
    public String getDataValue(String parameter){
        SharedPreferences sh = ctx.getSharedPreferences("events",Context.MODE_PRIVATE);
        return sh.getString(parameter,"");
    }
    public SharedPreferences getEditor(){
        SharedPreferences sh = ctx.getSharedPreferences("events",Context.MODE_PRIVATE);
        return sh;
    }
}
package com.app.events.utils;

import android.content.Context;

import com.app.events.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SwAlertHelper {
    Context ctx;
    public SwAlertHelper(Context context){
        ctx = context;
    }
    public void success(String message){

        SweetAlertDialog pDialog = new SweetAlertDialog(ctx, SweetAlertDialog.SUCCESS_TYPE);
            pDialog.setContentText(message);
            pDialog.setConfirmText("Close").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {//send request to approve/reject event
                sweetAlertDialog.dismiss();

            }
        });
        pDialog.show();
    }
    public void failed(String message){
        SweetAlertDialog pDialog = new SweetAlertDialog(ctx, SweetAlertDialog.ERROR_TYPE);
        pDialog.setContentText(message);
        pDialog.setConfirmText("Close").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {//send request to approve/reject event
                sweetAlertDialog.dismiss();

            }
        });
        pDialog.show();
    }

}

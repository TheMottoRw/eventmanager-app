package com.app.events.adapters.business;



import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.events.R;
import com.app.events.utils.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ViewReservationsAdapter extends RecyclerView.Adapter<com.app.events.adapters.business.ViewReservationsAdapter.MyViewHolder> {
    public LinearLayout v;
    public Context ctx;
    public Helper helper;
    private JSONArray mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ViewReservationsAdapter(Context context, JSONArray myDataset) {
        mDataset = myDataset;
        ctx = context;
        helper = new Helper(ctx);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public com.app.events.adapters.business.ViewReservationsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                                              int viewType) {
        // create a new view
        v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_reservations, parent, false);
        com.app.events.adapters.business.ViewReservationsAdapter.MyViewHolder vh = new com.app.events.adapters.business.ViewReservationsAdapter.MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final com.app.events.adapters.business.ViewReservationsAdapter.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
            JSONObject currentObj = mDataset.getJSONObject(position);
            //Toast.makeText(ctx,currentObj.getString("cat_name")+"-"+currentObj.getString("cat_id"),Toast.LENGTH_SHORT).show();
            holder.reservationId.setText(currentObj.getString("id"));
            holder.reservedBy.setText("Reserved by: "+currentObj.getString("reserver_name"));
            holder.reserverPhone.setText(currentObj.getString("reserver_phone"));
            holder.eventReservedOn.setText("Reserved on "+currentObj.getString("created_at").substring(0,16));
            holder.eventStart.setText(ctx.getString(R.string.event_kickoff)+": "+currentObj.getString("event_kikoff"));
            holder.eventEnd.setText(ctx.getString(R.string.closetime)+": "+currentObj.getString("event_close"));
            holder.eventName.setText(" ");
            //set image icons


        } catch (JSONException ex) {

        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView reservationId,eventName,reservedBy,reserverPhone,eventStart,eventEnd,eventBriefDetails,eventReservedOn;
        public ImageView imgCategoryIcon;
        public LinearLayout lnlayout;

        public MyViewHolder(LinearLayout lny) {
            super(lny);
            lnlayout = lny.findViewById(R.id.singleBusinessHolder);
            reservationId = lny.findViewById(R.id.reservationId);
            eventName = lny.findViewById(R.id.eventName);
            reservedBy = lny.findViewById(R.id.reserverName);
            reserverPhone = lny.findViewById(R.id.reserverPhone);
            eventStart = lny.findViewById(R.id.eventStart);
            eventEnd = lny.findViewById(R.id.eventEnd);
            eventReservedOn = lny.findViewById(R.id.eventReservedOn);
            //tvMsg = lny.findViewById(R.id.tvRecyclerDate);
        }
    }
}
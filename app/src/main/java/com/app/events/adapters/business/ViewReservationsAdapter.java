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

import java.text.ParseException;
import java.text.SimpleDateFormat;


public class ViewReservationsAdapter extends RecyclerView.Adapter<ViewReservationsAdapter.MyViewHolder> {
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
    public ViewReservationsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                                              int viewType) {
        // create a new view
        v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_reservations_1, parent, false);
        ViewReservationsAdapter.MyViewHolder vh = new ViewReservationsAdapter.MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final ViewReservationsAdapter.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
            JSONObject currentObj = mDataset.getJSONObject(position);
            //Toast.makeText(ctx,currentObj.getString("cat_name")+"-"+currentObj.getString("cat_id"),Toast.LENGTH_SHORT).show();
            holder.reservationId.setText(currentObj.getString("id"));
            holder.reservedBy.setText(currentObj.getString("reserver_name"));
            holder.reserverPhone.setText(currentObj.getString("reserver_phone"));
            SimpleDateFormat sda = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            SimpleDateFormat sdf = new SimpleDateFormat("E,MMM dd . hh:mm a");
            holder.eventStart.setText(sdf.format(sda.parse(currentObj.getString("event_kikoff"))));
            holder.eventReservedOn.setText("Reserved on "+sdf.format(sda.parse(currentObj.getString("created_at").replaceAll("T"," ").substring(0,16))));
//            holder.eventStart.setText(ctx.getString(R.string.event_kickoff)+": "+currentObj.getString("event_kikoff"));
            holder.eventEnd.setText(ctx.getString(R.string.closetime)+": "+currentObj.getString("event_close"));
            holder.eventName.setText(currentObj.getString("event_name"));
            //set image icons


        } catch (JSONException ex) {

        } catch (ParseException e) {
            e.printStackTrace();
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
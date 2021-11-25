package com.app.events.adapters.standard;



import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.events.R;
import com.app.events.activities.admin.ConfirmEventAction;
import com.app.events.activities.business.ViewReservations;
import com.app.events.activities.standard.ConfirmWatchLater;
import com.app.events.activities.standard.ViewEventAgenda;
import com.app.events.utils.Helper;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;


public class SavedWatchLaterAdapter extends RecyclerView.Adapter<SavedWatchLaterAdapter.MyViewHolder> {
    public LinearLayout v;
    public Context ctx;
    public Helper helper;
    public JSONObject readStatusSymbol = new JSONObject();
    private JSONArray mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public SavedWatchLaterAdapter(Context context, JSONArray myDataset) {
        super();
        mDataset = myDataset;
        ctx = context;
        helper = new Helper(ctx);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SavedWatchLaterAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                                              int viewType) {
        // create a new view
        v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_watch_later, parent, false);
        SavedWatchLaterAdapter.MyViewHolder vh = new SavedWatchLaterAdapter.MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final SavedWatchLaterAdapter.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
            JSONObject currentObj = mDataset.getJSONObject(position);
            //Toast.makeText(ctx,currentObj.getString("cat_name")+"-"+currentObj.getString("cat_id"),Toast.LENGTH_SHORT).show();
            holder.eventId.setText(currentObj.getString("id"));
            holder.eventName.setText(currentObj.getString("event_name"));
            holder.eventType.setText(ctx.getString(R.string.event_type)+": "+ currentObj.getString("event_type"));
            SimpleDateFormat sda = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            SimpleDateFormat sdf = new SimpleDateFormat("E,MMM dd . hh:mm a");
            holder.eventStart.setText(sdf.format(sda.parse(currentObj.getString("event_kikoff"))));
            holder.briefDetails.setText(currentObj.getString("brief_description"));
            holder.eventEnd.setText(ctx.getString(R.string.event_kickon)+": "+ currentObj.getString("event_close"));
            if(!currentObj.has("reserved_seat")){//standard user reservation
                holder.availableSeat.setText("Reserved on "+sdf.format(sda.parse(currentObj.getString("created_at").replace("T"," "))));
            }else {//business owners
                if (currentObj.getInt("reserved_seat") > 0) {
                    holder.availableSeat.setText(currentObj.getString("reserved_seat") + " People joining");
                } else {
                    holder.availableSeat.setText("Only " + currentObj.getString("available_seat") + " remaining seat");
                }
            }

            holder.eventPreparedBy.setText(ctx.getString(R.string.preparedBy)+": "+ currentObj.getString("business_name"));
            //set image icons
            String images = currentObj.getString("images");
            if(!images.isEmpty()){
                String[] imagesArr = images.split(",");
                Glide.with(ctx).load(imagesArr[0])
                        .error(ctx.getDrawable(R.drawable.logo))
                        .centerCrop().into(holder.imgBanners);
            }else{
                Glide.with(ctx).load(ctx.getDrawable(R.drawable.logo))
                        .error(ctx.getDrawable(R.drawable.logo))
                        .centerCrop().into(holder.imgBanners);
            }
            if(helper.hasSession()){
                if(helper.getDataValue("user_type").equals("Admin")){
                    Log.d("Parsed","Id "+currentObj.getString("status"));
                    if(currentObj.getString("status").equals("pending")) {
                        holder.lnyApprovalLayout.setVisibility(View.VISIBLE);

                        //Listen to approve and reject
                        holder.btnApprove.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                            approve funct
                                Intent intent = new Intent(ctx, ConfirmEventAction.class);
                                intent.putExtra("action", "approve");
                                intent.putExtra("status", "approved");
                                try {
                                    intent.putExtra("id", currentObj.getString("id"));
                                    intent.putExtra("event_name", currentObj.getString("event_name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                ctx.startActivity(intent);
                            }
                        });
                        holder.btnReject.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ctx, ConfirmEventAction.class);
                                intent.putExtra("action", "reject");
                                intent.putExtra("status", "rejected");
                                try {
                                    intent.putExtra("id", currentObj.getString("id"));
                                    intent.putExtra("event_name", currentObj.getString("event_name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                ctx.startActivity(intent);
                            }
                        });
                    }
                }else{
                    holder.lnyApprovalLayout.setVisibility(View.GONE);
                }
                if(helper.getDataValue("user_type").equals("Business")){//for business to reschedule or cancel events
                    if(currentObj.getString("business_id").equals(helper.getDataValue("id"))) {
                        holder.lnyRescheduleLayout.setVisibility(View.VISIBLE);

                        //Listen to approve and reject
                        holder.btnReschedule.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                            approve funct
                                Intent intent = new Intent(ctx, ConfirmEventAction.class);
                                intent.putExtra("action", "reschedule");
                                intent.putExtra("status", "approved");
                                try {
                                    intent.putExtra("id", currentObj.getString("id"));
                                    intent.putExtra("event_name", currentObj.getString("event_name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                ctx.startActivity(intent);
                            }
                        });
                        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ctx, ConfirmEventAction.class);
                                intent.putExtra("action", "cancel");
                                intent.putExtra("status", "cancelled");
                                try {
                                    intent.putExtra("id", currentObj.getString("id"));
                                    intent.putExtra("event_name", currentObj.getString("event_name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                ctx.startActivity(intent);
                            }
                        });
                    }
                }else{
                    holder.lnyRescheduleLayout.setVisibility(View.GONE);
                }

            }

            holder.lnlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(helper.hasSession()){
                        if(helper.getDataValue("user_type").equals("Business")){
                            //open view full with edit option
                            try {
                                Intent intent = new Intent(ctx, ViewReservations.class);
                                intent.putExtra("event_id", currentObj.getString("id"));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                ctx.startActivity(intent);
                            }catch (JSONException ex){

                            }
                        }else{
                            Intent intent = new Intent(ctx, ViewEventAgenda.class);
                            intent.putExtra("data",currentObj.toString());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            ctx.startActivity(intent);
                        }
                    }else{
                        //standard user open view agenda,with a reserve option
                        Intent intent = new Intent(ctx, ViewEventAgenda.class);
                        intent.putExtra("data",currentObj.toString());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ctx.startActivity(intent);
                    }
                }
            });
            holder.btnRemoveFromWatchLater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(ctx, ConfirmWatchLater.class);
                        intent.putExtra("id", currentObj.getString("id"));
                        intent.putExtra("event_name", currentObj.getString("event_name"));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ctx.startActivity(intent);
                    }catch (JSONException ex){
                        Log.d("jsonerr","Error "+ex.getMessage());
                    }
                }
            });


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
        public TextView eventId,eventName,eventType,briefDetails,eventStart,eventEnd,availableSeat,eventPreparedBy;
        public ImageView imgBanners;
        public LinearLayout lnlayout,lnyApprovalLayout,lnyRescheduleLayout;
        public Button btnApprove,btnReject,btnReschedule,btnCancel,btnRemoveFromWatchLater;

        public MyViewHolder(LinearLayout lny) {
            super(lny);
            lnlayout = lny.findViewById(R.id.lnyLayout);
            lnyApprovalLayout = lny.findViewById(R.id.lnyApprovalLayout);
            lnyRescheduleLayout = lny.findViewById(R.id.lnyRescheduleLayout);
            eventId = lny.findViewById(R.id.eventId);
            imgBanners = lny.findViewById(R.id.imgBanners);
            eventName = lny.findViewById(R.id.eventName);
            eventType = lny.findViewById(R.id.eventType);
            briefDetails = lny.findViewById(R.id.eventBriefDetails);
            eventStart = lny.findViewById(R.id.eventStart);
            eventEnd = lny.findViewById(R.id.eventEnd);
            availableSeat = lny.findViewById(R.id.eventSeat);
            eventPreparedBy = lny.findViewById(R.id.eventPreparedBy);
            btnApprove = lny.findViewById(R.id.btnApprove);
            btnReject = lny.findViewById(R.id.btnReject);
            btnReschedule = lny.findViewById(R.id.btnReschedule);
            btnCancel = lny.findViewById(R.id.btnCancel);
            btnRemoveFromWatchLater = lny.findViewById(R.id.btnRemoveFromWatchLater);
            //tvMsg = lny.findViewById(R.id.tvRecyclerDate);
        }
    }
}
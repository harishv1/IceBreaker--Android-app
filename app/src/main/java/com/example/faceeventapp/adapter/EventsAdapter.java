package com.example.faceeventapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.faceeventapp.Event;
import com.example.faceeventapp.R;

import java.util.List;

/**
 * Created by rashmitonge on 12/21/14.
 */
public class EventsAdapter extends ArrayAdapter{
    private Context context;
    private boolean useList = true;

    public EventsAdapter(Context context, List<Event> items)
    {
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
    }

    private class ViewHolder{
        TextView eventName;
        ImageView eventCategory;
        TextView eventDate;
        TextView eventVenue;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder = null;
        Event item = (Event) getItem(position);
        View viewToUse = null;

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            viewToUse = mInflater.inflate(R.layout.example_event_item_old,parent,false);

            holder = new ViewHolder();
            holder.eventName = (TextView)viewToUse.findViewById(R.id.eventListName);
            holder.eventCategory = (ImageView)viewToUse.findViewById(R.id.eventListCategory);
            holder.eventDate = (TextView)viewToUse.findViewById(R.id.eventListTime);
            holder.eventVenue = (TextView)viewToUse.findViewById(R.id.eventListVenue);

            viewToUse.setTag(holder);
        }
        else{
            viewToUse = convertView;
            holder = (ViewHolder)viewToUse.getTag();
        }
        holder.eventName.setText(item.getEventName());
        holder.eventVenue.setText(item.getEventVenue());
        holder.eventDate.setText(item.getEventDate());
        String cat = item.getEventCategory();
        if(cat.toLowerCase().equals("technology")){
            holder.eventCategory.setImageResource(R.drawable.technology);
        }
        else if(cat.toLowerCase().equals("fashion")){
            holder.eventCategory.setImageResource(R.drawable.fashion);
        }
        else if(cat.toLowerCase().equals("food")){
            holder.eventCategory.setImageResource(R.drawable.food);
        }
        else{
            holder.eventCategory.setImageResource(R.drawable.sport);
        }
        return viewToUse;


    }
}

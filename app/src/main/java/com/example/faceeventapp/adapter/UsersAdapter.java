package com.example.faceeventapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.faceeventapp.DownloadProfilePic;
import com.example.faceeventapp.Event;
import com.example.faceeventapp.R;
import com.example.faceeventapp.User;

import java.io.File;
import java.util.List;

/**
 * Created by rashmitonge on 12/21/14.
 */
public class UsersAdapter extends ArrayAdapter{
    private Context context;
    private boolean useList = true;

    public UsersAdapter(Context context, List<Event> items)
    {
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
    }

    private class ViewHolder{
        TextView userName;
        ImageView userPicture;
        TextView userInterests;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder = null;
        User item = (User) getItem(position);
        View viewToUse = null;

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            viewToUse = mInflater.inflate(R.layout.example_user_item,parent,false);

            holder = new ViewHolder();
            holder.userName = (TextView)viewToUse.findViewById(R.id.userListName);
//            holder.userInterests = (TextView)viewToUse.findViewById(R.id.userListInterests);
            holder.userPicture = (ImageView)viewToUse.findViewById(R.id.userListPicture);

            viewToUse.setTag(holder);
        }
        else{
            viewToUse = convertView;
            holder = (ViewHolder)viewToUse.getTag();
        }
        holder.userName.setText(item.username);
//        holder.userInterests.setText(item.interests);
        try{
            new DownloadProfilePic().execute(item.username).get();
        }
        catch(Exception e)
        {

        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        File imgFile = new File("/sdcard/Images/test_image.jpg");
        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
        holder.userPicture.setImageBitmap(bitmap);
        return viewToUse;


    }
}

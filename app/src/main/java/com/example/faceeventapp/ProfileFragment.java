package com.example.faceeventapp;

/**
 * Created by rashmitonge on 12/20/14.
 */
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class ProfileFragment extends Fragment {

    private TextView user_name;
    private TextView user_interests;
    private CheckBox cat_technology;
    private CheckBox cat_fashion;
    private CheckBox cat_food;
    private CheckBox cat_sports;
    private ImageView user_profile;

    private String current_user;

    public ProfileFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        final GlobalUser global_user = (GlobalUser) getActivity().getApplicationContext();
        current_user = global_user.getCurrentUser();

        if(current_user == null)
        {

        }
        else
        {
            user_name = (TextView) rootView.findViewById(R.id.user_name);
            user_interests = (TextView) rootView.findViewById(R.id.user_interests);

            cat_technology = (CheckBox) rootView.findViewById(R.id.cat_technology);
            cat_fashion = (CheckBox) rootView.findViewById(R.id.cat_fashion);
            cat_food = (CheckBox) rootView.findViewById(R.id.cat_food);
            cat_sports = (CheckBox) rootView.findViewById(R.id.cat_sports);

            user_profile = (ImageView) rootView.findViewById(R.id.user_profile);

            // Set User Profile
            // Get The user information
            User this_user = new User();
            this_user.username = current_user;
            if(current_user.equals(global_user.getUsername()))
            {
                this_user = global_user.getGlobalUser();
            }
            else {
                try {
                    this_user = new ReadUserInternal().execute(this_user).get();
                } catch (Exception e) {

                }
            }
            try{
                new DownloadProfilePic().execute(current_user).get();
            }
            catch(Exception e)
            {

            }

            user_name.setText(this_user.username);
            user_interests.setText("Interests:"+this_user.interests);

            if(this_user.technology.equals("0")) {
                cat_technology.setChecked(false);
            }else{
                cat_technology.setChecked(true);
            }
            if(this_user.fashion.equals("0")) {
                cat_fashion.setChecked(false);
            }else{
                cat_fashion.setChecked(true);
            }
            if(this_user.food.equals("0")) {
                cat_food.setChecked(false);
            }else{
                cat_food.setChecked(true);
            }
            if(this_user.sports.equals("0")) {
                cat_sports.setChecked(false);
            }else{
                cat_sports.setChecked(true);
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            File imgFile = new File("/sdcard/Images/test_image.jpg");
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
            user_profile.setImageBitmap(bitmap);
        }

        return rootView;
    }
}

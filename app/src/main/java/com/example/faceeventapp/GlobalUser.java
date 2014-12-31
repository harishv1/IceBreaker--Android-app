package com.example.faceeventapp;

/**
 * Created by rashmitonge on 12/20/14.
 */

import android.app.Application;

public class GlobalUser extends Application{

    private String username;
    private String password;
    private String interests;
    //    private String technology;
//    private String fashion;
//    private String food;
//    private String sports;
    private int technology;
    private int fashion;
    private int food;
    private int sports;

    private String event;

    private String current_user = null;

    private int homeFragmentId = 0;

    public String picture_bucket = "FaceEvenProfilePictures";

    public void copyClass(User user)
    {
        username = user.username;
        password = user.password;
        interests = user.interests;

        technology = Integer.parseInt(user.technology);
        fashion = Integer.parseInt(user.fashion);
        sports = Integer.parseInt(user.sports);
        food = Integer.parseInt(user.food);

//        technology = user.technology;
//        fashion = user.fashion;
//        food = user.food;
//        sports = user.sports;

    }
    public void setCurrentUser(String username)
    {
        current_user = username;
    }
    public void setHomeFragmentId(int id){homeFragmentId = id;}

    public String getUsername()
    {
        return username;
    }

    public int getSubscription()
    {
        return technology;
    }

    public void setEvent(String event_name)
    {
        event = event_name;
    }
    public String getEvent()
    {
        return event;
    }
    public String getCurrentUser()
    {
        return current_user;
    }

    public User getGlobalUser()
    {
        User user = new User();
        user.username = username;
        user.interests = interests;
        user.technology = technology + "";
        user.fashion = fashion + "";
        user.food = food + "";
        user.sports = sports + "";
        return user;
    }
    public int getHomeFragmentId()
    {
        return homeFragmentId;
    }

    public void logout()
    {
        username = null;
        interests = null;
        technology = 0;
        fashion = 0;
        food = 0;
        sports = 0;
    }
}

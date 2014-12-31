package com.example.faceeventapp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import android.content.ContextWrapper;

import com.amazonaws.mobileconnectors.s3.transfermanager.Download;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.Item;

import java.io.File;
import java.util.List;

/**
 * Created by rashmitonge on 12/20/14.
 */
public class DownloadProfilePic extends AsyncTask<String,Void,Void> {

    public static void downloadImage(String user_name) throws Exception
    {
        try{
            GetObjectRequest profile_picture =
                    new GetObjectRequest(Connection.getPictureBucket(),"profile_pictures/" + user_name + ".jpg");
            TransferManager transferManager = new TransferManager(Connection.getAwsS3());
            File file = new File("/sdcard/Images/test_image.jpg");
            Download download = transferManager.download(profile_picture,file);
            while(!download.isDone())
            {
//                   Toast.makeText(getBaseContext(), "Uploading...", Toast.LENGTH_LONG).show();
//                Log.d("Download Profile Pic", "Downloading Profile Picture...");
            }
        }
        catch( Exception e)
        {
            throw new Exception("First Exception",e);
        }

    }

    @Override
    protected Void doInBackground(String... params) {
        // TODO Auto-generated method stub
        try {
            downloadImage(params[0]);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

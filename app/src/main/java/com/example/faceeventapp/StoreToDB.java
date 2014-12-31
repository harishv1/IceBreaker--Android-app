package com.example.faceeventapp;

import java.util.ArrayList;
import java.util.List;
import android.os.AsyncTask;
import java.io.File;

import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;

public class StoreToDB extends AsyncTask<User, Void, Void>  {
	
	// 2. Create Domain and save movie information in domain
	public static void saveUser(User user)
	{
		try {

			 Connection.getAwsSimpleDB().createDomain(new CreateDomainRequest( "users_info"));
			 List<ReplaceableAttribute> attribute= new ArrayList<ReplaceableAttribute>(1);
			 attribute.add(new ReplaceableAttribute().withName("user_name").withValue(user.username));
			 attribute.add(new ReplaceableAttribute().withName("password").withValue(user.password));
             attribute.add(new ReplaceableAttribute().withName("interests").withValue(user.interests));
             attribute.add(new ReplaceableAttribute().withName("technology").withValue(user.technology));
             attribute.add(new ReplaceableAttribute().withName("fashion").withValue(user.fashion));
             attribute.add(new ReplaceableAttribute().withName("sports").withValue(user.sports));
             attribute.add(new ReplaceableAttribute().withName("food").withValue(user.food));

            // Store S3 link
            File file = new File(user.picturePath);
            PutObjectRequest por = new PutObjectRequest(Connection.getPictureBucket(),
                    "profile_pictures/" + user.username + user.picturePath.substring(user.picturePath.length()-4,user.picturePath.length()),
                    file);
            Connection.getAwsS3().putObject(por);
            attribute.add(new ReplaceableAttribute().withName("food").withValue(user.food));

//            attribute.add(new ReplaceableAttribute()
//                    .withName("picture_path").withValue("s3://" + Connection.getPictureBucket() + "/profile_pictures/"));

             Connection.awsSimpleDB.putAttributes(new PutAttributesRequest("users_info", user.username, attribute));
			
		} catch (Exception e) {
				System.out.println(e.getMessage());
		}
	}
	
	@Override
	protected Void doInBackground(User... params) {
		// TODO Auto-generated method stub
		saveUser(params[0]);
		return null;
	}



	
}

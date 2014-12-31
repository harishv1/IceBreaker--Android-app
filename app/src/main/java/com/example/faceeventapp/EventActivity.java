package com.example.faceeventapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.faceeventapp.adapter.NavDrawerListAdapter;
import com.example.faceeventapp.model.NavDrawerItem;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;


public class EventActivity extends Activity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private String user_name;
    private String event_name;
    private Event current_event;
    private int attendee_size = 0;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    private static final int CAMERA_REQUEST = 1888,RESULT_LOAD_IMAGE = 1;
    Uri imageUri;

    private GlobalUser global_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        // Get the event details
        global_user = (GlobalUser) getApplicationContext();
        user_name = global_user.getUsername();
        event_name = global_user.getEvent();
        try {
            current_event = new ReadCurrentEvent().execute(event_name,user_name).get();
            Log.d("filter", current_event.getEventName());
        }catch(Exception e)
        {

        }
        if(current_event.getAllAttendees().isEmpty())
        {
            attendee_size = 0;
        }
        else{
            attendee_size = current_event.getAllAttendees().size();
        }

        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_events_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Profile
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // My Events
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Create Event
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4],
                navMenuIcons.getResourceId(4, -1),true,attendee_size+""));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));

        // Recycle the typed array
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }
    }

    /**
     * Slide menu item click listener
     */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0://Current Event
                fragment = new CurrentEventFragment();
                break;
            case 1://User Profile
                global_user.setCurrentUser(global_user.getUsername());
                fragment = new ProfileFragment();
                break;
            case 2://I am Going
                try {
                    new StoreToUserEvent().execute(event_name, user_name).get();
                    Toast.makeText(getApplicationContext(), "Event " + event_name + " successfully added",
                            Toast.LENGTH_LONG).show();
                }catch(Exception e)
                {

                }
                fragment = new CurrentEventFragment();
                break;
            case 3://Take a Picture
//                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, CAMERA_REQUEST);
////                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
////                intent.setType("image/*");
////                startActivityForResult(intent,1);
//                fragment = new TakePictureFragment();
                Intent gotoHome =  new Intent(getBaseContext(),TakeNewPictureActivity.class);
                startActivity(gotoHome);
                break;
            case 4://See who is going
                fragment = new WhoAllAreGoingFragment();
                break;
            case 5://Home
                //Go to Home Activity
                global_user.setCurrentUser(global_user.getUsername());
                Intent ihome =  new Intent(getBaseContext(),HomeActivity.class);
                startActivity(ihome);
                break;
            case 6://LogOut
                global_user.logout();
                Intent ilogin =  new Intent(getBaseContext(),Login.class);
                startActivity(ilogin);
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////        // TODO Auto-generated method stub
////        super.onActivityResult(requestCode, resultCode, data);
////        if(resultCode != RESULT_CANCELED){
////            if (requestCode == CAMERA_REQUEST) {
////                Uri selectedImage = data.getData();
////                String[] filePathColumn = { MediaStore.Images.Media.DATA };
////
////                Cursor cursor = getContentResolver().query(selectedImage,
////                        filePathColumn, null, null, null);
////                cursor.moveToFirst();
////
////                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
////                String picturePath = cursor.getString(columnIndex);
////                cursor.close();
//////                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//////                imageUri = data.getData();
//////                String picturePath = imageUri.toString();
////                ExifInterface ei = null;
////                try {
////                    ei = new ExifInterface(picturePath);
////                } catch (IOException e) {
////                    // TODO Auto-generated catch block
////                    e.printStackTrace();
////                }
////                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
////
//////                switch(orientation) {
//////                    case ExifInterface.ORIENTATION_ROTATE_90:
//////                        RotateBitmap(bitmap, 90);
//////                        break;
//////                    case ExifInterface.ORIENTATION_ROTATE_180:
//////                        RotateBitmap(bitmap, 180);
//////                        break;
//////                    // etc.
//////                }
//////                bitmap=RotateBitmap(bitmap, 90);
////                //imgFavorite.setImageBitmap(bitmap);
////
////                // Server
////                HttpClient httpClient = new DefaultHttpClient();
////                HttpContext localContext = new BasicHttpContext();
////                HttpPost httpPost = new HttpPost("http://192.168.1.6:5000/login");
////                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
////                nameValuePairs.add(new BasicNameValuePair("image", picturePath));
////                nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));
////
////                try {
////                    MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
////
////                    for(int index=0; index < nameValuePairs.size(); index++) {
////                        if(nameValuePairs.get(index).getName().equalsIgnoreCase("image")) {
////                            // If the key equals to "image", we use FileBody to transfer the data
////                            entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File(nameValuePairs.get(index).getValue())));
////                        } else {
////                            // Normal string data
////                            entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
////                        }
////                    }
////
////                    httpPost.setEntity(entity);
////
////                    HttpResponse response=httpClient.execute(httpPost, localContext);
////                    //                 Toast.makeText(getApplicationContext(), "Uploading, Please Wait...", Toast.LENGTH_SHORT).show();
////                    HttpEntity response_entity = response.getEntity();
////                    String response_text = _getResponseBody(response_entity);
////                    if(response_entity == null) {
////                        String message = "No response from server";
////                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
////
////                    }
////                    response_entity.consumeContent();
//////              InputStream instream = response_entity.getContent();
////
//////              String msg=inputStreamToString(instream);
////                    Log.e("RESPONSE", response_text);
////                    Toast.makeText(getApplicationContext(), response_text, Toast.LENGTH_LONG).show();
////
////                    //                 InputStream is = entity.getContent();
////                    //                 String msg=inputStreamToString(is);
////                    //                 Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
////
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////
////            }
////        }
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
//            Uri selectedImage = data.getData();
//            String[] filePathColumn = { MediaStore.Images.Media.DATA };
//
//            Cursor cursor = getContentResolver().query(selectedImage,
//                    filePathColumn, null, null, null);
//            cursor.moveToFirst();
//
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = cursor.getString(columnIndex);
//            cursor.close();
//            ///////////////////////////////////////
//            //              String responseBody = "failure";
//            //              HttpClient client = new DefaultHttpClient();
//            //              client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
//            //
//            //              String url = "http://192.168.1.7:5000/login";
//            //              Map<String, String> map = new HashMap<String, String>();
//            ////                map.put("user_id", String.valueOf(userId));
//            ////                map.put("action", "update");
//            ////                url = addQueryParams(map, url);
//            //
//            //              HttpPost post = new HttpPost(url);
//            //              post.addHeader("Accept", "application/json");
//            //
//            //              MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//            //              builder.setCharset(MIME.UTF8_CHARSET);
//            //              builder.addPart("image", new FileBody(new File (picturePath)));
//            ////                if (career != null)
//            ////                    builder.addTextBody("career", career, ContentType.create("text/plain", MIME.UTF8_CHARSET));
//            ////                if (gender != null)
//            ////                    builder.addTextBody("gender", gender, ContentType.create("text/plain", MIME.UTF8_CHARSET));
//            ////                if (username != null)
//            ////                    builder.addTextBody("username", username, ContentType.create("text/plain", MIME.UTF8_CHARSET));
//            ////                if (email != null)
//            ////                    builder.addTextBody("email", email, ContentType.create("text/plain", MIME.UTF8_CHARSET));
//            ////                if (password != null)
//            ////                    builder.addTextBody("password", password, ContentType.create("text/plain", MIME.UTF8_CHARSET));
//            ////                if (country != null)
//            ////                    builder.addTextBody("country", country, ContentType.create("text/plain", MIME.UTF8_CHARSET));
//            ////                if (file != null)
//            ////                    builder.addBinaryBody("Filedata", picturePath, ContentType.MULTIPART_FORM_DATA);
//            //
//            //              post.setEntity(builder.build());
//            //
//            ////                try {
//            ////                    responseBody =
//            //                          try {
//            //                              client.execute(post);
//            //                          } catch (ParseException | IOException e) {
//            //                              // TODO Auto-generated catch block
//            //                              e.printStackTrace();
//            //                          }
//            //              //  System.out.println("Response from Server ==> " + responseBody);
//            //
//            //                  JSONObject object = new JSONObject(responseBody);
//            //                  Boolean success = object.optBoolean("success");
//            //                  String message = object.optString("error");
//            //
//            //                  if (!success) {
//            //                      responseBody = message;
//            //                  } else {
//            //                      responseBody = "success";
//            //                  }
//            //
//            //              } catch (Exception e) {
//            //                  e.printStackTrace();
//            //              } finally {
//            //                  client.getConnectionManager().shutdown();
//            //              }
//            //////////////////////////////////////
//            //              HttpClient httpclient = new DefaultHttpClient();
//            //              HttpPost httppost = new HttpPost("http://192.168.1.7:5000/login");
//            //
//            //              try {
//            //                  // Add your data
//            //                  List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//            //                  nameValuePairs.add(new BasicNameValuePair("id", "12345"));
//            //                  nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));
//            //                  httppost.setEntity((HttpEntity) new UrlEncodedFormEntity((List<? extends org.apache.http.NameValuePair>) nameValuePairs));
//            //
//            //                  // Execute HTTP Post Request
//            //                   httpclient.execute(httppost);
//            //
//            //              } catch (ClientProtocolException e) {
//            //                  // TODO Auto-generated catch block
//            //              } catch (IOException e) {
//            //                  // TODO Auto-generated catch block
//            //              }
//            ////////////////////////////////////////////////////////////
//            HttpClient httpClient = new DefaultHttpClient();
//            HttpContext localContext = new BasicHttpContext();
//            HttpPost httpPost = new HttpPost("http://192.168.1.6:5000/login");
//            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//            nameValuePairs.add(new BasicNameValuePair("image", picturePath));
//            nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));
//
//            try {
//                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//
//                for(int index=0; index < nameValuePairs.size(); index++) {
//                    if(nameValuePairs.get(index).getName().equalsIgnoreCase("image")) {
//                        // If the key equals to "image", we use FileBody to transfer the data
//                        entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File(nameValuePairs.get(index).getValue())));
//                    } else {
//                        // Normal string data
//                        entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
//                    }
//                }
//
//                httpPost.setEntity(entity);
//
//                HttpResponse response=httpClient.execute(httpPost, localContext);
//                //                 Toast.makeText(getApplicationContext(), "Uploading, Please Wait...", Toast.LENGTH_SHORT).show();
//                HttpEntity response_entity = response.getEntity();
//                String response_text = _getResponseBody(response_entity);
//                if(response_entity == null) {
//                    String message = "No response from server";
//                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//
//                }
//                response_entity.consumeContent();
////              InputStream instream = response_entity.getContent();
//
////              String msg=inputStreamToString(instream);
//                Log.e("RESPONSE", response_text);
//                Toast.makeText(getApplicationContext(), response_text, Toast.LENGTH_LONG).show();
//
//                //                 InputStream is = entity.getContent();
//                //                 String msg=inputStreamToString(is);
//                //                 Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            ////////////////////////////////////////////////////////////
//            //              HttpURLConnection conn = null;
//            //              DataOutputStream os = null;
//            //              DataInputStream inputStream = null;
//            //
//            //              String urlServer = "http://192.168.1.7:5000/login";
//            //
//            //              String lineEnd = "\r\n";
//            //              String twoHyphens = "--";
//            //              String boundary =  "*****";
//            //              int bytesRead, bytesAvailable, bufferSize, bytesUploaded = 0;
//            //              byte[] buffer;
//            //              int maxBufferSize = 2*1024*1024;
//            //
//            //              List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//            //              nameValuePairs.add(new BasicNameValuePair("userid", "12312"));
//            //              nameValuePairs.add(new BasicNameValuePair("sessionid", "234"));
//            ////                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//            //              String uploadname = picturePath;
//            //              Toast.makeText(getApplicationContext(), picturePath, Toast.LENGTH_LONG).show();
//            //
//            //              try
//            //              {
//            //                  FileInputStream fis = new FileInputStream(new File(picturePath) );
//            //
//            //                  URL url = new URL(urlServer);
//            //                  conn = (HttpURLConnection) url.openConnection();
//            //                  conn.setChunkedStreamingMode(maxBufferSize);
//            //
//            //                  // POST settings.
//            //                  conn.setDoInput(true);
//            //                  conn.setDoOutput(true);
//            //                  conn.setUseCaches(false);
//            //                  conn.setRequestMethod("POST");
//            //                  conn.setRequestProperty("Connection", "Keep-Alive");
//            //                  conn.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);
//            //                  conn.addRequestProperty("file", picturePath);
//            ////                    conn.addRequestProperty("password", Password);
//            //                  conn.connect();
//            //
//            //                  os = new DataOutputStream(conn.getOutputStream());
//            //                  os.writeBytes(twoHyphens + boundary + lineEnd);
//            //                  os.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + uploadname +"\"" + lineEnd);
//            //                  os.writeBytes(lineEnd);
//            //
//            //                  bytesAvailable = fis.available();
//            ////                    System.out.println("available: " + String.valueOf(bytesAvailable));
//            //                  bufferSize = Math.min(bytesAvailable, maxBufferSize);
//            //                  buffer = new byte[bufferSize];
//            //
//            //                  bytesRead = fis.read(buffer, 0, bufferSize);
//            //                  bytesUploaded += bytesRead;
//            //                  while (bytesRead > 0)
//            //                  {
//            //                      os.write(buffer, 0, bufferSize);
//            //                      bytesAvailable = fis.available();
//            //                      bufferSize = Math.min(bytesAvailable, maxBufferSize);
//            //                      buffer = new byte[bufferSize];
//            //                      bytesRead = fis.read(buffer, 0, bufferSize);
//            //                      bytesUploaded += bytesRead;
//            //                  }
//            ////                    System.out.println("uploaded: "+String.valueOf(bytesUploaded));
//            //                  os.writeBytes(lineEnd);
//            //                  os.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//            //
//            //                  // Responses from the server (code and message)
//            //                  conn.setConnectTimeout(2000); // allow 2 seconds timeout.
//            //                  int rcode = conn.getResponseCode();
//            //                  if (rcode == 200) Toast.makeText(getApplicationContext(), "Success!!", Toast.LENGTH_LONG).show();
//            //                  else Toast.makeText(getApplicationContext(), "Failed!!", Toast.LENGTH_LONG).show();
//            //                  fis.close();
//            //                  os.flush();
//            //                  os.close();
//            //                  Toast.makeText(getApplicationContext(), "Record Uploaded!", Toast.LENGTH_LONG).show();
//            //              }
//            //              catch (Exception ex)
//            //              {
//            //                  //ex.printStackTrace();
//            //                  //return false;
//            //              }
//            //
//
//            Toast.makeText(getApplicationContext(), "Your photo has been uploaded successfully", Toast.LENGTH_SHORT).show();
//
//            //              ImageView imageView = (ImageView) findViewById(R.id.imgView);
//            //              imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
//
//        }
//
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_CANCELED && data != null && resultCode == RESULT_OK){
            if (requestCode == CAMERA_REQUEST) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

//                imageUri = data.getData();
//                String picturePath = imageUri.toString();
//                Toast.makeText(getApplicationContext(), picturePath,
//                        Toast.LENGTH_LONG).show();
                ExifInterface ei = null;
                try {
                    ei = new ExifInterface(picturePath);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                switch(orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        RotateBitmap(bitmap, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        RotateBitmap(bitmap, 180);
                        break;
                    // etc.
                }
                bitmap=RotateBitmap(bitmap, 90);
//                imgFavorite.setImageBitmap(bitmap);
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost("http://158.222.144.160:5000/login");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("image", picturePath));
                nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));

                try {
                    MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                    for(int index=0; index < nameValuePairs.size(); index++) {
                        if(nameValuePairs.get(index).getName().equalsIgnoreCase("image")) {
                            // If the key equals to "image", we use FileBody to transfer the data
                            entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File (nameValuePairs.get(index).getValue())));
                        } else {
                            // Normal string data
                            entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
                        }
                    }

                    httpPost.setEntity(entity);

                    HttpResponse response=httpClient.execute(httpPost, localContext);
                    //                 Toast.makeText(getApplicationContext(), "Uploading, Please Wait...", Toast.LENGTH_SHORT).show();
                    HttpEntity response_entity = response.getEntity();
                    String response_text = _getResponseBody(response_entity);
                    if(response_entity == null) {
                        String message = "No response from server";
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                    }
                    response_entity.consumeContent();
//              InputStream instream = response_entity.getContent();

//              String msg=inputStreamToString(instream);
                    Log.e("RESPONSE", response_text);
                    //Toast.makeText(getApplicationContext(), response_text, Toast.LENGTH_LONG).show();

                    //                 InputStream is = entity.getContent();
                    //                 String msg=inputStreamToString(is);
                    //                 Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(), "Your photo has been uploaded successfully", Toast.LENGTH_SHORT).show();

            }
        }
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static String _getResponseBody(final HttpEntity entity) throws IOException, ParseException {
        if (entity == null) { throw new IllegalArgumentException("HTTP entity may not be null"); }
        InputStream instream = entity.getContent();
        if (instream == null) { return ""; }
        if (entity.getContentLength() > Integer.MAX_VALUE) { throw new IllegalArgumentException(
                "HTTP entity too large to be buffered in memory"); }
        String charset = getContentCharSet(entity);
        if (charset == null) {
            charset = HTTP.DEFAULT_CONTENT_CHARSET;
        }
        Reader reader = new InputStreamReader(instream, charset);
        StringBuilder buffer = new StringBuilder();
        try {
            char[] tmp = new char[1024];
            int l;
            while ((l = reader.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
        } finally {
            reader.close();
        }
        return buffer.toString();
    }

    public static String getContentCharSet(final HttpEntity entity) throws ParseException {
        if (entity == null) { throw new IllegalArgumentException("HTTP entity may not be null"); }
        String charset = null;
        if (entity.getContentType() != null) {
            HeaderElement values[] = entity.getContentType().getElements();
            if (values.length > 0) {
                NameValuePair param = values[0].getParameterByName("charset");
                if (param != null) {
                    charset = param.getValue();
                }
            }
        }
        return charset;
    }


}
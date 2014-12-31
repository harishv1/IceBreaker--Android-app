package com.example.faceeventapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.PrivilegedExceptionAction;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class TakeNewPictureActivity extends Activity {

    private static final int CAMERA_REQUEST = 1;

    View parent=null;
    int parentWidth=0;
    int parentHeight=0;
    private static int RESULT_LOAD_IMAGE = 1;


    private GlobalUser global_user;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_new_picture);

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);

//        parent=(View)view.getParent();
//        parentWidth=parent.getWidth();
//        parentHeight=parent.getHeight();

        final View content = findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //Remove it here unless you want to get this callback for EVERY
                //layout pass, which can get you into infinite loops if you ever
                //modify the layout from within this method.
                content.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                //Now you can get the width and height from content
            }
        });
        parentHeight = content.getHeight();
        parentWidth = content.getWidth();
        Log.d("rashmi",parentHeight+" "+parentWidth);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_take_new_picture, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    private String inputStreamToString(InputStream is) {

        String line = "";
        StringBuilder total = new StringBuilder();

        // Wrap a BufferedReader around the InputStream
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        try {
            // Read response until the end
            while ((line = rd.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {
            Log.e("streamtostring", e.getLocalizedMessage(), e);
        }
        catch(Exception e)
        {
            Log.e("streamtostring", e.getLocalizedMessage(), e);
        }

        // Return full string
        return total.toString();
    }

    //  @SuppressLint("NewApi")
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_CANCELED && requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            Bitmap bitmap =null;
//            bitmap = (Bitmap) data.getExtras().get("data");
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

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

            global_user = (GlobalUser) getApplicationContext();
            global_user.setHomeFragmentId(1);


            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost("http://158.222.144.160:5000/login");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("image", picturePath));
            nameValuePairs.add(new BasicNameValuePair("stringdata", global_user.getEvent()));

            try {
                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                for(int index=0; index < nameValuePairs.size(); index++) {
                    if(nameValuePairs.get(index).getName().equalsIgnoreCase("image")) {
                        // If the key equals to "image", we use FileBody to transfer the data
                        entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File(nameValuePairs.get(index).getValue())));
                    } else {
                        // Normal string data
                        entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
                    }
                }

                httpPost.setEntity(entity);

                HttpResponse response=httpClient.execute(httpPost, localContext);
                //                 Toast.makeText(getApplicationContext(), "Uploading, Please Wait...", Toast.LENGTH_SHORT).show();
                HttpEntity response_entity = response.getEntity();
                String response_text = null;
                try {
                    response_text = _getResponseBody(response_entity);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                if(response_entity == null) {
                    String message = "No response from server";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                }
                response_entity.consumeContent();
                //              InputStream instream = response_entity.getContent();

                //              String msg=inputStreamToString(instream);
                Log.e("RESPONSE", response_text);
                //              try {
                JSONObject fieldsJson=null;
                try {
                    fieldsJson = new JSONObject(response_text);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                /*
                     Button[] myButton=new Button[3];
                for (int faces=0;faces<3;faces++)
                {

                        myButton[faces].setText("Push Me");
                        RelativeLayout rl=(RelativeLayout)findViewById(R.id.rel);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        myButton[faces].setX(350);
                        myButton[faces].setY(400+faces*50);
                        rl.addView(myButton[faces]);
                }
                 */
                String numFacesStr=null;
                try {
                    numFacesStr = fieldsJson.getString("numFaces");

                    //                  } catch (JSONException e) {
                    //                      // TODO Auto-generated catch block
                    //                      e.printStackTrace();
                    //                  }
                    int numFaces=Integer.parseInt(numFacesStr);



                    RelativeLayout rl=(RelativeLayout)findViewById(R.id.rel);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    ////                    //                      Button[] myButton=new Button[1];
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    int height = size.y;
                    Log.d("parentwidth",Integer.toString(parentWidth));
                    Log.d("parentheight",Integer.toString(parentHeight));
                    Log.d("width",Integer.toString(width));
                    Log.d("height",Integer.toString(height));


                    Button []  button = new  Button[numFaces];
                    for (int faces=0;faces<numFaces;faces++)
                    {
                        Button btn = new Button(this);

//                      btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        btn.setWidth(15);
                        btn.setHeight(15);
                        btn.setTextSize(10);
                        btn.setId(faces);
                        int x = fieldsJson.getJSONObject(Integer.toString(faces)).getInt("centreX");
                        int y = fieldsJson.getJSONObject(Integer.toString(faces)).getInt("centreY");
                        username = fieldsJson.getJSONObject(Integer.toString(faces)).getString("match");
                        int xnew=(x*(width-100)/bitmap.getWidth());
                        int ynew=(y*(height)/bitmap.getHeight());
                        btn.setX(xnew);
                        btn.setY(ynew);
                        btn.setText(username);
                        rl.addView(btn);
                        button[faces] = btn;

                        global_user.setCurrentUser(username);
                        Log.d("Rashmi",username);

                        button[faces].setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0){
                                Button b = (Button)arg0;
                                global_user.setCurrentUser(b.getText().toString());
//                                Toast.makeText(getApplicationContext(),global_user.getUsername(),Toast.LENGTH_LONG);
                                Intent gotoHome =  new Intent(getBaseContext(),HomeActivity.class);
                                startActivity(gotoHome);
                            }
                        });

                    }
                    //
                    //                  Toast.makeText(getApplicationContext(), numFaces, Toast.LENGTH_LONG).show();

                    Log.d("jsonresponse", Integer.toString(numFaces));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //Toast.makeText(getApplicationContext(), response_text, Toast.LENGTH_LONG).show();

                //                 InputStream is = entity.getContent();
                //                 String msg=inputStreamToString(is);
                //                 Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }

            Toast.makeText(getApplicationContext(), "Your photo has been uploaded successfully", Toast.LENGTH_SHORT).show();

            ImageView imageView = (ImageView) findViewById(R.id.imgview);
//            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            imageView.setImageBitmap(bitmap);

        }


    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}

package com.example.faceeventapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;


public class TakePictureFragment extends Fragment {

    private static final int CAMERA_REQUEST = 1888,RESULT_LOAD_IMAGE = 1;
    Uri imageUri;
    private View rootView;
    private ImageView imageView;
    private TextView textView;
    private Bitmap bitmap;
    private String picturePath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_take_picture, container, false);

        imageView = (ImageView) rootView.findViewById(R.id.imgview);
        textView = (TextView) rootView.findViewById(R.id.garbage);


        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        getActivity().startActivityForResult(intent, CAMERA_REQUEST);
//                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                intent.setType("image/*");
//                startActivityForResult(intent,1);

        imageView.setImageBitmap(bitmap);

        textView.setText("Garbage");


        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != getActivity().RESULT_CANCELED && data != null && resultCode == getActivity().RESULT_OK){
            if (requestCode == CAMERA_REQUEST) {
                bitmap = null;//(Bitmap) data.getExtras().get("data");

                Uri selectedImage = data.getData();

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);

                } catch (FileNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                Log.d("rashmi",picturePath);
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
                    String response_text = _getResponseBody(response_entity);
                    if(response_entity == null) {
                        String message = "No response from server";
                        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                    }
                    response_entity.consumeContent();
//              InputStream instream = response_entity.getContent();

//              String msg=inputStreamToString(instream);
                    Log.e("RESPONSE", response_text);
                    //Toast.makeText(getActivity().getApplicationContext(), response_text, Toast.LENGTH_LONG).show();

                    //                 InputStream is = entity.getContent();
                    //                 String msg=inputStreamToString(is);
                    //                 Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getActivity().getApplicationContext(), "Your photo has been uploaded successfully", Toast.LENGTH_SHORT).show();


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

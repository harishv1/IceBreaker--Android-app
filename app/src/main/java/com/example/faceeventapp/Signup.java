package com.example.faceeventapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

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

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;


public class Signup extends Activity {

    private TextView username;
    private TextView password;
    private CheckBox technology;
    private CheckBox sports;
    private CheckBox fashion;
    private CheckBox food;
    private TextView interests;
    private Button register;

    private String picturePath;

	private static int RESULT_LOAD_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        username = (TextView)findViewById(R.id.username_text);
        password = (TextView)findViewById(R.id.pass_signup);
        interests = (TextView)findViewById(R.id.interest_text);

        technology = (CheckBox)findViewById(R.id.technology_checkbox);
        sports = (CheckBox)findViewById(R.id.sports_checkbox);
        fashion = (CheckBox)findViewById(R.id.fashion_checkbox);
        food = (CheckBox)findViewById(R.id.food_checkbox);

        register = (Button)findViewById(R.id.button1);
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                try {// TODO Auto-generated method stub
                    User object = new User();
                    object.username = username.getText().toString();
                    // Check if the username is available or not
                    object.password = password.getText().toString();
                    object.interests = interests.getText().toString();
                    object.picturePath = picturePath;
                    if (technology.isChecked()) {
                        object.technology = "1";
                    } else {
                        object.technology = "0";
                    }
                    if (fashion.isChecked()) {
                        object.fashion = "1";
                    } else {
                        object.fashion = "0";
                    }
                    if (food.isChecked()) {
                        object.food = "1";
                    } else {
                        object.food = "0";
                    }
                    if (sports.isChecked()) {
                        object.sports = "1";
                    } else {
                        object.sports = "0";
                    }


                    //DbOperation.saveMovie(object);

                    new StoreToDB().execute(object).get();
                    Toast.makeText(getApplicationContext(), "User " + object.username
                            + " registered", Toast.LENGTH_LONG).show();

                    startActivity(new Intent(Signup.this, Login.class));
                } catch (Exception e) {

                }
            }

        });


    }

	public void upload(View view)
	{
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");
		startActivityForResult(intent, RESULT_LOAD_IMAGE);

		//				AmazonS3Client s3Client = new AmazonS3Client( new BasicAWSCredentials(Connection.getProperties().getProperty("accessKey"), Connection.getProperties().getProperty("secreteKey")) );  
		//				s3Client.createBucket("imagebucket");
		//				PutObjectRequest por = new PutObjectRequest( Constants.getPictureBucket(), Constants.PICTURE_NAME, new java.io.File( filePath) );  
		//				s3Client.putObject( por );
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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			picturePath = cursor.getString(columnIndex);
			cursor.close();


			////////////////////////////////////////////////////////////
			//	            HttpURLConnection conn = null;
			//	            DataOutputStream os = null;
			//	            DataInputStream inputStream = null;
			//
			//	            String urlServer = "http://192.168.1.7:5000/login";
			//
			//	            String lineEnd = "\r\n";
			//	            String twoHyphens = "--";
			//	            String boundary =  "*****";
			//	            int bytesRead, bytesAvailable, bufferSize, bytesUploaded = 0;
			//	            byte[] buffer;
			//	            int maxBufferSize = 2*1024*1024;
			//
			//	            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
			//	            nameValuePairs.add(new BasicNameValuePair("userid", "12312"));  
			//	            nameValuePairs.add(new BasicNameValuePair("sessionid", "234"));  
			////	            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
			//	            String uploadname = picturePath;
			//	            Toast.makeText(getApplicationContext(), picturePath, Toast.LENGTH_LONG).show();
			//
			//	            try
			//	            {
			//	                FileInputStream fis = new FileInputStream(new File(picturePath) );
			//
			//	                URL url = new URL(urlServer);
			//	                conn = (HttpURLConnection) url.openConnection();
			//	                conn.setChunkedStreamingMode(maxBufferSize);
			//
			//	                // POST settings.
			//	                conn.setDoInput(true);
			//	                conn.setDoOutput(true);
			//	                conn.setUseCaches(false);
			//	                conn.setRequestMethod("POST");
			//	                conn.setRequestProperty("Connection", "Keep-Alive");
			//	                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);
			//	                conn.addRequestProperty("file", picturePath);
			////	                conn.addRequestProperty("password", Password);
			//	                conn.connect();
			//
			//	                os = new DataOutputStream(conn.getOutputStream());
			//	                os.writeBytes(twoHyphens + boundary + lineEnd);
			//	                os.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + uploadname +"\"" + lineEnd);
			//	                os.writeBytes(lineEnd);
			//
			//	                bytesAvailable = fis.available();
			////	                System.out.println("available: " + String.valueOf(bytesAvailable));
			//	                bufferSize = Math.min(bytesAvailable, maxBufferSize);
			//	                buffer = new byte[bufferSize];
			//
			//	                bytesRead = fis.read(buffer, 0, bufferSize);
			//	                bytesUploaded += bytesRead;
			//	                while (bytesRead > 0)
			//	                {
			//	                    os.write(buffer, 0, bufferSize);
			//	                    bytesAvailable = fis.available();
			//	                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
			//	                    buffer = new byte[bufferSize];
			//	                    bytesRead = fis.read(buffer, 0, bufferSize);
			//	                    bytesUploaded += bytesRead;
			//	                }
			////	                System.out.println("uploaded: "+String.valueOf(bytesUploaded));
			//	                os.writeBytes(lineEnd);
			//	                os.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
			//
			//	                // Responses from the server (code and message)
			//	                conn.setConnectTimeout(2000); // allow 2 seconds timeout.
			//	                int rcode = conn.getResponseCode();
			//	                if (rcode == 200) Toast.makeText(getApplicationContext(), "Success!!", Toast.LENGTH_LONG).show();
			//	                else Toast.makeText(getApplicationContext(), "Failed!!", Toast.LENGTH_LONG).show();
			//	                fis.close();
			//	                os.flush();
			//	                os.close();
			//	                Toast.makeText(getApplicationContext(), "Record Uploaded!", Toast.LENGTH_LONG).show();
			//	            }
			//	            catch (Exception ex)
			//	            {
			//	                //ex.printStackTrace();
			//	                //return false;
			//	            }
			//	        

			Toast.makeText(getApplicationContext(), "Your photo has been uploaded successfully", Toast.LENGTH_SHORT).show();

			//	            ImageView imageView = (ImageView) findViewById(R.id.imgView);
			//	            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

		}


	}
	public void register(View view) throws InterruptedException
	{

		Toast.makeText(getApplicationContext(), "You have been registered successfully", Toast.LENGTH_SHORT).show();
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//		getMenuInflater().inflate(R.menu.signup, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}
}

package com.example.faceeventapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.SelectRequest;

import java.util.List;

public class Login extends Activity {

    private EditText  username=null;
    private EditText  password=null;
    private TextView attempts;
    private Button login;
    private Button signup;
    int counter = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = (EditText)findViewById(R.id.editText1);
        password = (EditText)findViewById(R.id.editText2);
//      attempts = (TextView)findViewById(R.id.textView5);
//      attempts.setText(Integer.toString(counter));
        login = (Button)findViewById(R.id.button1);

    }

    public void signup(View view)
    {
        Intent signup= new Intent("android.intent.action.SIGNUP");
        startActivity(signup);

    }

    public void login(View view){
        Toast.makeText(getApplicationContext(), "Logging in..",
                Toast.LENGTH_SHORT).show();
        User user = new User();
        user.username = username.getText().toString();
        user.password = password.getText().toString();

        try{
            User profile = new ReadUser().execute(user).get();
            if(profile.username.equals("Invalid Username"))
            {
                Toast.makeText(getApplicationContext(),
                        profile.username, Toast.LENGTH_LONG).show();

            }
            else if(profile.password.equals("Invalid Password"))
            {
                Toast.makeText(getApplicationContext(),
                        profile.password, Toast.LENGTH_LONG).show();

            }
            else{
//                Toast.makeText(getApplicationContext(),
//                        "Welcome, " + profile.username, Toast.LENGTH_LONG).show();

                final GlobalUser global_user = (GlobalUser) getApplicationContext();
                global_user.copyClass(profile);
                global_user.setCurrentUser(profile.username);
                global_user.setHomeFragmentId(0);

                Toast.makeText(getApplicationContext(),
                        "Welcome, " + profile.username, Toast.LENGTH_LONG).show();

                Intent gotoHome =  new Intent(getBaseContext(),HomeActivity.class);
                startActivity(gotoHome);
            }
        }
        catch(Exception e)
        {

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//      getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
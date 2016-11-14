package com.example.accelerometerapprevisted.accelerometerapprevisted;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * Created by reusitestudent2016 on 6/28/16.
 */
public class MyServices2 extends Activity {
    private Button btnend;
    private ToggleButton btneat;
    private ToggleButton btnsmoke;
    private boolean checked1 = false;
    private boolean checked2 = false;
    private String ac;

    //use the default shared preference file
    private SharedPreferences prefs;






    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.service2_my);

        btnend = (Button) findViewById(R.id.btnEnd);

        btneat = (ToggleButton) findViewById(R.id.btneat);

        btnsmoke = (ToggleButton) findViewById(R.id.btnsmoke);




    }

    public void buttonOnClick2(View v) {

        Intent intent = new Intent(getBaseContext(), MyServices.class);
        stopService(intent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(0);
        finish();
    }

    public void changeEatState(View v){

        boolean eatChecked = ((ToggleButton)v).isChecked();
        checked1 = ((ToggleButton)v).isChecked();

        if(eatChecked && checked2 == false)
        {
            ac = "eating";
            prefs = getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("activity",ac);
            editor.commit();
            Toast.makeText(this, "Eating mode on", Toast.LENGTH_LONG).show();
            btnsmoke.setEnabled(false);
        }
        else
        {
            ac = "";
            prefs = getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("activity",ac);
            editor.commit();
            Toast.makeText(this, "Eating mode off", Toast.LENGTH_LONG).show();
            btnsmoke.setEnabled(true);
        }

    }

    public void changeSmokeState(View v){

        boolean smokeChecked = ((ToggleButton)v).isChecked();
        checked2 = ((ToggleButton)v).isChecked();

        if(smokeChecked && checked1 == false)
        {
            ac = "smoking";
            prefs = getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("activity",ac);
            editor.commit();
            Toast.makeText(this, "smoking mode on", Toast.LENGTH_LONG).show();
            btneat.setEnabled(false);

        }
        else
        {
            ac = "";
            prefs = getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("activity",ac);
            editor.commit();
            Toast.makeText(this, "smoking mode off", Toast.LENGTH_LONG).show();
            btneat.setEnabled(true);
        }

    }


}


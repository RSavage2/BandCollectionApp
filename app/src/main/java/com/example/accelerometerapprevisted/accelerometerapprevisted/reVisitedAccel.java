package com.example.accelerometerapprevisted.accelerometerapprevisted;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.microsoft.band.sensors.BandGsrEvent;
import com.microsoft.band.sensors.BandGsrEventListener;
import com.microsoft.band.sensors.SampleRate;

import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.BandIOException;
import com.microsoft.band.ConnectionState;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class reVisitedAccel extends AppCompatActivity {
    private BandClient client = null;
    private Button btnStart;
    private TextView AccTxt,GsryTxt;
    private ArrayList<String> theArrayAcc;
    private int dataHolder;
    private TextView tv;
    private Intent intent;
    private File theFile;
    private File theFile2;
    private PrintWriter theWriter;
    private long time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_visited_accel);
        dataHolder = 0;
        theArrayAcc = new ArrayList<String>();
        AccTxt = (TextView) findViewById(R.id.startINGtxt);

        btnStart = (ToggleButton) findViewById(R.id.startINGbutton);
        tv = (TextView) findViewById(R.id.textView);
         intent = getIntent();


        tv.setText(intent.getStringExtra("name"));
        time= System.currentTimeMillis();
        Toast.makeText(getApplicationContext(), "Time is:"+ time, Toast.LENGTH_LONG).show();

        Timestamp ts = new Timestamp(time);



         theFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "reVisitAcc");
         theFile2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "reVisitAcc/" + ts + ".csv");



        try {
            theFile.mkdir();
            theFile2.createNewFile();
            theWriter = new PrintWriter(theFile2);
            theWriter.println("Time, Acceler X, Acceler Y, Acceler Z, user");



        } catch (IOException x) {
            x.printStackTrace();
        }

    }
    public void changeVibrateState(View view) {
        boolean checked = ((ToggleButton) view).isChecked();
        if (checked) {
            AccTxt.setText("");
            GsryTxt.setText("");
            new AccelerometerSubscriptionTask().execute();
        } else {
            Toast.makeText(getApplicationContext(), "Error Error", Toast.LENGTH_LONG);
            try {


                PrintWriter theWriter;

                try {
                    theFile.mkdir();
                    theFile2.createNewFile();
                    theWriter = new PrintWriter(theFile2);
                    theWriter.println("Acceler X, Acceler Y, Acceler Z, user");


                    for (int i = 0; i <= (dataHolder - 1); i++) {

                        //theWriter.println(theArrayAcc.get(i));

                    }

                    theWriter.close();
                } catch (IOException x) {
                    x.printStackTrace();
                }

                client.getSensorManager().unregisterAccelerometerEventListener(mAccelerometerEventListener);
                client.getSensorManager().unregisterGsrEventListener(mGsrEventListener);
            } catch (BandIOException e) {
                appendToUI(e.getMessage());
            }
        }
    }


    private BandAccelerometerEventListener mAccelerometerEventListener = new BandAccelerometerEventListener() {
        @Override
        public void onBandAccelerometerChanged(final BandAccelerometerEvent event) {
            if (event != null) {

                 theWriter.println(System.currentTimeMillis() + ", " + String.valueOf(event.getAccelerationX()) + ", " + String.valueOf(event.getAccelerationY()) + ", " + String.valueOf(event.getAccelerationZ()) + ", "+ intent.getStringExtra("name"));



                appendToUI(String.format(" X = %.3f \n Y = %.3f\n Z = %.3f", event.getAccelerationX(),
                        event.getAccelerationY(), event.getAccelerationZ()));
            }
        }
    };

    private boolean getConnectedBandClient() throws InterruptedException, BandException {
        if (client == null) {
            //Find paired Bands
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                appendToUI("Band isn't paired with your phone.\n");
                return false;
            }

            client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }
        appendToUI("Band is connecting...\n");
        return ConnectionState.CONNECTED == client.connect().await();
    }

    private class AccelerometerSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    appendToUI("Band is connected.\n");
                    client.getSensorManager().registerAccelerometerEventListener(mAccelerometerEventListener, SampleRate.MS128);
                } else {
                    appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage = "";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                appendToUI(exceptionMessage);

            } catch (Exception e) {
                appendToUI(e.getMessage());
            }
            return null;
        }
    }

    private BandGsrEventListener mGsrEventListener = new BandGsrEventListener(){
        @Override
        public void onBandGsrChanged(final BandGsrEvent event) {
            if (event != null) {
                //theArrayGsr.add(dataHolder,String.valueOf(event.getResistance()));
                GrappendToUI(String.format("Resistance = %d kOhms\n", event.getResistance()));
            }
        }
    };



    @Override
    protected void onResume() {
        super.onResume();
        AccTxt.setText("");
    }

    @Override
    public void onBackPressed()
    {
        // code here to show dialog
        theWriter.close();
        finish();
    }


    @Override
    protected void onDestroy() {
        if (client != null) {
            try {
                client.disconnect().await();
            } catch (InterruptedException e) {
                // Do nothing as this is happening during destroy
            } catch (BandException e) {
                // Do nothing as this is happening during destroy
            }
        }
        super.onDestroy();

    }
    private void GrappendToUI(final String string) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GsryTxt.setText(string);
            }
        });
    }
    private void appendToUI(final String string) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AccTxt.setText(string);

            }
        });
    }
}

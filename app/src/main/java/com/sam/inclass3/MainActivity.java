package com.sam.inclass3;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/*
InClass3
MainActivity.java
Sam Painter and Praveen Surenani
 */

public class MainActivity extends AppCompatActivity {

    private TextView pwCount;
    private SeekBar seekBar;
    private TextView pwDisplay;
    private Button genThread;
    private Button genAsync;

    private ArrayList<String> generatedPWS;
    private Handler handler;
    private int pwNum;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pwCount = (TextView) findViewById(R.id.textViewCount);
        pwCount.setText(getString(R.string.select_passwords) + " 1");
        seekBar = (SeekBar) findViewById(R.id.seekBarSelect);
        seekBar.setProgress(1);
        pwDisplay = (TextView) findViewById(R.id.textViewPassword);
        genThread = (Button) findViewById(R.id.buttonThreads);
        genAsync = (Button) findViewById(R.id.buttonAsync);


        handler = new Handler(new Handler.Callback() {
            public boolean handleMessage(Message msg) {
                if(msg.getData().containsKey("pw")) {
                    generatedPWS.add(msg.getData().getString("pw"));
                    if (generatedPWS.size() == pwNum) {
                        buildAlert();

                        progressDialog.dismiss();
                    }
                }
                return true;
            }
        });

        genAsync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwNum = seekBar.getProgress();
                if (!checkNumber())
                    return;

                class getPW extends AsyncTask<Integer, Void, ArrayList<String>> {
                    @Override
                    protected ArrayList<String> doInBackground(Integer... params) {
                        ArrayList<String> toReturn = new ArrayList<String>();
                        for (int i = 0; i < params[0]; i++) {
                            toReturn.add(Util.getPassword());
                        }
                        return toReturn;
                    }

                    protected void onPostExecute(ArrayList<String> result) {
                        generatedPWS = result;
                        progressDialog.dismiss();

                        buildAlert();
                    }
                }
                showDialog();

                new getPW().execute(pwNum);
            }
        });

        genThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String selected;
                if (!checkNumber())
                    return;

                int threads;
                if (pwNum == 1)
                    threads = 1;
                else
                    threads = pwNum / 2;

                ExecutorService taskPool = Executors.newFixedThreadPool(threads);

                generatedPWS = new ArrayList<String>();

                Runnable getPW = new Runnable() {
                    private void sendMessage(String txt) {
                        Bundle bundle = new Bundle();
                        bundle.putString("pw", txt);
                        Message msg = new Message();
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void run() {
                        sendMessage(Util.getPassword());
                    }
                };

                showDialog();

                for (int i = 0; i < pwNum; i++) {
                    taskPool.execute(getPW);
                }


            }
    });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setPWCount(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    private void setPWCount(int pws) {
        pwCount.setText(getString(R.string.select_passwords) + " " + Integer.toString(pws));
    }

    private void buildAlert() {
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(MainActivity.this);
        adBuilder.setTitle("Passwords");


        CharSequence[] items = generatedPWS.toArray(new String[generatedPWS.size()]);
        adBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selected = generatedPWS.get(which);
                pwDisplay.setText("Password: " + selected);
            }
        });
        adBuilder.create().show();
    }

    private void showDialog() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage(getString(R.string.generating_dialogue));
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    private Boolean checkNumber() {
        pwNum = seekBar.getProgress();
        if (pwNum == 0) {
            Toast.makeText(MainActivity.this, "You can't generate 0 passwords, Silly!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}

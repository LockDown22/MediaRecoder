package com.elevenzon.mediarecorder;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AudioRecorderActivity extends AppCompatActivity {

    TextView textView;
    ImageView start, stop, recordings;
    MediaRecorder mediaRecorder;
    CountDownTimer countDownTimer;
    int second = -1, minute, hour;
    String filePath;
    String audioFile;
    public static final int PERMISSION_ALL = 0;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);
        if (checkPermission()) {
            setAudioRecorder();
        }
    }

    public void setAudioRecorder() {
        textView = (TextView) findViewById(R.id.text);
        start = (ImageView) findViewById(R.id.start);
        stop = (ImageView) findViewById(R.id.stop);
        recordings = (ImageView) findViewById(R.id.recordings);
        stop.setEnabled(false);
        stop.setBackgroundResource(R.drawable.normal_background);
        stop.setImageResource(R.drawable.noraml_stop);
        Recording();
        stopRecording();
        getRecordings();
    }

    public void Recording() {
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.setEnabled(false);
                recordings.setEnabled(false);
                stop.setEnabled(true);
                stop.setBackgroundResource(R.drawable.round_shape);
                stop.setImageResource(R.drawable.ic_stop_black_35dp);
                recordings.setBackgroundResource(R.drawable.normal_background);
                recordings.setImageResource(R.drawable.normal_menu);

                try {
                    // Create folder to store recordingss
                    File myDirectory = new File(Environment.getExternalStorageDirectory(), "11zon");
                    if (!myDirectory.exists()) {
                        myDirectory.mkdirs();
                    }
                    SimpleDateFormat dateFormat = new SimpleDateFormat("mmddyyyyhhmmss");
                    String date = dateFormat.format(new Date());
                    audioFile = "REC" + date;
                    filePath = myDirectory.getAbsolutePath() + File.separator + audioFile;
                    startAudioRecorder();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                showTimer();
            }
        });
    }

    //start audio recorder
    public void startAudioRecorder() {
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(filePath);
            mediaRecorder.prepare();
            mediaRecorder.start();
        }catch (Exception  e) {
            e.printStackTrace();
        }
    }

    //stop audio recorder
    public void stopRecording() {
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancel count down timer
                countDownTimer.cancel();
                start.setEnabled(true);
                recordings.setEnabled(true);
                stop.setEnabled(false);
                stop.setBackgroundResource(R.drawable.normal_background);
                stop.setImageResource(R.drawable.noraml_stop);
                recordings.setBackgroundResource(R.drawable.round_shape);
                recordings.setImageResource(R.drawable.ic_menu_black_35dp);
                second = -1;
                minute = 0;
                hour = 0;
                textView.setText("00:00:00");

                if (mediaRecorder != null) {
                    try {
                        //stop mediaRecorder
                        mediaRecorder.stop();
                        mediaRecorder.reset();
                    }catch (IllegalStateException  e) {
                        e.printStackTrace();
                    }
                }

                //creating content resolver and put the values
                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Media.DATA, filePath);
                values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp");
                values.put(MediaStore.Audio.Media.TITLE, audioFile);
                //store audio recorder file in the external content uri
                getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
            }
        });
    }

    //lanuch RecordingsActivity
    public void getRecordings() {
        recordings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.setEnabled(true);
                recordings.setEnabled(true);
                stop.setEnabled(false);
                stop.setBackgroundResource(R.drawable.normal_background);
                stop.setImageResource(R.drawable.noraml_stop);
                recordings.setBackgroundResource(R.drawable.round_shape);
                recordings.setImageResource(R.drawable.ic_menu_black_35dp);
                startActivity(new Intent(getApplicationContext(), RecordingsActivity.class));
            }
        });
    }

    //display recording time
    public void showTimer() {
        countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                second++;
                textView.setText(recorderTime());
            }
            public void onFinish() {

            }
        };
        countDownTimer.start();
    }

    //recorder time
    public String recorderTime() {
        if (second == 60) {
            minute++;
            second = 0;
        }
        if (minute == 60) {
            hour++;
            minute = 0;
        }
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    //runtime permission
    public boolean checkPermission() {
        int RECORD_AUDIO_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int WRITE_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        ArrayList<String> PERMISSION_LIST =new ArrayList<>();
        if((RECORD_AUDIO_PERMISSION != PackageManager.PERMISSION_GRANTED)) {
            PERMISSION_LIST.add(Manifest.permission.RECORD_AUDIO);
        }
        if((WRITE_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED)) {
            PERMISSION_LIST.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!PERMISSION_LIST.isEmpty()) {
            ActivityCompat.requestPermissions(this, PERMISSION_LIST.toArray(new String[PERMISSION_LIST.size()]), PERMISSION_ALL);
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean record = false,storage =  false;
        switch (requestCode) {
            case  PERMISSION_ALL: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(Manifest.permission.RECORD_AUDIO)) {
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                record = true;
                            } else {
                                Toast.makeText(getApplicationContext(), "Please allow Microphone permission", Toast.LENGTH_LONG).show();
                            }
                        } else if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                storage = true;
                            } else {
                                Toast.makeText(getApplicationContext(), "Please allow Storage permission", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
                if (record && storage) {
                    setAudioRecorder();
                }
            }
        }
    }

    //release mediarecorder
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaRecorder != null) {
            mediaRecorder.release();
        }
    }
}
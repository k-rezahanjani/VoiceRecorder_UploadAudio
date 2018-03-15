package com.example.kaveh90.myapplication;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private Button start_recording,stop_recording;
    private TextView status_text;
    private MediaRecorder mediaRecorder;
    private String fileExten[] = {".3gp"};
    private int curFormat = 0;
    private Boolean isStop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start_recording = (Button) findViewById(R.id.start_recording);
        stop_recording = (Button) findViewById(R.id.stop_recording);
        status_text = (TextView) findViewById(R.id.status_text);

    }

    public void startRecording(View view)
    {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(createPath());


            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }


        status_text.setText("Recording Started");
    }

    public void stopRecording(View view)
    {
       try {
           mediaRecorder.stop();
           mediaRecorder.release();
           status_text.setText("Recording Stopped");
       }
       catch (Exception ex)
       {
           status_text.setText("Oops !!!");
       }

    }


    public String createPath()
    {
        String filePath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filePath,"MediaRecorder");

        if(!file.exists())
            file.mkdirs();

        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + fileExten[curFormat]);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        switch (item.getItemId())
        {
            case R.id.upload_file:
                Intent myIntent = new Intent(MainActivity.this,UploadAudio.class);
                startActivity(myIntent);
                break;

            case R.id.list_audio:

                Intent intent = new Intent(this,List.class);
                startActivity(intent);
                break;

            case R.id.exit_app:
                System.exit(0);
                break;
        }
        
        return super.onOptionsItemSelected(item);
    }
}

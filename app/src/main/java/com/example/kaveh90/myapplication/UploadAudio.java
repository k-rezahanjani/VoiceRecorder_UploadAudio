package com.example.kaveh90.myapplication;


import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadAudio extends AppCompatActivity
{
    private String filePath;
    private Button choose,upload;
    private TextView txt;

    private ProgressDialog dialog;
    private int serverResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_audio);

        choose = (Button) findViewById(R.id.choose);
        upload = (Button) findViewById(R.id.upload);

        txt = (TextView) findViewById(R.id.textView);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filePath != null)
                {
                    dialog = ProgressDialog.show(UploadAudio.this,"","Uploading...",true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            uploadFile(filePath);
                        }
                    }).start();
                }
                else
                {
                    Toast.makeText(UploadAudio.this, "Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void chooseFile(View v)
    {
        Intent intent = new Intent();
        intent.setType("audio/3gp");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Please select an Audio"),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1 && resultCode == RESULT_OK)
        {
            Uri uri = data.getData();
            filePath = getPath(uri);
            txt.setText("Your File Path is : \n" + filePath);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getPath(Uri uri)
    {
        String[] projection = {MediaStore.Audio.Media.DATA};
        Cursor cursor =getContentResolver().query(uri,projection,null,null,null);
        int columnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        cursor.moveToNext();
        return cursor.getString(columnIndex);
    }

    public void uploadFile(final String uploader)
    {
        String uploaderFile = uploader;
        File sourceFile = new File(uploader);

        try {

            String urlPath = "http://192.168.1.33:8080/uploadAudio/rest/uploadService/doUpload";

            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            byte[] tempBytes = new byte[(int) sourceFile.length()];
            fileInputStream.read(tempBytes);
            fileInputStream.close();

            String filebase64 = "file=" + new String(Base64.encode(tempBytes,Base64.NO_WRAP|Base64.URL_SAFE));
            System.out.println(filebase64);

            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection","Keep-Alive");
            connection.setRequestProperty("Accept-Charset","UTF-8");
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
            connection.setRequestProperty("uploaded_file",uploaderFile);
            connection.setRequestProperty("Content-Length",Integer.toString(filebase64.length()));

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.write(filebase64.getBytes());
            dataOutputStream.flush();
            dataOutputStream.close();

            InputStream inputStream = connection.getInputStream();
            String result = "";
            int c;

            while((c = inputStream.read()) != -1)
            {
                result += (char) c;
            }

            serverResponse = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();

            Log.i("uploadFile","HTTP Response is : " + responseMessage + ": " + serverResponse);

            if(serverResponse == 200)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = "Upload File Completed";
                        txt.setText(msg);
                        Toast.makeText(UploadAudio.this, "Upload File Completed", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }
                });
            }

            inputStream.close();

        }
        catch (Exception ex)
        {
            return;
        }
    }
}

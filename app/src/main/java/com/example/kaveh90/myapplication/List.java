package com.example.kaveh90.myapplication;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class List extends AppCompatActivity {

    private ListView listView;
    private String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = (ListView) findViewById(R.id.list_view);

        final ArrayList<File> myAudios = findSongs(Environment.getExternalStorageDirectory());
        items = new String[ myAudios.size() ];
        for(int i = 0; i < myAudios.size(); i++)
        {
            //toast(mySongs.get(i).getName().toString());
            items[i] = myAudios.get(i).getName().toString();
        }


        ArrayAdapter<String> adp = new ArrayAdapter<String>(getApplicationContext(),R.layout.audio_layout,R.id.textView,items);
        listView.setAdapter(adp);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //startActivity(new Intent(getApplicationContext(),Player.class).putExtra("pos",position).putExtra("songlist",mySongs));

            }
        });
    }

    public ArrayList<File> findSongs(File root)
    {
        ArrayList<File> a1 = new ArrayList<File>();
        File[] files = root.listFiles();

        for(File singleFile : files)
        {
            if(singleFile.isDirectory() && !singleFile.isHidden())
            {
                a1.addAll(findSongs(singleFile));
            }
            else
            {
                if(singleFile.getName().endsWith(".3gp"))
                {
                    a1.add(singleFile);
                }
            }
        }

        return a1;
    }

    public void toast(String text)
    {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }


}

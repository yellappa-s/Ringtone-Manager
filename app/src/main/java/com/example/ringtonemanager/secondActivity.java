package com.example.ringtonemanager;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Random;
public class secondActivity extends AppCompatActivity {

    public static ArrayList<Model> Tones = new ArrayList<>();
    public MediaPlayer mysong = new MediaPlayer();
    public static Uri path;
    public int loc = -1;
    public Button remove;
    ListView audioView;
    MyCustomAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        loadprefs();
        audioView = findViewById(R.id.songView);
        if(adapter == null){
            adapter = new MyCustomAdapter(getApplicationContext(), R.layout.song_info, Tones);
        } else {
            adapter.notifyDataSetChanged();
        }
        audioView.setAdapter(adapter);
        audioView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (loc == position) {
                    if (mysong.isPlaying()) mysong.pause();
                    else mysong.start();
                } else {
                    loc = position;
                    if (mysong.isPlaying() || mysong != null) {
                        mysong.stop();
                        mysong.release();
                    }
                    mysong = MediaPlayer.create(secondActivity.this, Uri.parse(Tones.get(position).getPath()));
                    mysong.start();
                }
            }
        });
        remove = findViewById(R.id.delete);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Integer> temp = adapter.removeList();
                int j = 0;
                if (temp.isEmpty()) {
                    Toast.makeText(secondActivity.this, "Please select items to remove", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < Tones.size(); i++) {
                        if (i == temp.get(j)) {
                            Tones.remove(i);
                            j++;
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    public Uri RandomRingtone() {
        Random r = new Random();
        int i = r.nextInt(Tones.size());
        String s = Tones.get(i).getPath();
        s = "file://"+s;
        return Uri.parse(s);
    }
    public void saveprefs(ArrayList<Model> paths) {
        String sfile = "com.example.android.ringtonemanager";
        SharedPreferences mshared = getSharedPreferences(sfile, MODE_PRIVATE);
        SharedPreferences.Editor editor = mshared.edit();
        LinkedHashSet<Model> nset = new LinkedHashSet<>(paths);
        Gson gson = new Gson();
        String json = gson.toJson(nset);
        editor.putString("paths",json);
        editor.apply();

    }
    public void loadprefs() {
        String sfile = "com.example.android.ringtonemanager";
        SharedPreferences mshared = getSharedPreferences(sfile, MODE_PRIVATE);
        Gson gson = new Gson();
        if (mshared.getString("paths", null) != null) {
        String json = mshared.getString("paths",null);
        Type type = new TypeToken<ArrayList<Model>>() {}.getType();
        Tones = gson.fromJson(json, type);
        }
    }
    public void additems(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK) {
            boolean notexist = true;
            path = data.getData();
                File file = new File(Objects.requireNonNull(path.getPath()));
                String name = file.getName();
                if (name.endsWith(".mp3")) {
                    for(Model o:Tones){
                        if(name.equals(o.getName())){
                            notexist = false;
                            Toast.makeText(this, "The file already exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                if(notexist) {
                    Model ob = new Model(name);
                        String[] fp = {MediaStore.Audio.Media.DATA};
                        Cursor cu = getContentResolver().query(path, fp, null, null, null);
                        assert cu != null;
                        cu.moveToFirst();
                        int cindex = cu.getColumnIndex(fp[0]);
                        String fpath = cu.getString(cindex);
                        ob.setPath(fpath);
                        cu.close();
                        //Log.i("stored path",fpath);
                        Tones.add(ob);
                        adapter.songList.add(ob);
                        adapter.notifyDataSetChanged();
                    }
                } else
                    Toast.makeText(this, "please select .mp3 file", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveprefs(Tones);
        if(mysong != null && mysong.isPlaying()){
            mysong.stop();
            mysong.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveprefs(Tones);
        if(mysong != null && mysong.isPlaying()){
            mysong.stop();
            mysong.release();
        }
    }

    class Model {
        private boolean checked;
        private String name;
        private String path;

        Model(String s){
            this.name = s;
            this.checked = false;
        }
        String getName() {
            return name;
        }
        void setChecked(boolean val) {
            this.checked = val;
        }
        boolean getChecked() {
            return checked;
        }
        void setPath(String p) {
            this.path = p;
        }
        String getPath() {
            return path;
        }
    }
}










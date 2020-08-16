package com.example.ringtonemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MyCustomAdapter extends ArrayAdapter<secondActivity.Model> {

    public ArrayList<secondActivity.Model> songList = new ArrayList<>();

    MyCustomAdapter(Context context, int textViewResourceId, ArrayList<secondActivity.Model> songlist) {
        super(context, textViewResourceId, songlist);
        songList.addAll(songlist);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private CheckBox check;

        ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public int getCount() {
        return songList.size();
    }

    public secondActivity.Model getItem(int pos) {
        return songList.get(pos);
    }

    @NonNull
    @Override
    public View getView(final int pos, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater vi = LayoutInflater.from(getContext());
        if (convertView == null) {
            convertView = vi.inflate(R.layout.song_info, null);

            holder = new ViewHolder(convertView);
            holder.name = convertView.findViewById(R.id.code);
            holder.check = convertView.findViewById(R.id.check);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(songList.get(pos).getName());
        holder.check.setChecked(songList.get(pos).getChecked());

        holder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(songList.get(pos).getChecked()){
                    songList.get(pos).setChecked(false);
                } else {
                    songList.get(pos).setChecked(true);
                }
            }
        });

    return convertView;
    }

    public ArrayList<Integer> removeList() {
        ArrayList<Integer> rmlist = new ArrayList<>();
        for(int i=0;i<songList.size();i++){
            if(songList.get(i).getChecked()){
               rmlist.add(i);
               songList.remove(i);
            }
        }
        return rmlist;
    }
}

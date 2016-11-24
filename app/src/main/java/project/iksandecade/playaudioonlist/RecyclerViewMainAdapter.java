package project.iksandecade.playaudioonlist;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import project.iksandecade.playaudioonlist.dao.ListAudio;

/**
 * Created by
 * Name         : Ihksan Sukmawan
 * Email        : iksandecade@gmail.com
 * Company      : Meridian.Id
 * Date         : 18/11/16
 * Project      : PlayAudioOnList
 */

public class RecyclerViewMainAdapter extends RecyclerView.Adapter<RecyclerViewMainAdapter.Holder> {


    private List<ListAudio> listAudios;
    private List<SeekBar> seekBars = new ArrayList<SeekBar>();
    private LayoutInflater layoutInflater;
    private MediaPlayer mp = new MediaPlayer();
    private int currentPosition = -1;
    private Context context;
    private boolean isPlay = false;
    private File file;

    RecyclerViewMainAdapter(List<ListAudio> listAudios, Activity activity) {
        this.listAudios = listAudios;
        this.layoutInflater = activity.getLayoutInflater();
        context = activity;
        file = activity.getCacheDir();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(layoutInflater.inflate(R.layout.item_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        holder.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPosition == position) {
                    pausePlaying();
                    Log.d("pause on", currentPosition + "," + position);
                } else {
                    Log.d("start on", currentPosition + "," + position);
                    String name = listAudios.get(position).getName();
                    currentPosition = position;
                    seekBars.add(position, holder.sbAudio);
                    playSong(file.getPath() + "/" + name);
                }

            }
        });
    }

    private void playSong(String songPath) {
        Log.d("play on", currentPosition + "" + songPath);
        for(int i = 0; i < seekBars.size(); i++){
            seekBars.get(i).setProgress(100);
        }
        try {

            File file1 = new File(songPath);
            if (file1.exists()) {
                mp.reset();
                mp.setDataSource(songPath);
                mp.prepare();
                mp.start();
            } else {
                Log.d("hello", "gagal");
            }

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer arg0) {
                    currentPosition = -1;
                }

            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void pausePlaying() {
        if (mp.isPlaying()) {
            mp.pause();
        } else {
            mp.start();
        }
    }


    @Override
    public int getItemCount() {
        seekBars.clear();
        for (int i = 0; i < listAudios.size(); i++) {
            SeekBar seekBar = new SeekBar(context);
            seekBars.add(seekBar);
        }
        return listAudios.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        SeekBar sbAudio;
        ImageView ivPlay;

        Holder(View itemView) {
            super(itemView);
            sbAudio = (SeekBar) itemView.findViewById(R.id.sbAudio);
            ivPlay = (ImageView) itemView.findViewById(R.id.ivPlay);
        }
    }
}
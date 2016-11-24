package project.iksandecade.playaudioonlist;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
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
    private List<Runnable> runnables = new ArrayList<Runnable>();
    private List<ImageView> imageViews = new ArrayList<ImageView>();
    private LayoutInflater layoutInflater;
    private MediaPlayer mp = new MediaPlayer();
    private int currentPosition = -1;
    private Context context;
    private File file;
    private double startTime = 0;
    private double finalTime = 0;
    private Handler handler = new Handler();

    RecyclerViewMainAdapter(List<ListAudio> listAudios, Activity activity) {
        this.listAudios = listAudios;
        this.layoutInflater = activity.getLayoutInflater();
        context = activity;
        file = activity.getCacheDir();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        seekBars.clear();
        runnables.clear();
        imageViews.clear();
        for (int i = 0; i < listAudios.size(); i++) {
            SeekBar seekBar = new SeekBar(context);
            ImageView imageView = new ImageView(context);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                }
            };
            seekBars.add(seekBar);
            runnables.add(runnable);
            imageViews.add(imageView);
        }
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
                    imageViews.add(position, holder.ivPlay);
                    playSong(file.getPath() + "/" + name);
                }

            }
        });
    }

    private void playSong(String songPath) {
        Log.d("play on", currentPosition + "" + songPath);
        for (int i = 0; i < seekBars.size(); i++) {
            if (i != currentPosition) {
                seekBars.get(i).setProgress(0);
            }
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
                    for(int i =0; i < imageViews.size(); i++){
                        imageViews.get(i).setImageResource(R.mipmap.ic_play_arrow_white_24dp);
                    }
                    currentPosition = -1;
                }

            });

            imageViews.get(currentPosition).setImageResource(R.mipmap.ic_pause_white_24dp);
            final SeekBar seekBar = seekBars.get(currentPosition);
            finalTime = mp.getDuration();
            startTime = mp.getCurrentPosition();
            seekBar.setMax((int) finalTime);
            seekBar.setProgress((int) startTime);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    startTime = mp.getCurrentPosition();
                    seekBar.setProgress((int) startTime);
                    handler.postDelayed(this, 100);
                }
            };
            runnables.add(currentPosition, runnable);
            for (int i = 0; i < runnables.size(); i++) {
                if (i != currentPosition) {
                    Log.d("kamen stop position", i + "");
                    handler.removeCallbacks(runnables.get(i));
                } else {
                    Log.d("kamen start position", i + "");
                    handler.postDelayed(runnables.get(i), 100);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void pausePlaying() {
        if (mp.isPlaying()) {
            imageViews.get(currentPosition).setImageResource(R.mipmap.ic_play_arrow_white_24dp);
            mp.pause();
        } else {
            imageViews.get(currentPosition).setImageResource(R.mipmap.ic_pause_white_24dp);
            mp.start();
        }
    }


    @Override
    public int getItemCount() {

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
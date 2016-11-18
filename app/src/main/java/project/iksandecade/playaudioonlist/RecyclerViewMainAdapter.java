package project.iksandecade.playaudioonlist;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import project.iksandecade.playaudioonlist.dao.ListAudio;

import static android.R.attr.path;

/**
 * Created by
 * Name         : Ihksan Sukmawan
 * Email        : iksandecade@gmail.com
 * Company      : Meridian.Id
 * Date         : 18/11/16
 * Project      : PlayAudioOnList
 */

public class RecyclerViewMainAdapter extends RecyclerView.Adapter<RecyclerViewMainAdapter.Holder> {

    List<ListAudio> listAudios;
    LayoutInflater layoutInflater;
    Activity activity;
    Handler handler = new Handler();
    private boolean isPlay = false;

    public RecyclerViewMainAdapter(List<ListAudio> listAudios, Activity activity){
        this.listAudios = listAudios;
        this.layoutInflater = activity.getLayoutInflater();
        this.activity = activity;
    }
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(layoutInflater.inflate(R.layout.item_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final String name = listAudios.get(position).getName();
        final MediaPlayer mediaPlayer = null;
        holder.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlay) {
                    pausePlaying(holder.ivPlay, mediaPlayer);
                } else {
                    holder.ivPlay.setImageResource(R.mipmap.ic_pause_white_24dp);
                    startPlaying(name, holder.ivPlay, holder.sbAudio, mediaPlayer);
                }
            }
        });
    }

    private void startPlaying(String name, final ImageView ivPlay, final SeekBar sbAudio, MediaPlayer mediaPlayer) {
        final double[] startTime = {0};
        final double[] finalTime = {0};
        final File file = activity.getCacheDir();
        if (mediaPlayer != null)
            mediaPlayer = null;
        String outputFile = file.getPath() + "/" + name;
        File file1 = new File(outputFile);
        if (file1.exists()) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(outputFile);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.start();
            isPlay = true;
        } else {
            Toast.makeText(activity, "No", Toast.LENGTH_SHORT).show();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mp.getCurrentPosition() + 21 == mp.getDuration()) {
                    ivPlay.setImageResource(R.mipmap.ic_play_arrow_white_24dp);
                    isPlay = false;
                    finalTime[0] = 0;
                    startTime[0] = 0;
                    sbAudio.setMax((int) finalTime[0]);
                    sbAudio.setProgress((int) startTime[0]);
                }
            }
        });
        finalTime[0] = mediaPlayer.getDuration();
        startTime[0] = mediaPlayer.getCurrentPosition();
        sbAudio.setMax((int) finalTime[0]);

        sbAudio.setProgress((int) startTime[0]);

        final MediaPlayer finalMediaPlayer = mediaPlayer;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                startTime[0] = finalMediaPlayer.getCurrentPosition();
                sbAudio.setProgress((int) startTime[0]);
                handler.postDelayed(this, 100);
            }
        };

        handler.postDelayed(runnable, 100);
    }
    void pausePlaying(ImageView view, MediaPlayer mediaPlayer) {
        if (mediaPlayer.isPlaying()) {
            view.setImageResource(R.mipmap.ic_play_arrow_white_24dp);
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
            view.setImageResource(R.mipmap.ic_pause_white_24dp);
        }
    }

    @Override
    public int getItemCount() {
        return listAudios.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        SeekBar sbAudio;
        ImageView ivPlay;
        public Holder(View itemView) {
            super(itemView);
            sbAudio = (SeekBar) itemView.findViewById(R.id.sbAudio);
            ivPlay = (ImageView) itemView.findViewById(R.id.ivPlay);
        }
    }
}

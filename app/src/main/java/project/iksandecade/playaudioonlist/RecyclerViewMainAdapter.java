package project.iksandecade.playaudioonlist;

import android.app.Activity;
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
    private LayoutInflater layoutInflater;
    private MediaPlayer mp = new MediaPlayer();
    private int currentPosition = 0;
    private File file;

    RecyclerViewMainAdapter(List<ListAudio> listAudios, Activity activity) {
        this.listAudios = listAudios;
        this.layoutInflater = activity.getLayoutInflater();
        file = activity.getCacheDir();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(layoutInflater.inflate(R.layout.item_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final String name = listAudios.get(position).getName();
        currentPosition = position;
        holder.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSong(file.getPath() + "/" + name);
            }
        });
    }

    private void playSong(String songPath) {
        try {

            File file1 = new File(songPath);
            if (file1.exists()) {
                mp.reset();
                mp.setDataSource(songPath);
                mp.prepare();
                mp.start();
            } else{
                Log.d("hello", "gagal");
            }

            // Setup listener so next song starts automatically
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer arg0) {
                    nextSong();
                }

            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void nextSong() {
        if (++currentPosition >= listAudios.size()) {
            // Last song, just reset currentPosition
            currentPosition = 0;
        } else {
            // Play next song
            playSong(file.getPath() + "/" + listAudios.get(currentPosition).getName());
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
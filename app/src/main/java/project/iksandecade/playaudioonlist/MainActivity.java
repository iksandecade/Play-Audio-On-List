package project.iksandecade.playaudioonlist;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import project.iksandecade.playaudioonlist.dao.DaoSession;
import project.iksandecade.playaudioonlist.dao.ListAudio;
import project.iksandecade.playaudioonlist.dao.ListAudioDao;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CardView cvTime;
    FloatingActionButton fabRecord;
    RecyclerViewMainAdapter recyclerViewMainAdapter;
    List<ListAudio> listAudioList = new ArrayList<ListAudio>();
    TextView tvTime;
    DaoSession daoSession;
    Timer timer;
    MediaRecorder mediaRecorder;

    long timeInMillisecond = 0L;
    long timeSwapBuff = 0L;
    long updateTime = 0L;
    long startTime = 0L;
    String nameAudio;
    private String outputFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.rvPlayAudio);
        cvTime = (CardView) findViewById(R.id.cvTime);
        fabRecord = (FloatingActionButton) findViewById(R.id.fabRecord);
        tvTime = (TextView) findViewById(R.id.tvTime);
        daoSession = DaoHandler.getInstance(this);
        recyclerViewMainAdapter = new RecyclerViewMainAdapter(listAudioList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewMainAdapter);

        loadData();

    }

    @Override
    protected void onStart() {
        super.onStart();
        playAnime(false);
        fabRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    playAnime(true);
                    startRecord();
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    playAnime(false);
                    stopRecord();
                }
                v.onTouchEvent(event);
                return true;
            }
        });
    }

    private void startRecord() {
        startTime = SystemClock.uptimeMillis();
        timer = new Timer();
        TimerTaskUtil timerTaskUtil = new TimerTaskUtil();
        timer.schedule(timerTaskUtil, 1000, 1000);

        nameAudio = System.currentTimeMillis() + ".3gp";
        File file = this.getCacheDir();
        outputFile = file.getPath() + "/" + nameAudio;
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(outputFile);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void stopRecord() {
        if (timer != null) {
            timer.cancel();
        }

        if (tvTime.getText().toString().equals("00:00")) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            File dir = this.getCacheDir();
            File file = new File(dir, nameAudio);
            file.delete();
            return;
        }

        tvTime.setText("00:00");

        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        daoSession.getListAudioDao().insertOrReplace(new ListAudio(nameAudio, System.currentTimeMillis()));
        loadData();
    }

    private void loadData() {
        listAudioList.clear();
        listAudioList.addAll(daoSession.getListAudioDao().queryBuilder().orderAsc(ListAudioDao.Properties.TimeStamp).list());
        recyclerViewMainAdapter.notifyDataSetChanged();

    }

    private void playAnime(Boolean status) {
        Animation animUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_up);
        Animation animBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_bottom);
        if (status) {
            cvTime.setAnimation(animUp);
        } else {
            cvTime.setAnimation(animBottom);
        }
    }

    class TimerTaskUtil extends TimerTask {

        @Override
        public void run() {
            timeInMillisecond = SystemClock.uptimeMillis() - startTime;
            updateTime = timeSwapBuff + timeInMillisecond;
            final String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(updateTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(updateTime)), TimeUnit.MILLISECONDS.toSeconds(updateTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(updateTime)));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (tvTime != null) {
                            tvTime.setText(hms);
                        }
                    } catch (Exception e) {

                    }
                }
            });

        }
    }
}

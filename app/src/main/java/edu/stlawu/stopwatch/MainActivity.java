package edu.stlawu.stopwatch;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // Define variable for our views
    private TextView tv_count = null;
    private Button bt_start = null;
    private Button bt_reset =null;
    private Timer t = null;
    private Counter ctr = null;  // TimerTask

    public AudioAttributes  aa = null;
    private SoundPool soundPool = null;
    private int bloopSound = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize views
        this.tv_count = findViewById(R.id.tv_count);
        this.bt_start = findViewById(R.id.bt_start);
        this.bt_reset = findViewById(R.id.bt_reset);

        this.bt_start.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(bt_start.getText() == getResources().getString(R.string.start) || bt_start.getText() == getResources().getString(R.string.resume)){
                     t.scheduleAtFixedRate(ctr, 0, 100);
                     bt_start.setText(getResources().getString(R.string.stop));
                     bt_start.setBackgroundColor(getResources().getColor(R.color.colorStop));
                 } else {
                     bt_start.setText(getResources().getString(R.string.resume));
                     bt_start.setBackgroundColor(getResources().getColor(R.color.colorStart));
                     t.cancel();
                     int countSave = ctr.count;
                     int secCountSave = ctr.secCount;
                     int minCountSave = ctr.minCount;
                     ctr.cancel();
                     t = new Timer();
                     ctr = new Counter();
                     ctr.count = countSave;
                     ctr.secCount = secCountSave;
                     ctr.minCount = minCountSave;
                 }


             }
            });

        this.bt_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                t.cancel();
                ctr.cancel();
                t = new Timer();
                ctr = new Counter();
                ctr.count = 0;
                ctr.secCount = 0;
                ctr.minCount = 0;
                MainActivity.this.tv_count.setText(R.string.timer0);
                bt_start.setText(getResources().getString(R.string.start));
                bt_start.setBackgroundColor(getResources().getColor(R.color.colorStart));
            }
        });

        this.aa = new AudioAttributes
                .Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build();

        this.soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(aa)
                .build();
        this.bloopSound = this.soundPool.load(
                this, R.raw.bloop, 1);

        this.tv_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundPool.play(bloopSound, 1f,
                        1f, 1, 0, 1f);
                Animator anim = AnimatorInflater
                        .loadAnimator(MainActivity.this,
                                       R.animator.counter);
                anim.setTarget(tv_count);
                anim.start();
            }
        });


        // releoad the count from a previous
        // run, if first time running, start at 0.
        /// preferences to share state
        int count = getPreferences(MODE_PRIVATE).getInt("COUNT", 0);
        int seccount = getPreferences(MODE_PRIVATE).getInt("SECCOUNT", 0);
        int mincount = getPreferences(MODE_PRIVATE).getInt("MINCOUNT", 0);

        String secCountS = Integer.toString(seccount);
        String minCountS = Integer.toString(mincount);
        if(seccount < 10){
            secCountS = "0" + secCountS;
        }
        if(mincount < 10){
            minCountS = "0" + minCountS;
        }
        this.tv_count.setText(minCountS + ":" + secCountS + "." + Integer.toString(count));
        this.ctr = new Counter();
        this.ctr.count = count;
        this.ctr.secCount = seccount;
        this.ctr.minCount = mincount;
        this.t = new Timer();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferences(MODE_PRIVATE).edit().putInt("COUNT", ctr.count).apply();
        getPreferences(MODE_PRIVATE).edit().putInt("SECCOUNT", ctr.secCount).apply();
        getPreferences(MODE_PRIVATE).edit().putInt("MINCOUNT", ctr.minCount).apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class Counter extends TimerTask {
        private int count = 0;
        private int secCount = 0;
        private int minCount = 0;
        @Override
        public void run() {
            MainActivity.this.runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            if (count == 10){
                                count = 0;
                                secCount++;
                                if (secCount == 60){
                                    secCount = 0;
                                    minCount++;
                                }
                            }
                            String minCountS = Integer.toString(minCount);
                            if(minCount < 10){
                                minCountS = "0"+minCountS;
                            }
                            String secCountS = Integer.toString(secCount);
                            if (secCount < 10){
                                secCountS = "0"+secCountS;
                            }
                            String countS = Integer.toString(count);
                            MainActivity.this.tv_count.setText(minCountS + ":" + secCountS + "." + countS);
                            count++;
                        }
                    }
            );
        }
    }
}

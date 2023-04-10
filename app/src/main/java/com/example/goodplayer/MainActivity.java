package com.example.goodplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements Runnable {

    private FloatingActionButton startButton;
    private FloatingActionButton stopButton;
    private SeekBar seekBar;
    private MediaPlayer mp;
    private boolean wasPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        startButton = (FloatingActionButton) findViewById(R.id.playButton);
        stopButton = (FloatingActionButton) findViewById(R.id.stopButton);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        startButton.setOnClickListener(startListener);
        stopButton.setOnClickListener(stopListener);
        seekBar.setOnSeekBarChangeListener(seekBarListener);
    }

    SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //
        }
    };

    private void cleanMediaPlayer(){
        mp.stop();
        mp.release();
        mp = null;
    }

    View.OnClickListener startListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            playMusic();
        }
    };

    View.OnClickListener stopListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            stopMusic();
        }
    };

    private void playMusic(){
        try{
            if(mp != null && mp.isPlaying()){
                cleanMediaPlayer();
                //seekBar.setProgress(0);
                wasPlaying = true;
                startButton.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_play));
            }
            if(!wasPlaying) {
                if (mp == null) {
                    mp = new MediaPlayer();
                }
                startButton.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_pause));

                AssetFileDescriptor descriptor = getAssets().openFd("gtasa.mp3");
                mp.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());

                mp.prepare();
                mp.setLooping(false);

                seekBar.setMax(mp.getDuration());
                mp.seekTo(seekBar.getProgress());

                new Thread(this).start();

                mp.start();
            }
            if(wasPlaying){
                wasPlaying = false;
            }
        } catch(IOException e){
            Toast.makeText(this, "Файл не найден...", Toast.LENGTH_SHORT).show();
        } catch (Error e){
            Toast.makeText(this, "Произошла ошибка...", Toast.LENGTH_SHORT).show();
        }
    }

    private void pauseMusic(){
        if(mp.isPlaying()){
            startButton.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_play));
            mp.pause();
        }
    }

    private void stopMusic(){
        cleanMediaPlayer();
        seekBar.setProgress(0);
        wasPlaying = false;
        startButton.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_play));
    }

    @Override
    public void run() {
        int currentPosition = mp.getCurrentPosition();
        int total = mp.getDuration();

        while(mp != null && mp.isPlaying() && currentPosition < total){
            try {
                Thread.sleep(100);
                currentPosition = mp.getCurrentPosition();
            } catch (InterruptedException e) {
                Toast.makeText(this, "Error...", Toast.LENGTH_SHORT).show();
                return;
            } catch (Exception e){
                return;
            }
            seekBar.setProgress(currentPosition);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanMediaPlayer();
    }
}

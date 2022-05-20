package com.example.reproductor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;

public class ServicioMusica extends Service {

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String cancionUrl="";
    private int length = 0;

    public ServicioMusica(String cancionUrl){
        this.cancionUrl=cancionUrl;
    }

    public void onCreate(){
        super.onCreate();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void playOrPause() {

        if(mediaPlayer.isPlaying()){

            mediaPlayer.pause();
            length=mediaPlayer.getCurrentPosition();

        } else {

            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            try{
                mediaPlayer.setDataSource(cancionUrl);
                mediaPlayer.prepare();
                mediaPlayer.start();

            } catch (IOException e) {
                Log.d("msgError", e.getMessage());
            }
        }
    }

    public void stop (){

        try {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;

        } catch (IllegalStateException e) {
            Log.d("msgError", e.getMessage());
        }
    }

    public void seekto(int duration){
        if(mediaPlayer!= null)
        {
            mediaPlayer.seekTo(duration);}
    }

    public void resumeMusic()
    {
        if(mediaPlayer.isPlaying()==false)
        {
            mediaPlayer.seekTo(length);
            mediaPlayer.start();
        }
    }

    public void onDestroy ()
    {
        super.onDestroy();
        if(mediaPlayer != null)
        {
            try{
                mediaPlayer.stop();
                mediaPlayer.release();
            }finally {
                mediaPlayer = null;
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

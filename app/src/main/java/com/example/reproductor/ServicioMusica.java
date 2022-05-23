package com.example.reproductor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ServicioMusica extends Service {

    private MediaPlayer mediaPlayer= new MediaPlayer();
    private String cancionUrl="";
    private int length = 0;
    private ArrayList<String> listaUrl;
    private int pos;
    private static ServicioMusica servicioMusica=null;

    private ServicioMusica(){ }

    public static ServicioMusica getInstance(){

        if(servicioMusica == null) servicioMusica = new ServicioMusica();

        return servicioMusica;
    }

    public void setCancionUrlPorPosicion(ArrayList<String> listaUrl, int pos){
        this.listaUrl = listaUrl;
        this.pos = pos;

        cancionUrl = listaUrl.get(pos);
        Log.d("msgError", "cancion ---> " + cancionUrl);

        start();
    }

    public void setCancionUrl(String cancionUrl){
        this.cancionUrl=cancionUrl;

        start();
    }

    public void onCreate(){
        super.onCreate();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


    }

    public void next(){

        int lastPos = this.pos;

        if (lastPos==(this.listaUrl).size()-1){

            setCancionUrlPorPosicion(this.listaUrl, 0);
        } else {
            setCancionUrlPorPosicion(this.listaUrl, lastPos+1);
        }
    }

    public void previous(){

        int lastPos = this.pos;

        if (lastPos<1){

            setCancionUrlPorPosicion(this.listaUrl, this.listaUrl.size()-1);
        } else {
            setCancionUrlPorPosicion(this.listaUrl, lastPos-1);
        }
    }

    public void playOrPause() {

        if(mediaPlayer!=null){
            if(mediaPlayer.isPlaying()){

                mediaPlayer.pause();
                length=mediaPlayer.getCurrentPosition();

            } else {
                resumeMusic();
            }
        }
    }

    public String getCancionUrl() {
        return cancionUrl;
    }

    public void start(){

        if(mediaPlayer!=null){
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

            if(mediaPlayer!=null){
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
    }

    public void seekto(int duration){
        if (mediaPlayer!=null){
            if(mediaPlayer!= null) {
                mediaPlayer.seekTo(duration);}
        }
    }

    public void resumeMusic()
    {
        if (mediaPlayer!=null){
            if(mediaPlayer.isPlaying()==false)
            {
                mediaPlayer.seekTo(length);
                mediaPlayer.start();
            }
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

    public boolean isPlaying(){

        if (mediaPlayer.isPlaying()){
            return true;
        } else {
            return false;
        }
    }

    public void updatePlayingTime(){
        if (mediaPlayer != null){
            int millis = mediaPlayer.getCurrentPosition();
            long total_secs = TimeUnit.SECONDS.convert(millis, TimeUnit.MILLISECONDS);
            long mins = TimeUnit.MINUTES.convert(total_secs, TimeUnit.SECONDS);
            long secs = total_secs - (mins*60);
            //MainActivity.textview3.setText(mins + ":" + secs + " / " + duration);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

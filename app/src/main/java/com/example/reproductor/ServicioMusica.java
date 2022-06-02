package com.example.reproductor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
    private static ServicioMusica servicioMusica=null;

    private String cancionUrl="";
    private int length = 0;
    private int pos;

    private String cancion;
    private String artista;
    private String portada;

    private ArrayList<CancionInfo> listaCanciones;
    private CancionInfo cancionInfo;


    private ServicioMusica(){ }

    public static ServicioMusica getInstance(){

        if(servicioMusica == null) servicioMusica = new ServicioMusica();

        return servicioMusica;
    }

    public void setListaCanciones(ArrayList<CancionInfo> listaCanciones, CancionInfo cancionInfo){

        this.listaCanciones=listaCanciones;
        this.cancionInfo = cancionInfo;

        this.cancion = cancionInfo.getNombre();
        this.artista = cancionInfo.getArtista();
        this.portada = cancionInfo.getPortadaUrl();
        this.cancionUrl = cancionInfo.getCancionUrl();

        this.pos = listaCanciones.indexOf(cancionInfo);

        Log.d("msgError", "cancion ---> " + cancionUrl);

        start();
    }

    public void alTerminarSiguiente(){
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                next();
            }

        });
    }

    public void setCancion(int pos){

        this.pos = pos;

        this.cancion = this.listaCanciones.get(pos).getNombre();
        this.artista = this.listaCanciones.get(pos).getArtista();
        this.cancionUrl = this.listaCanciones.get(pos).getCancionUrl();

        if(!(this.listaCanciones.get(pos).getPortadaUrl()).toString().isEmpty()) {
            this.portada = this.listaCanciones.get(pos).getPortadaUrl();
        } else {
            this.portada=null;
        }

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

        if (lastPos==(this.listaCanciones).size()-1){

            setCancion(0);
        } else {
            setCancion(lastPos+1);
        }
    }

    public void previous(){

        int lastPos = this.pos;

        if(mediaPlayer.getCurrentPosition()<3000){

            if (lastPos<1){

                setCancion(this.listaCanciones.size()-1);
            } else {
                setCancion(lastPos-1);
            }

        } else {
            start();
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
    public int getDuracion(){
        return mediaPlayer.getDuration();
    }

    public String getStringDuracion(){

        long duracion = mediaPlayer.getDuration();
        String duracionString = "";
        String segundosString;

        int minutes = (int) (duracion % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((duracion % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (seconds < 10) {
            segundosString = "0" + seconds;
        } else {
            segundosString = "" + seconds;
        }

        duracionString = duracionString + minutes + ":" + segundosString;
        return duracionString;
    }

    public String getStringTiempo(){

        long tiempo= mediaPlayer.getCurrentPosition();
        String duracionString = "";
        String segundosString;

        int minutes = (int) (tiempo % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((tiempo % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (seconds < 10) {
            segundosString = "0" + seconds;
        } else {
            segundosString = "" + seconds;
        }

        duracionString = duracionString + minutes + ":" + segundosString;
        return duracionString;
    }

    public int getPosition(){ return mediaPlayer.getCurrentPosition(); }

    public String getCancion() {
        return cancion;
    }

    public void setCancion(String cancion) {
        this.cancion = cancion;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getPortada() {
        return portada;
    }

    public void setPortada(String portada) {
        this.portada = portada;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

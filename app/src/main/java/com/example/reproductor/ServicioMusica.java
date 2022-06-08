package com.example.reproductor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;

import java.io.IOException;
import java.util.ArrayList;

public class ServicioMusica extends Service {

    private MediaPlayer mediaPlayer= new MediaPlayer();
    private static ServicioMusica servicioMusica=null;

    private String cancionUrl="";
    private String portadaUrl="";
    private int length = 0;
    private int pos;

    private String cancion;
    private String artista;
    private Drawable portada;
    private boolean favorita=false;
    private ArrayList<String> listas;

    private ArrayList<CancionInfo> listaCanciones;
    private CancionInfo cancionInfo;
    private Context context;


    private ServicioMusica(){ }

    public static ServicioMusica getInstance(){

        if(servicioMusica == null) servicioMusica = new ServicioMusica();

        return servicioMusica;
    }

    public void setListaCanciones(ArrayList<CancionInfo> listaCanciones, CancionInfo cancionInfo, Context context, int pos){

        this.context = context;

        this.listaCanciones=listaCanciones;
        this.cancionInfo = cancionInfo;

        this.favorita = cancionInfo.isFavorita();
        this.cancion = cancionInfo.getNombre();
        this.artista = cancionInfo.getArtista();
        this.cancionUrl = cancionInfo.getCancionUrl();
        this.portadaUrl=cancionInfo.getPortadaUrl();

        cargarPortada();

        this.pos = pos;

        Log.d("msgError", "index ---> " + listaCanciones.indexOf(cancionInfo));

        start();
    }

    public void cargarPortada(){

        if (portadaUrl != null && !portadaUrl.isEmpty()) {

            Thread mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Drawable drawable = Glide.with(context)
                                .load(portadaUrl)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .submit()
                                .get();

                        portada = drawable;

                    } catch (Exception e) {
                        Log.d("msgError", "Error al cargar la imagen en ServicioMusica: "+e.getMessage());
                    }
                }
            });
            mThread.start();
        } else {
            portada= null;
        }
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

        this.portada=null;
        this.portadaUrl=null;
        this.pos = pos;

        this.cancion = this.listaCanciones.get(pos).getNombre();
        this.artista = this.listaCanciones.get(pos).getArtista();
        this.cancionUrl = this.listaCanciones.get(pos).getCancionUrl();

        if(this.listaCanciones.get(pos).getPortadaUrl()!=null){
            this.portadaUrl = this.listaCanciones.get(pos).getPortadaUrl();
        }

        cargarPortada();

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

        if(servicioMusica!=null||listaCanciones!=null){
            int lastPos = this.pos;

            if (lastPos==(this.listaCanciones).size()-1){

                setCancion(0);
            } else {

                setCancion(lastPos+1);
            }
        }
    }

    public void previous(){

        if(servicioMusica!=null||listaCanciones!=null){
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

        if(mediaPlayer!=null&&!cancionUrl.isEmpty()){
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

    public Drawable getPortada() {
        return portada;
    }

    public void setPortada(Drawable portada) {
        this.portada = portada;
    }

    public void setContext(Context context){
        this.context = context;
    }

    public boolean isFavorita() {
        return favorita;
    }

    public void setFavorita(boolean favorita) {
        this.favorita = favorita;
    }

    public CancionInfo getCancionInfo() {
        return cancionInfo;
    }

    public void setCancionInfo(CancionInfo cancionInfo) {
        this.cancionInfo = cancionInfo;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

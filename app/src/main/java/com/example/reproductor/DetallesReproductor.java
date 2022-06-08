package com.example.reproductor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class DetallesReproductor extends AppCompatActivity {

    private ImageButton iv_prevDR, iv_playDR, ib_seleccionarListaDR;
    private ImageView iv_portadaDR;
    private TextView tv_cancionDR, tv_artistaDR, tv_tiempoDR, tv_duracionDR;
    private SeekBar seekBarDR;
    private VideoView videoViewDR;
    private long mLastClickTime;

    private ServicioMusica servicioMusica;

    private Handler mHandler;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private StorageReference storageRef;
    private String userId;
    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_reproductor);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mReference = mFirebaseDatabase.getReference();
        storageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();

        servicioMusica = ServicioMusica.getInstance();

        ib_seleccionarListaDR = findViewById(R.id.ib_seleccionarListaDR);
        iv_prevDR = findViewById(R.id.iv_prevDR);
        iv_playDR = findViewById(R.id.iv_playDR);
        iv_portadaDR = findViewById(R.id.iv_portadaDR);
        tv_tiempoDR = findViewById(R.id.tv_tiempoDR);
        tv_duracionDR = findViewById(R.id.tv_duracionDR);
        tv_cancionDR = findViewById(R.id.tv_cancionDR);
        tv_artistaDR = findViewById(R.id.tv_artistaDR);
        seekBarDR = findViewById(R.id.seekBarDR);

        videoViewDR = findViewById(R.id.videoViewDR);
        startVideo();

        servicioMusica.alTerminarSiguiente();

        // Actualizar Barra de progreso
        seekBarDR.setMax(servicioMusica.getDuracion()/1000);
        mHandler= new Handler();
        DetallesReproductor.this.runOnUiThread(new Runnable(){
            @Override
            public void run() {

                if(servicioMusica!=null){
                    tv_tiempoDR.setText(servicioMusica.getStringTiempo());
                }

                if(servicioMusica != null){
                    int mCurrentPosition = servicioMusica.getPosition()/1000;
                    seekBarDR.setProgress(mCurrentPosition);
                }
                mHandler.postDelayed(this, 1000);
            }
        });

        // Actualizar progreso de la musica
        seekBarDR.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(seekBar.getProgress()==0) actualizarInfo();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(servicioMusica!=null){
                    servicioMusica.seekto(seekBar.getProgress()*1000);
                }
            }
        });

        actualizarInfo();

        if(servicioMusica.isPlaying()){
            iv_playDR.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_detalles));
        } else {
            iv_playDR.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_detalles));
        }
    }

    public void play(View view){

        servicioMusica.playOrPause();

        if(servicioMusica.isPlaying()){
            iv_playDR.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_detalles));
        } else {
            iv_playDR.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_detalles));
        }
    }

    public void prev(View view){

        // Evita pulsar varias veces seguidas
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
        mLastClickTime = SystemClock.elapsedRealtime();

        servicioMusica.setContext(getApplicationContext());
        servicioMusica.previous();
        iv_playDR.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_detalles));
        actualizarInfo();
    }

    public void next(View view){

        // Evita pulsar varias veces seguidas
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
        mLastClickTime = SystemClock.elapsedRealtime();

        servicioMusica.setContext(getApplicationContext());
        servicioMusica.next();
        iv_playDR.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_detalles));
        actualizarInfo();
    }

    public void actualizarInfo(){
        tv_duracionDR.setText(String.valueOf(servicioMusica.getStringDuracion()));

        tv_cancionDR.setText(servicioMusica.getCancion());
        tv_artistaDR.setText(servicioMusica.getArtista());

        if(servicioMusica.getPortada()!=null){
            iv_portadaDR.setImageDrawable(servicioMusica.getPortada());
        } else {
            iv_portadaDR.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.placeholder));
        }

        seekBarDR.setMax(servicioMusica.getDuracion()/1000);

        int posicion=videoViewDR.getCurrentPosition();
        videoViewDR.stopPlayback();
        startVideo();
        videoViewDR.seekTo(posicion+500);
    }

    public void borrarItem(View view){

        new AlertDialog.Builder(this, R.style.Base_Theme_Material3_Dark_Dialog)
                .setTitle("Eliminar cancion")
                .setMessage("¿Esta seguro de que desea borrar esta cancion? Esta accion no se podra deshacer.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String nombre = tv_cancionDR.getText().toString();
                        String cancionUrl = servicioMusica.getCancionUrl();

                        servicioMusica.next();

                        // Borrar de Storage
                        StorageReference desertRef = storageRef.child(cancionUrl);

                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("msgError", "Cancion eliminada");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("msgError", "No se ha podido eliminar de Storage"+e.getMessage());
                            }
                        });

                        //Borrar de la base de datos
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(userId).child("canciones");
                        Query query = ref.orderByChild("nombre").equalTo(nombre);
                        ValueEventListener listener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds : dataSnapshot.getChildren()){

                                    ds.getRef().removeValue();
                                    finish();
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError e) {
                                Log.d("msgError", "No se ha podido eliminar de Database"+e.getMessage());
                            }
                        };
                        query.addValueEventListener(listener);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                })
                .show();
    }

    private void startVideo(){
                Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video_background);
                videoViewDR.setVideoURI(uri);
                videoViewDR.start();

                videoViewDR.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                        float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
                        float screenRatio = videoViewDR.getWidth() / (float)
                                videoViewDR.getHeight();
                        float scaleX = videoRatio / screenRatio;
                        if (scaleX >= 1f) {
                            videoViewDR.setScaleX(scaleX);
                        } else {
                            videoViewDR.setScaleY(1f / 1.7f);
                        }
                    }
                });
    }

    public void selecccionarLista(View view){

        startActivity(new Intent(this, SeleccionarLista.class));
    }

    protected void onPause(){
        super.onPause();
        videoViewDR.stopPlayback();
    }

    protected void onResume(){
        super.onResume();
        videoViewDR.start();
    }

    protected void onDestroy(){
        super.onDestroy();
        setResult(Activity.RESULT_CANCELED, null);
        finish();
    }
}
package com.example.reproductor.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.reproductor.Buscar;
import com.example.reproductor.CancionInfo;
import com.example.reproductor.Canciones;
import com.example.reproductor.DetallesReproductor;
import com.example.reproductor.Listas;
import com.example.reproductor.MenuReproductor;
import com.example.reproductor.R;
import com.example.reproductor.ServicioMusica;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private Activity mActivity;
    private ArrayList<CancionInfo> mContentList;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;
    private String userId;
    private long mLastClickTime;
    public static MenuReproductor menuReproductor;

    public RecyclerAdapter(Context mContext, Activity mActivity, ArrayList<CancionInfo> mContentList) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mContentList = mContentList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cancion, parent, false);
        return new ViewHolder(view, viewType);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardViewC;
        private ImageView iv_portadaC;
        private TextView tv_nCancionC, tv_artistaC;
        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            cardViewC = itemView.findViewById(R.id.cardViewC);
            iv_portadaC = itemView.findViewById(R.id.iv_portadaC);
            tv_nCancionC = itemView.findViewById(R.id.tv_nCancionC);
            tv_artistaC = itemView.findViewById(R.id.tv_artistaMR);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mainHolder, int position) {
        ViewHolder holder = (ViewHolder) mainHolder;
        holder.setIsRecyclable(false);
        final CancionInfo cancionInfo = mContentList.get(position);

        // Añadiendo los datos al recyclerView
        String imgUrl = cancionInfo.getPortadaUrl();
        if (imgUrl != null && !imgUrl.isEmpty()) {
            Glide.with(mContext)
                    .load(imgUrl)
                    .into(holder.iv_portadaC);
        }
        holder.tv_nCancionC.setText(cancionInfo.getNombre());
        holder.tv_artistaC.setText(cancionInfo.getArtista());

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mReference = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();

        menuReproductor = new MenuReproductor();

        // Reproducir cancion segun el item
        mainHolder.itemView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                try {
                    // La siguiente condicion evita que pulse varias veces seguidas
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                    mLastClickTime = SystemClock.elapsedRealtime();

                    ArrayList<CancionInfo> listaCanciones = new ArrayList<>();


                    int pos = mainHolder.getAdapterPosition();

                    mReference.child("canciones").child(userId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {

                            //Recoger todas las canciones en listas
                            for(DataSnapshot dataSnapshot: snapshot.getChildren()){

                                CancionInfo cancionInfo= new CancionInfo();
                                cancionInfo.setCancionUrl(dataSnapshot.child("cancionUrl").getValue(String.class));
                                cancionInfo.setNombre(dataSnapshot.child("nombre").getValue(String.class));
                                cancionInfo.setArtista(dataSnapshot.child("artista").getValue(String.class));
                                cancionInfo.setPortadaUrl(dataSnapshot.child("nombre").getValue(String.class));

                                listaCanciones.add(cancionInfo);
                            }

                            AppCompatActivity activity = (AppCompatActivity) view.getContext();

                            // Iniciar servicio de musica
                            ServicioMusica servicioMusica = ServicioMusica.getInstance();
                            servicioMusica.setListaCanciones(listaCanciones, cancionInfo);

                            // Iniciar el fragmento con el menu para controlar la musica
                            FragmentManager manager = activity.getSupportFragmentManager();
                            if(activity.getClass()== Canciones.class) manager.beginTransaction().replace(R.id.fl_reproductorC, menuReproductor).commit();
                            if(activity.getClass()== Buscar.class) manager.beginTransaction().replace(R.id.fl_reproductorB, menuReproductor).commit();
                            if(activity.getClass()== Listas.class) manager.beginTransaction().replace(R.id.fl_reproductorL, menuReproductor).commit();

                            menuReproductor.actualizarMenu();
                            activity.startActivity(new Intent(view.getContext().getApplicationContext(), DetallesReproductor.class));
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                            Log.d("msgError", databaseError.getMessage());
                        }
                    });
                } catch (Exception e){
                    Log.d("msgError", "Error al seleccionar cancion ----> "+e.getMessage());
                }
            }
        });

    }
    @Override
    public int getItemCount() {
        if(mContentList!=null){
            return mContentList.size();
        }
        return 0;
    }
}

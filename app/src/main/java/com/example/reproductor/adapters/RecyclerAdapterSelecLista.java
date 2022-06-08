package com.example.reproductor.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reproductor.CancionInfo;
import com.example.reproductor.ListaCanciones;
import com.example.reproductor.ListaInfo;
import com.example.reproductor.Listas;
import com.example.reproductor.R;
import com.example.reproductor.SeleccionarLista;
import com.example.reproductor.ServicioMusica;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecyclerAdapterSelecLista extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ServicioMusica servicioMusica;

    private Context mContext;
    private Activity mActivity;
    private ArrayList<ListaInfo> mContentList;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;
    private String userId;
    private long mLastClickTime;

    public RecyclerAdapterSelecLista(Context mContext, SeleccionarLista listas, ArrayList<ListaInfo> mContentList) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mContentList = mContentList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista, parent, false);
        return new RecyclerAdapterSelecLista.ViewHolder(view, viewType);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_nListaL;
        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            tv_nListaL = itemView.findViewById(R.id.tv_nListaL);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mainHolder, int position) {
        RecyclerAdapterSelecLista.ViewHolder holder = (RecyclerAdapterSelecLista.ViewHolder) mainHolder;
        final ListaInfo listaInfo = mContentList.get(position);

        holder.tv_nListaL.setText(listaInfo.getNombre());

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mReference = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();

        // Abrir lista
        mainHolder.itemView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                view.getContext().startActivity(new Intent(view.getContext(), SeleccionarLista.class));

                CancionInfo cancionInfo = servicioMusica.getCancionInfo();
                Map<String, Object> cancionMap  = new HashMap<String, Object>();
                cancionMap.put("nombre", cancionInfo.getNombre());
                cancionMap.put("artista", cancionInfo.getArtista());
                cancionMap.put("album", cancionInfo.getAlbum());
                cancionMap.put("genero", cancionInfo.getGenero());
                cancionMap.put("cancionUrl", cancionInfo.getCancionUrl());
                cancionMap.put("favorita", cancionInfo.isFavorita());
                //cancionMap.put("listas", cancionInfo.getListas());

                String portadaUrl=cancionInfo.getPortadaUrl();
                if(portadaUrl!=null)cancionMap.put("portadaUrl", portadaUrl);

                mReference.child(userId).child("canciones").child("id-"+cancionInfo.getNombre()).updateChildren(cancionMap);
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

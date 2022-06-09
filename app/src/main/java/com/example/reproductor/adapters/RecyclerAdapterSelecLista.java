package com.example.reproductor.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reproductor.CancionInfo;
import com.example.reproductor.ListaInfo;
import com.example.reproductor.R;
import com.example.reproductor.SeleccionarLista;
import com.example.reproductor.ServicioMusica;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        servicioMusica=ServicioMusica.getInstance();

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
        holder.setIsRecyclable(false);
        holder.tv_nListaL.setText(listaInfo.getNombre());

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mReference = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();

        // Abrir lista
        mainHolder.itemView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                TextView tv_nListaL =  view.findViewById(R.id.tv_nListaL);
                String lista = tv_nListaL.getText().toString();

                CancionInfo cancionInfo = servicioMusica.getCancionInfo();

                boolean repetida=false;
                for(String s: cancionInfo.getListas()){
                    if (s!=null) {
                        if(s.equals(lista)) repetida=true;
                    }
                }

                if(repetida){

                    Toast.makeText(view.getContext().getApplicationContext(), "La cancion ya se encuentra en esa lista", Toast.LENGTH_SHORT).show();

                } else {
                    cancionInfo.setLista(lista);

                    Map<String, Object> cancionMap  = new HashMap<String, Object>();
                    cancionMap.put("nombre", cancionInfo.getNombre());
                    cancionMap.put("artista", cancionInfo.getArtista());
                    cancionMap.put("album", cancionInfo.getAlbum());
                    cancionMap.put("genero", cancionInfo.getGenero());
                    cancionMap.put("cancionUrl", cancionInfo.getCancionUrl());
                    cancionMap.put("favorita", cancionInfo.isFavorita());
                    cancionMap.put("listas", cancionInfo.getListas());

                    String portadaUrl=cancionInfo.getPortadaUrl();
                    if(portadaUrl!=null)cancionMap.put("portadaUrl", portadaUrl);

                    mReference.child(userId).child("canciones").child("id-"+cancionInfo.getNombre()).updateChildren(cancionMap);

                    Toast.makeText(view.getContext().getApplicationContext(), "Se ha añadido la cancion a "+lista, Toast.LENGTH_SHORT).show();
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

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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reproductor.Buscar;
import com.example.reproductor.CancionInfo;
import com.example.reproductor.Canciones;
import com.example.reproductor.DetallesReproductor;
import com.example.reproductor.ListaCanciones;
import com.example.reproductor.Listas;
import com.example.reproductor.MenuReproductor;
import com.example.reproductor.R;
import com.example.reproductor.ServicioMusica;
import com.example.reproductor.ListaInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecyclerAdapterListas extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private Activity mActivity;
    private ArrayList<ListaInfo> mContentList;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;
    private String userId;
    private long mLastClickTime;

    public RecyclerAdapterListas(Context applicationContext, Listas listas, ArrayList<ListaInfo> listadeListas) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mContentList = mContentList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardViewL;
        private TextView tv_nListaL, tv_numCancionesL;
        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            cardViewL = itemView.findViewById(R.id.cardViewL);
            tv_nListaL = itemView.findViewById(R.id.tv_nListaL);
            tv_numCancionesL = itemView.findViewById(R.id.tv_numCancionesL);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista, parent, false);
        return new RecyclerAdapter.ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mainHolder, int position) {
        RecyclerAdapterListas.ViewHolder holder = (RecyclerAdapterListas.ViewHolder) mainHolder;
        holder.setIsRecyclable(false);
        final ListaInfo listaInfo = mContentList.get(position);

        // Añadiendo los datos al recyclerView
        holder.tv_nListaL.setText(listaInfo.getNombre());
        holder.tv_numCancionesL.setText(listaInfo.getNumCanciones());

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mReference = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();

        // Abrir lista
        mainHolder.itemView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                try {
                    // La siguiente condicion evita que pulse varias veces seguidas
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                    mLastClickTime = SystemClock.elapsedRealtime();

                    ArrayList<ListaInfo> listaDeListas = new ArrayList<>();

                    int pos = mainHolder.getAdapterPosition();

                    AppCompatActivity activity = (AppCompatActivity) view.getContext();

                    activity.startActivity(new Intent(view.getContext().getApplicationContext(), ListaCanciones.class));

                } catch (Exception e){
                    Log.d("msgError", "Error al seleccionar la lista: "+e.getMessage());
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

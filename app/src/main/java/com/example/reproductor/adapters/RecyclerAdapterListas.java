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

import com.example.reproductor.ListaPersonalizada;
import com.example.reproductor.Listas;
import com.example.reproductor.R;
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

    public RecyclerAdapterListas(Context mContext, Listas listas, ArrayList<ListaInfo> mContentList) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mContentList = mContentList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista, parent, false);
        return new RecyclerAdapterListas.ViewHolder(view, viewType);
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
        RecyclerAdapterListas.ViewHolder holder = (RecyclerAdapterListas.ViewHolder) mainHolder;
        final ListaInfo listaInfo = mContentList.get(position);

        // A??adiendo los datos al recyclerView
        holder.tv_nListaL.setText(listaInfo.getNombre());

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
                    int pos = mainHolder.getAdapterPosition();

                    ArrayList<ListaInfo> listaDeListas = new ArrayList<>();

                    mReference.child(userId).child("listas").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {

                            for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                                ListaInfo listaInfo= new ListaInfo();
                                listaInfo.setNombre(dataSnapshot.child("nombre").getValue(String.class));
                                listaDeListas.add(listaInfo);
                            }

                            AppCompatActivity activity = (AppCompatActivity) view.getContext();

                            Intent i = new Intent(view.getContext().getApplicationContext(), ListaPersonalizada.class);
                            i.putExtra("nLista", listaDeListas.get(pos).getNombre());
                            activity.startActivity(i);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("msgError", "Error al seleccionar la lista: "+error.getMessage());
                        }
                    });

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

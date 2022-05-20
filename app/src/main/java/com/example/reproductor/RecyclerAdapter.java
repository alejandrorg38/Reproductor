package com.example.reproductor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private Activity mActivity;
    private ArrayList<CancionInfo> mContentList;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;
    private String userId;

    public RecyclerAdapter(Context mContext, Activity mActivity, ArrayList<CancionInfo> mContentList) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mContentList = mContentList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
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
            tv_artistaC = itemView.findViewById(R.id.tv_artistaC);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mainHolder, int position) {
        ViewHolder holder = (ViewHolder) mainHolder;
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

        MenuReproductor menuReproductor = new MenuReproductor();

        // Reproducir cancion segun el item
        mainHolder.itemView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                ArrayList<String> listaUrl = new ArrayList<>();

                int pos = mainHolder.getAdapterPosition();

                mReference.child("canciones").child(userId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {

                                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                                    String cancionesUrl = dataSnapshot.child("cancionUrl").getValue(String.class);

                                    listaUrl.add(cancionesUrl);
                                    Log.d("msgError", cancionesUrl);
                                }

                                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                                menuReproductor.setCancion(listaUrl,pos);
                                activity.getSupportFragmentManager().beginTransaction().add(R.id.fl_reproductor, menuReproductor).commit();
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                                Log.d("msgError", databaseError.getMessage());
                            }
                        });
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

package com.example.reproductor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reproductor.adapters.RecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OpcionesLista extends AppCompatActivity {

    private ServicioMusica servicioMusica;

    private CircularProgressIndicator progressIndicator;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ArrayList<CancionInfo> listaCanciones;

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter = null;

    private ImageButton ib_atrasOL, ib_eliminarOL;
    private TextView tv_sinCancionesOL, tv_nListaOL;
    String nombreLista, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones_lista);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();

        Bundle bundle = getIntent().getExtras();
        nombreLista = bundle.getString("nLista");
        tv_nListaOL = findViewById(R.id.tv_nListaOL);
        tv_nListaOL.setText(nombreLista);
        tv_sinCancionesOL = findViewById(R.id.et_sinCancionesOL);
        progressIndicator = findViewById(R.id.progress_circularOL);
        ib_atrasOL = findViewById(R.id.ib_atrasOL);
        ib_eliminarOL = findViewById(R.id.ib_eliminarOL);

        // Rellenar RecyclerView
        recyclerView = findViewById(R.id.rv_lista_cancionesOL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, GridLayoutManager.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        listaCanciones = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference().child(userId).child("canciones");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    tv_sinCancionesOL.setVisibility(View.INVISIBLE);
                    listaCanciones.clear();

                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        CancionInfo cancionInfo = dataSnapshot.getValue(CancionInfo.class);
                        listaCanciones.add(cancionInfo);
                    }

                    //Filtrar RecyclerView segun la lista
                    ArrayList<CancionInfo> lista = new ArrayList<>();

                    for(CancionInfo obj:listaCanciones){
                        for(String s: obj.getListas()){
                            if(s.equalsIgnoreCase(nombreLista)) lista.add(obj);
                        }
                    }

                    recyclerAdapter = new RecyclerAdapter(getApplicationContext(), OpcionesLista.this, (ArrayList<CancionInfo>) lista);
                    recyclerView.setAdapter(recyclerAdapter);
                    recyclerAdapter.notifyDataSetChanged();
                    progressIndicator.setVisibility(View.GONE);

                } else {
                    progressIndicator.setVisibility(View.GONE);
                    tv_sinCancionesOL.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getApplicationContext(), "Error al cargar la musica", Toast.LENGTH_SHORT).show();
            }
        });

        // Inicializacion del menu de navegacion inferior
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.listas);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {


            // Respondiendo a las acciones del menu
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.buscar:
                        startActivity(new Intent(getApplicationContext(),Buscar.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.canciones:
                        startActivity(new Intent(getApplicationContext(),Canciones.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.listas:
                        return true;
                }
                return false;
            }
        });
    }

    public void eliminarLista(View view){

        new AlertDialog.Builder(this, R.style.Base_Theme_Material3_Dark_Dialog)
                .setTitle("Eliminar lista")
                .setMessage("¿Esta seguro de que desea borrar esta lista? Esta accion no se podra deshacer.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(userId).child("listas");
                        Query query = ref.orderByChild("nombre").equalTo(nombreLista);
                        ValueEventListener listener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds : dataSnapshot.getChildren()){

                                    ds.getRef().removeValue();
                                    startActivity(new Intent(view.getContext(), Listas.class));
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

    public void atras(View view){
        finish();
        overridePendingTransition(0,0);
    }
}
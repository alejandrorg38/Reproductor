package com.example.reproductor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class AnadirLista extends AppCompatActivity {

    private EditText et_nombreListaAL;
    private Button b_anadirAL;

    private ListaInfo listaInfo;

    private StorageReference storageRef;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir_lista);

        et_nombreListaAL = findViewById(R.id.et_nombreListaAL);
        b_anadirAL = findViewById(R.id.b_andirAL);

        storageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mReference = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();
    }


    public void crearLista(View view){

        listaInfo = new ListaInfo();
        listaInfo.setNombre((et_nombreListaAL.getText()).toString());

        if (!listaInfo.getNombre().isEmpty()) {
            DatabaseReference listaDataRef = mReference.child(userId).child("listas").child("id-"+listaInfo.getNombre());
            listaDataRef.setValue(listaInfo);

            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Debes darle un nombre a la lista", Toast.LENGTH_SHORT).show();
        }


        //        mReference.child(userId).child("listas").child("id-"+nombre);
//        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(!snapshot.exists()){
//                    listaInfo.setNombre(nombre);
//                    DatabaseReference listaDataRef = mReference.child(userId).child("listas").child("id-"+listaInfo.getNombre());
//                    listaDataRef.setValue(listaInfo);
//                    finish();
//                }
//            }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getApplicationContext(), "Esta lista ya existe", Toast.LENGTH_SHORT).show();
//                Log.d("msgError", "Error AñadirLista:"+ error.getMessage());
//            }
//        });
    }
}
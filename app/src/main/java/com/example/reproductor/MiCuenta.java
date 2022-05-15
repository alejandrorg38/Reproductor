package com.example.reproductor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MiCuenta extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextView tv_emailMC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_cuenta);

        mAuth = FirebaseAuth.getInstance();

        tv_emailMC = findViewById(R.id.tv_emailMC);
        tv_emailMC.setText((mAuth.getCurrentUser()).getEmail());
    }

    public void cerrarSesion(View view){

        mAuth.signOut();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void borrarCuenta(View view){

        Intent i = new Intent(this, BorrarCuenta.class);
        startActivity(i);
    }
}
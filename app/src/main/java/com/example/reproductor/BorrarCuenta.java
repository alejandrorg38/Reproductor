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

public class BorrarCuenta extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextView tv_emailBC;
    private EditText et_contrasenaBC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrar_cuenta);

        mAuth = FirebaseAuth.getInstance();

        tv_emailBC = findViewById(R.id.tv_emailBC);
        tv_emailBC.setText((mAuth.getCurrentUser()).getEmail());

        et_contrasenaBC = findViewById(R.id.et_contrasenaBC);
    }

    public void botonBorrar(View view){

        String email = (tv_emailBC.getText()).toString();
        String password = (et_contrasenaBC.getText()).toString();

        if(!(password.isEmpty())) {
            borrarUsuario(email, password);
        }
    }

    public void borrarUsuario(String email, String password){

        new AlertDialog.Builder(this, R.style.Base_Theme_Material3_Dark_Dialog)
                .setTitle("Eliminar cuenta")
                .setMessage("¿Esta seguro de que desea eliminar su cuenta? Esta accion no se podra deshacer.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        // Recoge las credenciales para eliminar la cuenta
                        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

                        if (user != null) {
                            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("msgError", "Cuenta eliminada.");

                                                salir();

                                            } else {
                                                Toast.makeText(BorrarCuenta.this, "Error de autenticacion", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                    })
                .show();
    }

    public void salir(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
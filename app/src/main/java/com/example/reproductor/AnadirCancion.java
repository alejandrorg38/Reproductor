package com.example.reproductor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class AnadirCancion extends AppCompatActivity {

    StorageReference storageRef;

    private TextView tv_albumAC, tv_artistaAC, tv_generoAC, tv_cancionAC;
    private EditText et_albumAC, et_artistaAC, et_generoAC, et_cancionAC;
    private Button b_subirAC, b_archivoAC, b_imagenAC;
    private ImageView iv_imagenAC;
    private String userId, nombreImagen="";
    private Uri uri;

    private CancionInfo cancionInfo;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir_cancion);

        storageRef = FirebaseStorage.getInstance().getReference();

        tv_cancionAC = findViewById(R.id.tv_cancionAC);
        tv_albumAC = findViewById(R.id.tv_albumAC);
        tv_artistaAC = findViewById(R.id.tv_artistaAC);
        tv_generoAC = findViewById(R.id.tv_generoAC);

        et_cancionAC = findViewById(R.id.et_cancionAC);
        et_albumAC = findViewById(R.id.et_albumAC);
        et_artistaAC = findViewById(R.id.et_artistaAC);
        et_generoAC = findViewById(R.id.et_generoAC);

        b_imagenAC = findViewById(R.id.b_imagenAC);
        b_subirAC = findViewById(R.id.b_subirAC);
        b_archivoAC = findViewById(R.id.b_archivoAC);

        iv_imagenAC = findViewById(R.id.iv_imagen);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mReference = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();
    }


    // Abrir el buscador de archivos
    public void seleccionar(View view){

        b_imagenAC.setText("Añadir imagen");

        // Ver el buscador de archivos
        if (view.getId()==R.id.b_archivoAC) {
            Intent i = new Intent();
            i.setType("audio/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(i, 1);

        } else if(view.getId()==R.id.b_imagenAC){
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(i, 1);
        }
    }

    public void subirArchivo(View view){
        if (uri != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Subiendo archivo...");
            progressDialog.show();

            StorageReference cancionRef = storageRef.child((et_cancionAC.getText()).toString());

            // Agregar metadatos
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("audio/mpeg")
                    .setCustomMetadata("Album", (et_albumAC.getText()).toString())
                    .setCustomMetadata("Artista", (et_artistaAC.getText()).toString())
                    .setCustomMetadata("Genero", (et_generoAC.getText()).toString())
                    .build();

            // Subir cancion
            cancionRef.putFile(uri, metadata)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            guardarCancion();

                            cancionRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                            {
                                @Override
                                public void onSuccess(Uri downloadUrl)
                                {
                                    String cancionUrlReference = downloadUrl.toString();

                                    mReference.child("canciones").child(userId).child(cancionInfo.getKey()).child("cancionUrl").setValue(cancionUrlReference);
                                    cancionInfo.setCancionUrl(cancionUrlReference);

                                    Toast.makeText(getApplicationContext(), "Archivo subido", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "No se ha podido subir el archivo", Toast.LENGTH_SHORT).show();
                            Log.d("msgError", "Error al subir el archivo: " + e.getMessage());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progreso = (100.0 * snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                            progressDialog.setMessage(String.valueOf((int)progreso)+"%");
                        }
                    });


            // Subir imagen si exite

            if (!nombreImagen.isEmpty()) {
                StorageReference imagenRef = storageRef.child((nombreImagen).substring(0, nombreImagen.length() - 4));

                StorageMetadata metadataImagen = new StorageMetadata.Builder()
                        .setContentType("image/jpeg")
                        .build();

                imagenRef.putFile(uri, metadataImagen)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                imagenRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                                {
                                    @Override
                                    public void onSuccess(Uri downloadUrl)
                                    {
                                        String imagenUrlReference = "";
                                        imagenUrlReference = downloadUrl.toString();

                                        mReference.child("canciones").child(userId).child(cancionInfo.getKey()).child("portadaUrl").setValue(imagenUrlReference);
                                        cancionInfo.setPortadaUrl(imagenUrlReference);
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("msgError", "Error al subir la portada: " + e.getMessage());
                            }
                        });
            }
        } else {

            Toast.makeText(getApplicationContext(), "No hay ningun archivo seleccionado", Toast.LENGTH_SHORT).show();
            Log.d("msgError", "No se encontro la uri - Uri: " + uri.toString());
        }
    }

    // Guarda los datos de la cancion en la base de datos
    public void guardarCancion(){

        cancionInfo = new CancionInfo();
        cancionInfo.setNombre((et_cancionAC.getText()).toString());
        cancionInfo.setArtista((et_artistaAC.getText()).toString());
        cancionInfo.setAlbum((et_albumAC.getText()).toString());
        cancionInfo.setGenero((et_generoAC.getText()).toString());

        String id = mReference.push().getKey();

        DatabaseReference cancionDataRef = mReference.child("canciones").child(userId).push();
        cancionInfo.setCancionUrl("");
        cancionDataRef.setValue(cancionInfo);

        // Conseguir la key de la cancion
        String key = cancionDataRef.getKey();
        cancionInfo.setKey(key);
    }

    //Selecciona uno de los archivos y muestra la informacion
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == 1){

            if(resultCode == RESULT_OK){

                uri = data.getData();

                String extension = null;
                extension = getExtension(uri);

                if (extension.equals("mp3")) {
                    DocumentFile archivo = DocumentFile.fromSingleUri(this, uri);

                    String nombreCancion = archivo.getName();
                    String nombreArchivo = archivo.getName();

                    // Se recorta el nombre del archivo si es demasiado largo
                    if(nombreArchivo.length()>30){
                        b_archivoAC.setText(nombreArchivo.substring(0, Math.min(nombreArchivo.length(), 30)) + "...");
                    } else {
                        b_archivoAC.setText(nombreArchivo);
                    }

                    et_cancionAC.setText(nombreCancion.substring(0, nombreCancion.length() - 4));

                    // Se recopilan los metadatos del archivo
                    try {
                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                        mmr.setDataSource(this, uri);

                        et_artistaAC.setText(mmr.extractMetadata((MediaMetadataRetriever.METADATA_KEY_ARTIST)));
                        et_albumAC.setText(mmr.extractMetadata((MediaMetadataRetriever.METADATA_KEY_ALBUM)));
                        et_generoAC.setText(mmr.extractMetadata((MediaMetadataRetriever.METADATA_KEY_GENRE)));

                        byte[] imagen = mmr.getEmbeddedPicture();

                        if (imagen != null) {
                            Bitmap imagenCancion = BitmapFactory
                                    .decodeByteArray(imagen, 0, b_imagenAC.length());
                            iv_imagenAC.setImageBitmap(imagenCancion);
                            iv_imagenAC.setVisibility(View.VISIBLE);
                        }

                    }catch (Exception e){
                        Log.d("msgError", "\nError al extraer metadatos: " + e.getMessage() +"\n Path: " + uri.getPath());
                    }

                    tv_cancionAC.setVisibility(View.VISIBLE);
                    tv_albumAC.setVisibility(View.VISIBLE);
                    tv_artistaAC.setVisibility(View.VISIBLE);
                    tv_generoAC.setVisibility(View.VISIBLE);
                    et_cancionAC.setVisibility(View.VISIBLE);
                    et_albumAC.setVisibility(View.VISIBLE);
                    et_artistaAC.setVisibility(View.VISIBLE);
                    et_generoAC.setVisibility(View.VISIBLE);
                    b_imagenAC.setVisibility(View.VISIBLE);
                    b_subirAC.setVisibility(View.VISIBLE);

                } else if (extension.equals("jpg")||extension.equals("png")){

                    DocumentFile archivo = DocumentFile.fromSingleUri(this, uri);

                    nombreImagen = archivo.getName();

                    try {

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);

                        Bitmap imagenCancion = ThumbnailUtils.extractThumbnail(bitmap, 480, 480);

                        if (imagenCancion!=null) {
                            iv_imagenAC.setImageBitmap(imagenCancion);
                            iv_imagenAC.setVisibility(View.VISIBLE);
                            b_imagenAC.setText("Cambiar imagen");
                        }

                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "No se ha podido actualizar la imagen", Toast.LENGTH_SHORT).show();
                        Log.d("msgError", "Excepcion: " + e.getMessage());
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "La extension del archivo no esta permitida", Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Devuelve la extension del archivo
    public String getExtension(Uri uri)
    {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
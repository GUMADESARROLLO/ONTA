package com.a7m.endscom.isbot.Actividades;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.a7m.endscom.isbot.Clases.ApiRest;
import com.a7m.endscom.isbot.Clases.Usuario;
import com.a7m.endscom.isbot.DB.Database;
import com.a7m.endscom.isbot.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class NuevoClienteActivity extends AppCompatActivity {
    Intent iCliente;
    String Folder = "BaseImagenes";
    TextView Nombre,Direccion,CodCliente,latitud,longitud;
    LocationManager locationManager;
    Location location;
    LocationListener locationListener;
    Usuario Agente;
    AlertDialog alert = null;
    Database myDB;
    private ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_cliente);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("POSICION");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        final Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final File imagesFolder = new File(Environment.getExternalStorageDirectory(), Folder);
        imagesFolder.mkdirs();
        iCliente = getIntent();
        img = (ImageView)this.findViewById(R.id.foto);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File image = new File(imagesFolder, iCliente.getStringExtra("IdCliente") + ".jpg");
                Uri uriSavedImage = Uri.fromFile(image);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                startActivityForResult(cameraIntent, 1);
            }
        });


        myDB = new Database(this);

        Nombre = (TextView) findViewById(R.id.campo_nombre);
        Direccion = (TextView) findViewById(R.id.campo_Direccion);
        CodCliente = (TextView) findViewById(R.id.campo_codCl);
        latitud = (TextView) findViewById(R.id.campo_lati);
        longitud = (TextView) findViewById(R.id.campo_long);

        CodCliente.setText(iCliente.getStringExtra("IdCliente"));
        Nombre.setText(iCliente.getStringExtra("ClsNombre"));
        Direccion.setText(iCliente.getStringExtra("CLdir"));
        Cursor logRes =  myDB.getPosicion(iCliente.getStringExtra("IdCliente"));
        if (logRes.getCount()!=0){
            if (logRes.moveToFirst()) {
                do {
                    latitud.setText(logRes.getString(4));
                    longitud.setText(logRes.getString(5));
                } while(logRes.moveToNext());
            }
        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            AlertNoGps();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            } else {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        } else {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        MostrarLocalizacion(location);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                MostrarLocalizacion(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (location!=null){
                    if (myDB.insertPosicion(Agente.getIdVendedor(),Agente.getNombre(),CodCliente.getText().toString(),Nombre.getText().toString(),latitud.getText().toString(),longitud.getText().toString(),iCliente.getStringExtra("Estado"))){
                        myDB.UpdateEstado(CodCliente.getText().toString(),"1");
                        finish();
                    }else{
                        Toast.makeText(NuevoClienteActivity.this, "Ocurrio un problema, no se pudo guardar la posición", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(NuevoClienteActivity.this, "El Dispositivo no sea Triangulado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.idbtnClla).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               //MostrarLocalizacion(location);
                if (location!=null){
                    postImage(CodCliente.getText().toString(),Environment.getExternalStorageDirectory()+"/"+ Folder + "/" + iCliente.getStringExtra("IdCliente") + ".jpg",NuevoClienteActivity.this);
                    if (myDB.insertPosicion(Agente.getIdVendedor(),Agente.getNombre(),CodCliente.getText().toString(),Nombre.getText().toString(),latitud.getText().toString(),longitud.getText().toString(),iCliente.getStringExtra("Estado"))){
                        myDB.UpdateEstado(CodCliente.getText().toString(),"1");
                        finish();
                    }else{
                        Toast.makeText(NuevoClienteActivity.this, "Ocurrio un problema, no se pudo guardar la posición", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(NuevoClienteActivity.this, "El Dispositivo no sea Triangulado", Toast.LENGTH_SHORT).show();
                }


            }
        });

        if (new File(Environment.getExternalStorageDirectory(), Folder + "/" + iCliente.getStringExtra("IdCliente") + ".jpg").exists()){
            img.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/"+Folder+"/"+iCliente.getStringExtra("IdCliente")+".jpg"));
        }

        //uploadFoto(new File(Environment.getExternalStorageDirectory(), Folder + "/" + iCliente.getStringExtra("IdCliente") + ".jpg"));

    }
    public void MostrarLocalizacion(Location loc){
        if (loc!=null){
            latitud.setText(String.valueOf(loc.getLatitude()));
            longitud.setText(String.valueOf(loc.getLongitude()));
        }else{
            Toast.makeText(NuevoClienteActivity.this, "El Dispositivo no sea Triangulado", Toast.LENGTH_SHORT).show();
        }



    }
    private void AlertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        finish();
                    }
                });
        alert = builder.create();
        alert.show();
    }
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == 16908332){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap bMap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/"+Folder+"/"+iCliente.getStringExtra("IdCliente")+".jpg");
            img.setImageBitmap(bMap);
        }
    }
    public static void postImage(String codCls,String ImageLink, final Context context){
        final ProgressDialog pdialog;
        RequestParams params = new RequestParams();
        pdialog = ProgressDialog.show(context, "","Procesando, ya casi terminamos...", true);
        try {
            params.put("data",new FileInputStream(ImageLink), "image/jpg");
            params.put("CLiente",codCls);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(ApiRest.getURL_updaload(), params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
                if (statusCode==200){
                    Toast.makeText(context, "Correcto", Toast.LENGTH_SHORT).show();
                    pdialog.dismiss();
                }else{
                    Toast.makeText(context, "Problemas de Conexion al Servidor de Recibos", Toast.LENGTH_SHORT).show();
                    pdialog.dismiss();
                }
            }

            @Override
            public void onFailure(int statusCode,org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }



}
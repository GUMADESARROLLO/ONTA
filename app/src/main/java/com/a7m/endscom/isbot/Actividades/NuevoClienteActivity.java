package com.a7m.endscom.isbot.Actividades;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.a7m.endscom.isbot.Clases.Usuario;
import com.a7m.endscom.isbot.DB.Database;
import com.a7m.endscom.isbot.R;

public class NuevoClienteActivity extends AppCompatActivity {
    Intent iCliente;
    TextView Nombre,Direccion,CodCliente,latitud,longitud;
    LocationManager locationManager;
    Location location;
    LocationListener locationListener;
    Usuario Agente;
    AlertDialog alert = null;
    Database myDB;
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
        iCliente = getIntent();

        myDB = new Database(this);

        Nombre = (TextView) findViewById(R.id.campo_nombre);
        Direccion = (TextView) findViewById(R.id.campo_Direccion);
        CodCliente = (TextView) findViewById(R.id.campo_codCl);
        latitud = (TextView) findViewById(R.id.campo_lati);
        longitud = (TextView) findViewById(R.id.campo_long);

        CodCliente.setText(iCliente.getStringExtra("IdCliente"));
        Nombre.setText(iCliente.getStringExtra("ClsNombre"));
        Direccion.setText(iCliente.getStringExtra("CLdir"));
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
                    if (myDB.insertPosicion(Agente.getIdVendedor(),Agente.getNombre(),CodCliente.getText().toString(),Nombre.getText().toString(),latitud.getText().toString(),longitud.getText().toString())){
                        myDB.UpdateEstado(CodCliente.getText().toString(),"1");
                        finish();
                    }else{
                        Toast.makeText(NuevoClienteActivity.this, "Ocurrio un problema, no se pudo guardar la posición", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(NuevoClienteActivity.this, "El Dispositivo no sea Triangulado", Toast.LENGTH_SHORT).show();
                }

                ;

                //finish();
            }
        });

        findViewById(R.id.idbtnClla).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               MostrarLocalizacion(location);
            }
        });
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

}

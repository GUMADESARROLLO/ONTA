package com.a7m.endscom.isbot.Actividades;

import android.Manifest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.a7m.endscom.isbot.Clases.Usuario;
import com.a7m.endscom.isbot.DB.Database;
import com.a7m.endscom.isbot.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class NuevoClienteActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, ResultCallback<Status> {

    private static final String TAG = NuevoClienteActivity.class.getSimpleName();
    private static final String LOCATION_KEY = "location-key";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Location mLastLocation;
    private ActivityDetectionBroadcastReceiver mBroadcastReceiver;
    private TextView mLatitude;
    private TextView mLongitude;
    public static final int REQUEST_LOCATION = 1;
    public static final int REQUEST_CHECK_SETTINGS = 2;
    Database myDB;
    TextView Nombre,Direccion,CodCliente,latitud,longitud;
    Usuario Agente;
    Intent iCliente;
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
        mLatitude = (TextView) findViewById(R.id.campo_lati);
        mLongitude = (TextView) findViewById(R.id.campo_long);
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        checkLocationSettings();
        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();
        updateValuesFromBundle(savedInstanceState);

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
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLastLocation!=null){
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

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            stopActivityUpdates();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            startActivityUpdates();
        }
        IntentFilter intentFilter = new IntentFilter(Constants.BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(LOCATION_KEY, mLastLocation);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        processLastLocation();
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        finish();
                        break;
                }
                break;
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                startLocationUpdates();

            } else {
                Toast.makeText(this, "Permisos no otorgados", Toast.LENGTH_LONG).show();
            }
        }
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .enableAutoManage(this, this)
                .build();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest()
                .setInterval(Constants.UPDATE_INTERVAL)
                .setFastestInterval(Constants.UPDATE_FASTEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest)
                .setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();
    }

    private void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient, mLocationSettingsRequest
                );

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                Status status = result.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d(TAG, "Los ajustes de ubicación satisfacen la configuración.");
                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            Log.d(TAG, "Los ajustes de ubicación no satisfacen la configuración. " +
                                    "Se mostrará un diálogo de ayuda.");
                            status.startResolutionForResult(
                                    NuevoClienteActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.d(TAG, "El Intent del diálogo no funcionó.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.d(TAG, "Los ajustes de ubicación no son apropiados.");
                        break;

                }
            }
        });
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(LOCATION_KEY)) {
                mLastLocation = savedInstanceState.getParcelable(LOCATION_KEY);
                updateLocationUI();
            }
        }
    }

    private void updateLocationUI() {
        mLatitude.setText(String.valueOf(mLastLocation.getLatitude()));
        mLongitude.setText(String.valueOf(mLastLocation.getLongitude()));
    }


    private void stopActivityUpdates() {
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                mGoogleApiClient,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi
                .removeLocationUpdates(mGoogleApiClient, (LocationListener) this);
    }

    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, DetectedActivitiesIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void getLastLocation() {
        if (isLocationPermissionGranted()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } else {
            manageDeniedPermission();
        }
    }

    private void processLastLocation() {
        getLastLocation();
        if (mLastLocation != null) {
            updateLocationUI();
        }
    }

    private void startActivityUpdates() {
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient,
                Constants.ACTIVITY_RECOGNITION_INTERVAL,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
    }

    private void startLocationUpdates() {
        if (isLocationPermissionGranted()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } else {
            manageDeniedPermission();
        }
    }

    private void manageDeniedPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
        } else {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
    }
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == 16908332){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    private boolean isLocationPermissionGranted() {
        int permission = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        processLastLocation();
        startLocationUpdates();
        startActivityUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Conexión suspendida");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(
                this,
                "Error de conexión con el código:" + connectionResult.getErrorCode(),
                Toast.LENGTH_LONG)
                .show();

    }
    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Log.d(TAG, "Detección de actividad iniciada");
        } else {
            Log.e(TAG, "Error al iniciar/remover la detección de actividad: "
                    + status.getStatusMessage());
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, String.format("Nueva ubicación: (%s, %s)",
                location.getLatitude(), location.getLongitude()));
        mLastLocation = location;
        updateLocationUI();
    }
    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra(Constants.ACTIVITY_KEY, -1);

        }

    }
}


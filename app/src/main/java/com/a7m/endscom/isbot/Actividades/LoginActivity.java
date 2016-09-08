package com.a7m.endscom.isbot.Actividades;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.a7m.endscom.isbot.Clases.ApiRest;
import com.a7m.endscom.isbot.Clases.Mensajes;
import com.a7m.endscom.isbot.Clases.Usuario;
import com.a7m.endscom.isbot.DB.Database;
import com.a7m.endscom.isbot.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    TextView txtAgente,txtPass;
    Database myDB;
    ApiRest Dir;
    Usuario Agente;
    Mensajes ScreenAlert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //extraemos el drawable en un bitmap
        Drawable originalDrawable = getResources().getDrawable(R.drawable.avatar);
        Bitmap originalBitmap = ((BitmapDrawable) originalDrawable).getBitmap();

        //creamos el drawable redondeado
        RoundedBitmapDrawable roundedDrawable =
                RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);

        //asignamos el CornerRadius
        roundedDrawable.setCornerRadius(originalBitmap.getHeight());

        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        imageView.setImageDrawable(roundedDrawable);



        txtAgente = (TextView) findViewById(R.id.edtAgente);
        txtPass = (TextView) findViewById(R.id.edtPass);


        myDB = new Database(this);


        findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtAgente.setError(null);
                txtPass.setError(null);
                if (TextUtils.isEmpty(txtAgente.getText())){
                    txtAgente.setError("Nombre del Vendedor requerido");
                }else{
                    if (TextUtils.isEmpty(txtPass.getText())){
                        txtPass.setError("Ingrese la contraseña");
                    }else{
                        Agente.setIdVendedor(txtAgente.getText().toString().toUpperCase());
                        Agente.setPassword(txtPass.getText().toString().toUpperCase());
                        check();
                    }
                }

            }
        });

    }
    private boolean check(){


        if (myDB.ifExists(Agente.getIdVendedor(),Agente.getPassword())){
            startActivity(new Intent(this,ClientesActivity.class));

            finish();
        }else{
            ServerLogin();
        }
        return false;
    }

    private void ServerLogin() {

        AsyncHttpClient Cnx = new AsyncHttpClient();
        RequestParams paramentros = new RequestParams();
        paramentros.put("U",Agente.getIdVendedor());
        paramentros.put("P",Agente.getPassword());
        Cnx.post(Dir.getURL_login(), paramentros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
                if (statusCode==200){
                    ArrayList<String> MeEncontro = crearJson(new String(responseBody));
                    String[] items = MeEncontro.get(0).toString().split(",");
                    if (Integer.parseInt(items[2])==0){
                        Toast.makeText(LoginActivity.this, "Informacion Equivocada", Toast.LENGTH_SHORT).show();
                    }else{
                        if (myDB.SaveUsuario(items[0].toString().toUpperCase(),items[3].toString().toUpperCase(),items[1].toString().toUpperCase())){
                            check();
                        }else{
                            ScreenAlert.Alerta("No pudimos tener acceso al servidor, intente de nuevo",LoginActivity.this);
                        }
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "Sin Cobertura de datos.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(LoginActivity.this, "Sin Cobertura de datos Nivel 2.", Toast.LENGTH_SHORT).show();
            }

        });
    }
    public ArrayList<String> crearJson(String response){
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            String texto;

            for (int i=0; i<jsonArray.length(); i++){
                texto = jsonArray.getJSONObject(i).getString("Usr")+ "," +
                        jsonArray.getJSONObject(i).getString("Pss")+ "," +
                        jsonArray.getJSONObject(i).getString("Correcto")+ "," +
                        jsonArray.getJSONObject(i).getString("NOMBRE");
                listado.add(texto);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return listado;

    }

}

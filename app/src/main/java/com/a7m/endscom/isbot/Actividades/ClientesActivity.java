package com.a7m.endscom.isbot.Actividades;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import android.widget.SearchView;
import android.widget.Toast;

import com.a7m.endscom.isbot.Adaptadores.MyExpandableListAdapter;
import com.a7m.endscom.isbot.Clases.ApiRest;
import com.a7m.endscom.isbot.Clases.ChildRow;
import com.a7m.endscom.isbot.Clases.ParentRow;
import com.a7m.endscom.isbot.Clases.Usuario;
import com.a7m.endscom.isbot.DB.Database;
import com.a7m.endscom.isbot.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ClientesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,SearchView.OnCloseListener{
    private SearchManager searchManager;
    private android.widget.SearchView searchView;
    private MyExpandableListAdapter listAdapter;
    private ExpandableListView myList;
    private ArrayList<ParentRow> parentList = new ArrayList<ParentRow>();
    private ArrayList<ParentRow> showTheseParentList = new ArrayList<ParentRow>();
    private MenuItem searchItem;

    Usuario Agente;
    Database myDB;
    ProgressDialog pdialog;
    ApiRest Dir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myList = (ExpandableListView) findViewById(R.id.ExpPlan);

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(ClientesActivity.this, "OOK", Toast.LENGTH_SHORT).show();
            }
        });

        myDB = new Database(this);

        setTitle("CLIENTES [ "+ Agente.getIdVendedor() +" ]");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServerClientes();
            }
        });

        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        parentList = new ArrayList<ParentRow>();
        showTheseParentList = new ArrayList<ParentRow>();
        displayList();
        expandAll();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayList();
                        expandAll();
                    }
                });
            }
        }, 0, 5000);
    }

    private String knowPosiciones(){
        String logReg ="";

        Cursor logRes =  myDB.getClienteResgistrados();
        if (logRes.getCount()!=0){
            if (logRes.moveToFirst()) {
                do {
                    logReg += "'" + logRes.getString(0)+ "',";
                } while(logRes.moveToNext());
                logReg = logReg.substring(0,logReg.length()-1);
            }
        }
        return logReg;
    }
    private void knowPush(){
        String logReg ="";
        boolean upload = false;
        String SqlSyncInsert = "INSERT INTO clientes (IdVendedor, VENDEDOR, CLIENTE, NOMBRE, LATI, LNGI ) VALUES";
        AsyncHttpClient Cnx = new AsyncHttpClient();
        RequestParams paramentros = new RequestParams();
        pdialog = ProgressDialog.show(this, "","Procesando. Porfavor Espere...", true);

        Cursor res =  myDB.getClientePush();
        if (res.getCount()!=0){
            upload = true;
            if (res.moveToFirst()) {
                do {
                    logReg += "'" + res.getString(0)+ "',";
                } while(res.moveToNext());
                logReg = logReg.substring(0,logReg.length()-1);
            }
        }

        if (upload){
            Cursor resLOGS =  myDB.getMapeosClientes(logReg);
            if (resLOGS.getCount()!=0){
                if (resLOGS.moveToFirst()) {
                    do {
                        SqlSyncInsert +=
                                "("+
                                        "'"+resLOGS.getString(0)+"',"+
                                        "'"+resLOGS.getString(1)+"',"+
                                        "'"+resLOGS.getString(2)+"',"+
                                        "'"+resLOGS.getString(3)+"',"+
                                        "'"+resLOGS.getString(4)+"',"+
                                        "'"+resLOGS.getString(5)+"'"
                                        +"),";
                    } while(resLOGS.moveToNext());
                    SqlSyncInsert = SqlSyncInsert.substring(0,SqlSyncInsert.length()-1);
                }
            }


            paramentros.put("D",SqlSyncInsert);
            final String finalLogReg = logReg;
            Cnx.post(Dir.getURL_doom(), paramentros, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
                    if (statusCode==200){
                        myDB.Update(finalLogReg);
                        pdialog.dismiss();
                    }else{
                        Toast.makeText(ClientesActivity.this, "Problemas de Conexion al Servidor de Recibos", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                    pdialog.dismiss();
                    Toast.makeText(ClientesActivity.this, "Problemas de Cobertura de datos", Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            pdialog.dismiss();
            Toast.makeText(ClientesActivity.this, "No hay Datos que Enviar", Toast.LENGTH_SHORT).show();
        }
    }
    private void ServerClientes() {
        AsyncHttpClient Cnx = new AsyncHttpClient();
        RequestParams paramentros = new RequestParams();
        paramentros.put("C",Agente.getIdVendedor());

        paramentros.put("F",knowPosiciones());

        pdialog = ProgressDialog.show(this, "","Procesando. Porfavor Espere...", true);
        Cnx.post(Dir.getURL_mtlc(), paramentros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
                boolean Inserted=false;
                if (statusCode==200){
                    ArrayList<String> MeEncontro = crearJson(new String(responseBody));
                    SQLiteDatabase db = myDB.getWritableDatabase();
                    db.execSQL("DELETE FROM CLIENTES WHERE ESTADO ='0' ");
                    for (int n=0; n<MeEncontro.size();n++){
                        String[] items = MeEncontro.get(n).split(",");
                        Inserted = myDB.insertDataCliente(
                                items[0],
                                items[1],
                                items[2],
                                items[3],
                                items[4]
                        );
                    }
                    if (Inserted){
                        pdialog.dismiss();
                        Toast.makeText(ClientesActivity.this, "Master Actualizado", Toast.LENGTH_SHORT).show();
                    }else{
                        //adapter2.notifyDataSetChanged();
                        pdialog.dismiss();

                        Toast.makeText(ClientesActivity.this, "Error de Actualizacion de datos", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    pdialog.dismiss();
                    Toast.makeText(ClientesActivity.this, "Sin Cobertura de datos.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                pdialog.dismiss();
                Toast.makeText(ClientesActivity.this, "Sin Cobertura de datos Nivel 2.", Toast.LENGTH_SHORT).show();
            }

        });


    }
    public ArrayList<String> crearJson(String response){
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            String texto;

            for (int i=0; i<jsonArray.length(); i++){
                texto = jsonArray.getJSONObject(i).getString("CLIENTE")+ "," +
                        jsonArray.getJSONObject(i).getString("NOMBRE")+ "," +
                        jsonArray.getJSONObject(i).getString("DIRECCION")+ "," +
                        jsonArray.getJSONObject(i).getString("TELEFONO1")+ "," +
                        "0";
                listado.add(texto);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return listado;

    }
    private void CargarPLan(){
        ArrayList<ChildRow> childRows = new ArrayList<ChildRow>();
        ParentRow parentRow = null;
        Cursor res =  myDB.getData("CLIENTES");
        if (res.getCount()!=0){
            if (res.moveToFirst()) {
                do {
                    int icon = 0;
                    switch (res.getString(4)){
                        case "0":
                            icon = R.drawable.ic_add_location_black_24dp;
                        break;
                        case "1":
                            icon = R.drawable.ic_done_black_24dp;
                            break;
                        case "2":
                            icon = R.drawable.ic_done_all_black_24dp;
                            break;
                    }
                    childRows.add(new ChildRow(icon,res.getString(0),res.getString(1),res.getString(2),res.getString(4)));
                } while(res.moveToNext());
            }
        }

        parentRow = new ParentRow("Base de Clientes", childRows);
        parentList.add(parentRow);


    }
    private void displayList(){
        parentList.clear();
        CargarPLan();
        listAdapter = new MyExpandableListAdapter(ClientesActivity.this,parentList);
        myList.setAdapter(listAdapter);
    }

    private void expandAll(){
        int count = listAdapter.getGroupCount();
        for (int i=0;i<count;i++){
            myList.expandGroup(i);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_plan,menu);
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        searchView.requestFocus();
        return true;
    }

    @Override
    public boolean onClose() {
        listAdapter.filterData("");
        expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        listAdapter.filterData(query);
        expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        listAdapter.filterData(newText);
        expandAll();
        return false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        displayList();
        expandAll();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_Salir:
                finish();
                break;
            case R.id.action_upload:
                knowPush();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}

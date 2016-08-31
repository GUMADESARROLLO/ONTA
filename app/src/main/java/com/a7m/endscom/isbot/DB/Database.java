package com.a7m.endscom.isbot.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.ParcelUuid;
import android.util.Log;

import com.a7m.endscom.isbot.Clases.Usuario;

/**
 * Created by marangelo.php on 26/08/2016.
 */
public class Database extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ontaCLUMK.db";
    Usuario Agente;
    public Database(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME,null,1);
        SQLiteDatabase db = this.getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table Usuarios" +
                " (" +
                "       IdVendedor        TEXT(10)," +
                "       NombreUsuario     TEXT(100)," +
                "       Password          TEXT(20)" +
                ")");


        db.execSQL("create table CLIENTES ("+
                "CLIENTE        TEXT,"+
                "NOMBRE         TEXT,"+
                "DIRECCION      TEXT,"+
                "TELEFONO1      TEXT,"+
                "ESTADO         TEXT )" );

        db.execSQL("create table PUSH ("+
                "IdVendedor        TEXT,"+
                "VENDEDOR        TEXT,"+
                "CLIENTE        TEXT,"+
                "NOMBRE         TEXT,"+
                "LATI      TEXT,"+
                "LNGI      TEXT )" );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS Usuarios");
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS CLIENTES");
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS PUSH");
        onCreate(db);
    }
    public boolean ifExists(String U, String P){
        SQLiteDatabase db = this.getWritableDatabase();
        Boolean ok = false;
        String Query = "SELECT * FROM Usuarios WHERE IdVendedor="+ '"' + U.trim() + '"' +" and Password="+ '"' +P.trim() + '"' +"";
        Cursor res = db.rawQuery(Query ,null);
        if (res.getCount()!=0){
            if (res.moveToFirst()) {
                do {
                    Agente.setNombre(res.getString(1));
                } while(res.moveToNext());

            }
            ok = true;
        }
        return ok;
    }
    public boolean SaveUsuario(String IdVendedor,String Nombre, String Password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("IdVendedor",IdVendedor);
        contentValues.put("NombreUsuario",Nombre);
        contentValues.put("Password",Password);
        long result = db.insert("Usuarios",null,contentValues);
        if (result == -1){
            return false;
        }else{
            return true;
        }
    }
    public boolean insertDataCliente(String CLIENTE, String NOMBRE,String DIRECCION,String TELEFONO1, String sta){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("CLIENTE",CLIENTE);
        contentValues.put("NOMBRE",NOMBRE);
        contentValues.put("DIRECCION",DIRECCION);
        contentValues.put("TELEFONO1",TELEFONO1);
        contentValues.put("ESTADO",sta);
        long result = db.insert("CLIENTES",null,contentValues);
        if (result == -1){
            return false;
        }else{
            return true;
        }
    }
    public boolean insertPosicion(String idVendedor, String nombreVendedor,String codCliente,String NombreCliente,String longi,String lati,String Estado){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        final long result;
        if (Estado!="0"){
            contentValues.put("ESTADO", "1");
            result  = db.update("CLIENTES", contentValues, "CLIENTE" + "= '" + codCliente + "'", null);
        }else{
            contentValues.put("IdVendedor",idVendedor);
            contentValues.put("VENDEDOR",nombreVendedor);
            contentValues.put("CLIENTE",codCliente);
            contentValues.put("NOMBRE",NombreCliente);
            contentValues.put("LATI",longi);
            contentValues.put("LNGI",lati);
            result = db.insert("PUSH",null,contentValues);
        }
        if (result == -1){
            return false;
        }else{
            return true;
        }
    }
    public boolean UpdateEstado(String Id,String std){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ESTADO", std);
        long result  = db.update("CLIENTES", values, "CLIENTE" + "= '" + Id + "'", null);
        db.close();

        if (result == -1){
            return false;
        }else{
            return true;
        }
    }
    public Cursor getData(String Table){
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM " + Table + " ORDER BY ESTADO ASC";
        Cursor res = db.rawQuery(Query ,null);
        return res;
    }
    public Cursor getPosicion(String Clientes){
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM PUSH WHERE CLIENTE in("+Clientes+")";
        Cursor res = db.rawQuery(Query ,null);
        return res;
    }
    public Cursor getClienteResgistrados(){
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM CLIENTES WHERE ESTADO in('1','2')";
        Cursor res = db.rawQuery(Query ,null);
        return res;
    }
    public Cursor getClientePush(){
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM CLIENTES WHERE ESTADO in('1')";
        Cursor res = db.rawQuery(Query ,null);
        return res;
    }
    public Cursor getMapeosClientes(String Clientes){
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM PUSH WHERE CLIENTE in("+Clientes+")";
        Cursor res = db.rawQuery(Query ,null);
        return res;
    }
    public void Update(String Id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE CLIENTES SET ESTADO ='2' WHERE CLIENTE in("+Id+")");
        db.close();
    }


}

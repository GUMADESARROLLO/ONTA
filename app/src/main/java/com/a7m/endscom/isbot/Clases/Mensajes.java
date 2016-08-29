package com.a7m.endscom.isbot.Clases;

import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * Created by marangelo.php on 26/08/2016.
 */
public class Mensajes {
    public void Alerta(String Error, Context ctx){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(Error)
                .setNegativeButton("OK",null)
                .create()
                .show();
    }
}

package com.a7m.endscom.isbot.Clases;

/**
 * Created by marangelo.php on 26/08/2016.
 */
public class Usuario {
    private static String IdVendedor;
    private static String Nombre;
    private static String Password;

    public static String getIdVendedor() {
        return IdVendedor;
    }

    public static void setIdVendedor(String idVendedor) {
        IdVendedor = idVendedor;
    }

    public static String getNombre() {
        return Nombre;
    }

    public static void setNombre(String nombre) {
        Nombre = nombre;
    }

    public static String getPassword() {
        return Password;
    }

    public static void setPassword(String password) {
        Password = password;
    }
}

package com.a7m.endscom.isbot.Clases;

/**
 * Created by A7M on 20/08/2016.
 */
public class ChildRow {
    private String text,nombre,direc,Estado;
    private int icon;

    public ChildRow(int icon, String text,String nombre, String Dirs,String estado) {
        this.icon = icon;
        this.text = text;
        this.nombre = nombre;
        this.direc = Dirs;
        this.Estado = estado;
    }
    public int getIcon() {
        return icon;
    }
    public void setIcon(int icon) {
        this.icon = icon;
    }
    public String getDirec() {
        return direc;
    }
    public void setDirec(String direc) {
        this.direc = direc;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }
}

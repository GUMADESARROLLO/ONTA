package com.a7m.endscom.isbot.Clases;

/**
 * Created by marangelo.php on 26/08/2016.
 */
public class ApiRest {
    //private static String SERVER = "192.168.1.78:8448";
    private static String SERVER = "165.98.75.219:8448";

    private static String URL_login = "http://"+ SERVER +"/ONTA/Login.php";
    private static String URL_mtlc = "http://"+ SERVER +"/ONTA/MTCL2.php";
    private static String URL_doom = "http://"+ SERVER +"/ONTA/Doom.php";
    private static String URL_updaload = "http://"+ SERVER +"/ONTA/upload.php";

    public ApiRest() {
    }

    public static String getURL_login() {
        return URL_login;
    }
    public static String getURL_mtlc() {
        return URL_mtlc;
    }
    public static String getURL_doom() {
        return URL_doom;
    }

    public static String getURL_updaload() {
        return URL_updaload;
    }
}

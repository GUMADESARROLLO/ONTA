<?php


class Vistas{
    
   
	public static function open_database_connectionSQL(){
	    $serverName = "192.168.1.112";
	    $connectionInfo = array( "UID"=>"sa","PWD"=>"Server2012!","Database"=>"PRODUCCION","CharacterSet"=>"UTF-8");
	    $LINK = sqlsrv_connect( $serverName, $connectionInfo);
	    return $LINK;
	}
    public static function open_database_connectionMYSQL(){
        $link = mysql_connect('localhost', 'root', '');
        mysql_select_db('onta', $link); 
        return $link;
    }
	
     public static function JsonDetalle($ID){

         $obj = new Vistas;
        $conn = $obj -> open_database_connectionSQL();  
        $tsql = "SELECT MIN_ART_FACT,MAX_ART_FACT, UNIDADES_BONIF FROM dbo.ESCALA_BONIF where ARTICULO='$ID' and  NIVEL_PRECIO='FARMACIA' order by MIN_ART_FACT";        
        
        $json = array();        
        $params = array();
        $options =  array( "Scrollable" => SQLSRV_CURSOR_KEYSET );
        $stmt = sqlsrv_query( $conn, $tsql , $params, $options );          
        if(sqlsrv_num_rows($stmt)){            
            $i=0;
            while($row=sqlsrv_fetch_array($stmt,SQLSRV_FETCH_ASSOC)){                    
                $json['dataDetalle'][$i]['MIN_ART_FACT'] = number_format($row['MIN_ART_FACT'],0);  
                $json['dataDetalle'][$i]['MAX_ART_FACT'] = number_format($row['MAX_ART_FACT'],0);  
                $json['dataDetalle'][$i]['UNIDADES_BONIF'] = number_format($row['UNIDADES_BONIF'],0);  
                $i++;            
        
            }
        }else{
            $json['dataDetalle'][0]['MIN_ART_FACT'] = "";  
            $json['dataDetalle'][0]['MAX_ART_FACT'] = "";  
            $json['dataDetalle'][0]['UNIDADES_BONIF'] = "";  
            

        }
        echo json_encode($json);      
    }
    public static function Inventarios(){
        $obj = new Vistas;
        $conn = $obj -> open_database_connectionSQL();    
      
        $tsql = "SELECT  * FROM dbo.SALESUMK_MTAR ORDER BY DESCRIPCION";    
        $json = array();        
        $params = array();
        $options =  array( "Scrollable" => SQLSRV_CURSOR_KEYSET );
        $stmt = sqlsrv_query( $conn, $tsql , $params, $options );  
        $vowels = array(",");
        if(sqlsrv_num_rows($stmt)){
            $i=0;
            while($row=sqlsrv_fetch_array($stmt,SQLSRV_FETCH_ASSOC)){                                                                    
                $json[$i]['ARTICULO']         = $row['ARTICULO'];  
                $json[$i]['DESCRIPCION']      = str_replace($vowels, "",$row['DESCRIPCION']);                            
                $json[$i]['TOTAL_EXISTENCIA'] = number_format($row['TOTAL_EXISTENCIA'],2, '.', '')." [".$row['UNIDAD_ALMACEN']."]";  
                $json[$i]['PRECIO']           = 'C$ '.number_format($row['PRECIO'],4, '.', '');  
                $json[$i]['PUNTOS']           = ($row['PUNTOS']==null) ? "0" : $row['PUNTOS'] ;
                $json[$i]['BODEGAS']          = str_replace(",", "xxx", $row['BODEGAS']);
                $json[$i]['REGLAS']          =  (str_replace(",", "", $row['REGLAS'])=="") ? "--" : str_replace(",", "xxx", $row['REGLAS']) ;
                $i++;            
            }
            
        }
        echo json_encode($json);

    }
    public static function ExistenciaLotes(){
        $obj = new Vistas;
        $conn = $obj -> open_database_connectionSQL();    
      
        $tsql = "SELECT * FROM dbo.SALESUMK_EXISTENCIA_LOTE ";    
        $json = array();        
        $params = array();
        $options =  array( "Scrollable" => SQLSRV_CURSOR_KEYSET );
        $stmt = sqlsrv_query( $conn, $tsql , $params, $options );  
        $vowels = array(",");
        if(sqlsrv_num_rows($stmt)){
            $i=0;
            while($row=sqlsrv_fetch_array($stmt,SQLSRV_FETCH_ASSOC)){                                                                    
                $json[$i]['ARTICULO']               = $row['ARTICULO'];                  
                $json[$i]['LOTE']               = $row['LOTE'];                  
                $json[$i]['BODEGAS']            = $row['FECHA_VENCIMIENTO'];
                $json[$i]['CANT_DISPONIBLE']    = number_format($row['CANT_DISPONIBLE'],2, '.', '');                  
                $json[$i]['BODEGA']          = $row['BODEGA'];                
                $i++;            
            }
            
        }
        echo json_encode($json);

    }
   

     public static function Catalogo(){
        $obj = new Vistas;
        $conn = $obj -> open_database_connectionSQL();  
        $tsql = "SELECT ARTICULO,DESCRIPCION,LABORATORIOS FROM dbo.M_ARTIC_2015 ";        
        $json = array();        
        $params = array();
        $options =  array( "Scrollable" => SQLSRV_CURSOR_KEYSET );
        $stmt = sqlsrv_query( $conn, $tsql , $params, $options );          
        if(sqlsrv_num_rows($stmt)){
            $i=0;
            while($row=sqlsrv_fetch_array($stmt,SQLSRV_FETCH_ASSOC)){                                                           
                $json['data'][$i]['ARTICULO'] = $row['ARTICULO'];  
                $json['data'][$i]['DESCRIPCION'] = $row['DESCRIPCION'];  
                $json['data'][$i]['LABORATORIOS'] = $row['LABORATORIOS'];                  
                $i++;            
            }
            
        }
        echo json_encode($json);
    }
    public static function Liq6(){

        $obj = new Vistas;
        $conn = $obj -> open_database_connectionSQL();  
        $tsql = "SELECT  top 5 ARTICULO,DESCRIPCION,DIAS_VENCIMIENTO,CANT_DISPONIBLE,UNIDAD_VENTA,fecha_vencimientoR,LOTE FROM dbo.Vencimientos_6meses ";        
        $json = array();        
        $params = array();
        $options =  array( "Scrollable" => SQLSRV_CURSOR_KEYSET );
        $vowels = array(",");
        $stmt = sqlsrv_query( $conn, $tsql , $params, $options );          
        if(sqlsrv_num_rows($stmt)){
            $i=0;
            while($row=sqlsrv_fetch_array($stmt,SQLSRV_FETCH_ASSOC)){                                                           
                $json[$i]['ARTICULO'] = $row['ARTICULO'];  
                $json[$i]['DESCRIPCION'] =str_replace($vowels, "",$row['DESCRIPCION']); 
                $json[$i]['DIAS_VENCIMIENTO'] = $row['DIAS_VENCIMIENTO'];                  
                $json[$i]['CANT_DISPONIBLE'] = number_format($row['CANT_DISPONIBLE'],2, '.', '').' - [ '. $row['UNIDAD_VENTA'].' ] ';   
                $json[$i]['fecha_vencimientoR'] = $row['fecha_vencimientoR'];                  
                $json[$i]['LOTE'] = $row['LOTE'];
                $i++;            
            }
            
        }
        echo json_encode($json);
    }
     public static function Liq12(){

        $obj = new Vistas;
        $conn = $obj -> open_database_connectionSQL();  
        $tsql = "SELECT ARTICULO,DESCRIPCION,DIAS_VENCIMIENTO,CANT_DISPONIBLE,UNIDAD_VENTA,fecha_vencimientoR,LOTE FROM dbo.Vencimientos_12meses ";        
        $json = array();        
        $params = array();
        $options =  array( "Scrollable" => SQLSRV_CURSOR_KEYSET );
        $vowels = array(",");
        $stmt = sqlsrv_query( $conn, $tsql , $params, $options );          
        if(sqlsrv_num_rows($stmt)){
            $i=0;
            while($row=sqlsrv_fetch_array($stmt,SQLSRV_FETCH_ASSOC)){                                                           
                $json[$i]['ARTICULO'] = $row['ARTICULO'];  
                $json[$i]['DESCRIPCION'] =str_replace($vowels, "",$row['DESCRIPCION']); 
                $json[$i]['DIAS_VENCIMIENTO'] = $row['DIAS_VENCIMIENTO'];                  
                $json[$i]['CANT_DISPONIBLE'] = number_format($row['CANT_DISPONIBLE'],2, '.', '').' - [ '. $row['UNIDAD_VENTA'].' ] ';   
                $json[$i]['fecha_vencimientoR'] = $row['fecha_vencimientoR'];                  
                $json[$i]['LOTE'] = $row['LOTE'];
                $i++;            
            }
            
        }
        echo json_encode($json);
    }
    public static function MTCL($ID){
        $obj = new Vistas;
        $conn = $obj -> open_database_connectionSQL();  
        $tsql = "SELECT * FROM dbo.APKSaldo_clientes_umk where VENDEDOR='".$ID."' ORDER BY NOMBRE";        
        
        $json = array();        
        $params = array();
        $options =  array( "Scrollable" => SQLSRV_CURSOR_KEYSET );
        $stmt = sqlsrv_query( $conn, $tsql , $params, $options );  
        $vowels = array(",");        
        if(sqlsrv_num_rows($stmt)){
            $i=0;
            while($row=sqlsrv_fetch_array($stmt,SQLSRV_FETCH_ASSOC)){                    
                $json[$i]['CLIENTE'] = $row['CLIENTE'];                  
                $json[$i]['NOMBRE'] = $row['NOMBRE'];  
                $json[$i]['DIRECCION'] = str_replace($vowels, "",$row['DIRECCION']);
                $json[$i]['TELEFONO1'] = $row['TELEFONO1'];  
                $json[$i]['MOROSO'] = $row['MOROSO'];  
                $json[$i]['LIMITE_CREDITO'] = "C$ ".number_format($row['LIMITE_CREDITO'],2,".","");                
                $json[$i]['SALDO'] = "C$ ".number_format($row['SALDO'],2,".","");                
                $json[$i]['DISPO'] = "C$ ".number_format($row['Disp_credito'],2,".","");  

                $json[$i]['NoVencidos'] = "0.00";
                $json[$i]['Dias30'] = "0.00";
                $json[$i]['Dias60'] = "0.00";
                $json[$i]['Dias90'] = "0.00";
                $json[$i]['Dias120'] = "0.00";
                $json[$i]['Mas120'] = "0.00";

                    $SqlMora = "exec Softland.dbo.DOC_VENC_CL '".$row['CLIENTE']."'";
                    $stmtMora = sqlsrv_query( $conn, $SqlMora , $params, $options );  
                    while($rowMora=sqlsrv_fetch_array($stmtMora,SQLSRV_FETCH_ASSOC)){
                        $json[$i]['NoVencidos'] = "C$ ".number_format($rowMora['NoVencidos'],2,".","");  
                        $json[$i]['Dias30']     = "C$ ".number_format($rowMora['Dias30'],2,".","");  
                        $json[$i]['Dias60']     = "C$ ".number_format($rowMora['Dias60'],2,".","");  
                        $json[$i]['Dias90']     = "C$ ".number_format($rowMora['Dias90'],2,".","");  
                        $json[$i]['Dias120']    = "C$ ".number_format($rowMora['Dias120'],2,".","");  
                        $json[$i]['Mas120']     = "C$ ".number_format($rowMora['Mas120'],2,".","");  
                    }

                $i++;            
            }
            
        }
        echo json_encode($json);
    }
    public static function MTCL2($ID,$F){
        $obj = new Vistas;
        $conn = $obj -> open_database_connectionSQL();  
        if ($F=="") {
            $tsql = "SELECT * FROM dbo.APKSaldo_clientes_umk where VENDEDOR='".$ID."' ORDER BY NOMBRE";        
        }else{
            $tsql = "SELECT * FROM dbo.APKSaldo_clientes_umk where VENDEDOR='".$ID."' and CLIENTE not in (".$F.")  ORDER BY NOMBRE";        
        }
        
        //echo $tsql;
        $json = array();        
        $params = array();
        $options =  array( "Scrollable" => SQLSRV_CURSOR_KEYSET );
        $stmt = sqlsrv_query( $conn, $tsql , $params, $options );  
        $vowels = array(",");        
        if(sqlsrv_num_rows($stmt)){
            $i=0;
            while($row=sqlsrv_fetch_array($stmt,SQLSRV_FETCH_ASSOC)){                    
                $json[$i]['CLIENTE'] = $row['CLIENTE'];                  
                $json[$i]['NOMBRE'] = $row['NOMBRE'];  
                $json[$i]['DIRECCION'] = str_replace($vowels, "",$row['DIRECCION']);
                $json[$i]['TELEFONO1'] = $row['TELEFONO1'];  
                $json[$i]['MOROSO'] = $row['MOROSO'];  
                $json[$i]['LIMITE_CREDITO'] = "C$ ".number_format($row['LIMITE_CREDITO'],2,".","");                
                $json[$i]['SALDO'] = "C$ ".number_format($row['SALDO'],2,".","");                
                $json[$i]['DISPO'] = "C$ ".number_format($row['Disp_credito'],2,".","");  

                $json[$i]['NoVencidos'] = "0.00";
                $json[$i]['Dias30'] = "0.00";
                $json[$i]['Dias60'] = "0.00";
                $json[$i]['Dias90'] = "0.00";
                $json[$i]['Dias120'] = "0.00";
                $json[$i]['Mas120'] = "0.00";

                    $SqlMora = "exec Softland.dbo.DOC_VENC_CL '".$row['CLIENTE']."'";
                    $stmtMora = sqlsrv_query( $conn, $SqlMora , $params, $options );  
                    while($rowMora=sqlsrv_fetch_array($stmtMora,SQLSRV_FETCH_ASSOC)){
                        $json[$i]['NoVencidos'] = "C$ ".number_format($rowMora['NoVencidos'],2,".","");  
                        $json[$i]['Dias30']     = "C$ ".number_format($rowMora['Dias30'],2,".","");  
                        $json[$i]['Dias60']     = "C$ ".number_format($rowMora['Dias60'],2,".","");  
                        $json[$i]['Dias90']     = "C$ ".number_format($rowMora['Dias90'],2,".","");  
                        $json[$i]['Dias120']    = "C$ ".number_format($rowMora['Dias120'],2,".","");  
                        $json[$i]['Mas120']     = "C$ ".number_format($rowMora['Mas120'],2,".","");  
                    }

                $i++;            
            }
            
        }
        echo json_encode($json);
    }
    public static function FACTURAS($ID){
        $obj = new Vistas;
        $conn = $obj -> open_database_connectionSQL();  
        $tsql = "SELECT * FROM SALESUMK_FACTURAS where VENDEDOR='".$ID."' ";        
        
        $json = array();        
        $params = array();
        $options =  array( "Scrollable" => SQLSRV_CURSOR_KEYSET );
        $stmt = sqlsrv_query( $conn, $tsql , $params, $options );  
        $vowels = array(",");        
        if(sqlsrv_num_rows($stmt)){
            $i=0;
            while($row=sqlsrv_fetch_array($stmt,SQLSRV_FETCH_ASSOC)){                    
                $json[$i]['FACTURA'] = $row['FACTURA'];                  
                $json[$i]['CLIENTE'] = $row['CLIENTE'];                  
                $json[$i]['VENDEDOR'] = $row['VENDEDOR'];                  
                $json[$i]['MONTO'] = $row['MONTO'];  
                $json[$i]['SALDO'] = $row['SALDO'];  
                $json[$i]['FECHA_VENCE'] = $row['FECHA_VENCE'];  
                $i++;            
            }
            
        }
        echo json_encode($json);
    }
    public static function MTCL_MORA($ID){
        $obj = new Vistas;
        $conn = $obj -> open_database_connectionSQL();  
        $tsql = "exec Softland.dbo.DOC_VENC_CL '".$ID."'";
        
        $json = array();        
        $params = array();
        $options =  array( "Scrollable" => SQLSRV_CURSOR_KEYSET );
        $stmt = sqlsrv_query( $conn, $tsql , $params, $options );  
        $vowels = array(",");        
        if(sqlsrv_num_rows($stmt)){
            $i=0;
            while($row=sqlsrv_fetch_array($stmt,SQLSRV_FETCH_ASSOC)){                    
                $json[$i]['CLIENTE'] = $row['CLIENTE'];                  
                $json[$i]['NOMBRE'] = $row['NOMBRE'];  
                $json[$i]['DIRECCION'] = str_replace($vowels, "",$row['DIRECCION']);
                $json[$i]['TELEFONO1'] = $row['TELEFONO1'];  
                $json[$i]['MOROSO'] = $row['MOROSO'];  
                $json[$i]['LIMITE_CREDITO'] = "C$ ".number_format($row['LIMITE_CREDITO'],2,".","");                
                $json[$i]['SALDO'] = "C$ ".number_format($row['SALDO'],2,".","");                
                $json[$i]['DISPO'] = "C$ ".number_format($row['Disp_credito'],2,".","");  
                $i++;            
            }
            
        }
        echo json_encode($json);
    }
      public static function InsertRecibo($SQL){
        $obj = new Vistas;
        $link = $obj ->open_database_connectionMYSQL();        
        $porciones = explode(";", $SQL);
        for ($i=0; $i <count($porciones) ; $i++) {             
            mysql_query($porciones[$i],$link) or die (mysql_error());
        }
    }
    public static function Login($User,$Pass){
        $obj = new Vistas;
        $link = $obj ->open_database_connectionMYSQL();
        $consulta= "SELECT * FROM salesumk.usuario WHERE CodVendedor='".$User."' and Password='".$Pass."'"; 
        $resultado= mysql_query($consulta,$link) or die (mysql_error());
        $fila=mysql_fetch_array($resultado);
        $json = array();
        if (!$fila[0]) {            
            $json[0]['Key'] = "";
            $json[0]['Usr'] = "";
            $json[0]['Pss'] = "";
            $json[0]['NOMBRE'] = "";
            $json[0]['Correcto'] ="0";

        } else {
            $json[0]['Key'] = $fila['UsuarioID'];
            $json[0]['Usr'] = $fila['CodVendedor'];
            $json[0]['Pss'] = $fila['Password'];
            $json[0]['NOMBRE'] = $fila['NombreUsuario'];
            $json[0]['Correcto'] ="1";  
            
            
        }
        echo json_encode($json);
    }

    
   
    
}
?>
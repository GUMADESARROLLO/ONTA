<?php header('Access-Control-Allow-Origin: *');  
	require_once 'Class.Main.php';
	$obj = new Vistas;
	$obj ->MTCL2($_REQUEST['C'],$_REQUEST['F']);
	
?>
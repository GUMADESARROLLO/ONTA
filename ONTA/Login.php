<?php header('Access-Control-Allow-Origin: *');  
	require_once 'Class.Main.php';
	$obj = new Vistas;
	$obj ->Login($_REQUEST['U'],$_REQUEST['P']);
	
?>
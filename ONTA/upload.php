<?php
	$file_path1 = "baseimagenes/";
	$file_path = $file_path1 . $_REQUEST['CLiente'].".".basename($_FILES['data']['name']);            
    move_uploaded_file($_FILES['data']['tmp_name'], $file_path)

?>
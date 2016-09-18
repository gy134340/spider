<?php
function connect(){
	$host = 'localhost';
	$username = 'root';
	$pwd = '';

	$con = @mysql_connect($host,$username,$pwd);
	mysql_select_db("crawler",$con);
	mysql_query("set names 'utf8'");
	return $con;
}

?>

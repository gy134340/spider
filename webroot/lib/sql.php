<?php
include 'database.php';
$con = connect();
//select('img','home_clients','id','1');
//insert('contact',array('id'=>'2','content'=>'whatever','img'=>'whatever'));
//update('contact',array('content'=>'whatever','img'=>'whatever'),'id=2');
//SQL("select * from contact");



//SQL: Select 
//Select $attr from $table where $where
//Select $attr from $table where 1
function select($attr,$table,$where=null){
	if($where!=null && $attr!=null){
		$query =  "select ".$attr." from ".$table." WHERE ".$where;
	}
	else{
		$query =  "select ".$attr." from ".$table." WHERE 1";
	}
	$exc = mysql_query($query);
	$r = array();
	while($result = mysql_fetch_row($exc)){
		array_push($r,$result);
	}
	//echo json_encode($r);
	return $r;
}

//get data length
function getLength($table){
	$query  = "select count(*) from ".$table."";
	$res = mysql_query($query);
	$r = mysql_fetch_array($res);
	return $r[0];
}
function selectpage($table,$offset,$pagesize){
	$query = "select * from ".$table." order by id limit ".$offset.",".$pagesize."";
	$exc = mysql_query($query);
	$r = array();
	while($result = mysql_fetch_row($exc)){
		array_push($r,$result);
	}
	//echo json_encode($r);
	return $r;
}
//SQL: insert
//insert into $table ($array_keys) value ($array_values)
function insert($table,$array){
	$keys=join(",",array_keys($array));
	$vals="'".join("','",array_values($array))."'";
	$query="insert {$table}($keys) values({$vals})";
	$exc = mysql_query($query);
	//echo $query;
}

//SQL: update
//update $table set($array_keys)=($array_values) where $where
function update($table,$array,$where=null){
	$str=null;
	foreach($array as $key=>$val){
		if($str==null){
			$sep="";
		}else{
			$sep=",";
		}
		$str.=$sep.$key."='".$val."'";
	}
		$query="update {$table} set {$str} ".($where==null?null:" where ".$where);
		//echo $query;
		$result=mysql_query($query);
		if($result){
			//echo mysql_affected_rows();
			return mysql_affected_rows();
		}else{
			return false;
		}
}

//SQL:where
//delete from $table where $where
function delete($table,$where=null){
	$where=$where==null?null:" where ".$where;
	$query="delete from {$table} {$where}";
	mysql_query($query);
	echo mysql_affected_rows();
	return mysql_affected_rows();
}

//Pass in customized Query in $query
function SQL($query){
	$exc = mysql_query($query);
	$r = array();
	if(split(" ",$query)[0]=='select'){
		while($result = mysql_fetch_row($exc)){
			array_push($r,$result);
		}
		echo json_encode($r) ;
		return $r;
	}else{
		echo mysql_affected_rows();
		return mysql_affected_rows();
	}
}
?>

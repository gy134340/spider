<?php 
header('Content-Type:text/html;charset=utf-8'); 
include('lib/sql.php');
// $res = select('*','bean','1');
$total = getLength('bean');
$pagesize = 25;

$pages = intval($total/$pagesize);
if($total/$pagesize){
	$pages++;
}

if(isset($_GET['page']) && $_GET['page'] >1){
	$page = intval($_GET['page']);
}else{
	$page = 1;
}
$offset = $pagesize * ($page-1);
$res = selectpage('bean',$offset,$pagesize);

?>
<!DOCTYPE html>
<html>
<head>
	<title>豆瓣爬虫</title>
</head>
<meta charset="utf-8">
<meta  name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" type="text/css" href="css/reset.css">
<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">
<body>
<div class="container row">
<h1><?php echo $total ?></h1>
	<div class="col-md-6 col-md-offset-3">	
<?php 
foreach ($res as $key => $value) {
	$id = json_encode($value[0]);
	$name = json_encode($value[1],JSON_UNESCAPED_UNICODE);
	$url = $value[2];
	$date = json_encode($value[3],JSON_UNESCAPED_UNICODE);
?>

<a href='<?php echo $url ?>' class="" list-group-item text-error "><?php echo $name ?></a> <h5  class="text-info text-right"><?php echo $date ?></h5>

<?php	
} 
?>
<ul class='pagination'>
	<li><a href='#'>&laquo;</a></li>

	<?php for ($i=0;$i<$pages;$i++){?>
		<li>
			<a href='index.php?page=<?php echo $i; ?>'> 
			<?php echo $i ;?></a>
		</li>
	<?php } ?>

	<li><a href='#'>&raquo;</a></li>
</ul>
</div>
</div>
</body>
<script src="js/jquery-1.11.1.min.js"></script>
<script src="js/bootstrap.min.js"></script>
</html>
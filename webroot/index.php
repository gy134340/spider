<?php 
header('Content-Type:text/html;charset=utf-8'); 
include('lib/sql.php');
$res = select('*','bean','1');
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
<?php 
foreach ($res as $key => $value) {
	$id = json_encode($value[0]);
	$name = json_encode($value[1],JSON_UNESCAPED_UNICODE);
	$url = $value[2];
	$date = json_encode($value[3],JSON_UNESCAPED_UNICODE);
?>

<a href='<?php echo $url ?>' class="list-group-item text-error "><?php echo $name ?> <h5  class="text-info text-right"><?php echo $date ?></h5></a>

<?php	
} ?>
</body>
<script src="js/jquery-1.11.1.min.js"></script>
<script src="js/bootstrap.min.js"></script>
</html>
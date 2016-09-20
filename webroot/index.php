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
<style type="text/css">
	.container{
		padding-top: 100px;
		padding-bottom: 50px;
	}
</style>
<body>
<div class="container row">
<!-- <h1><?php echo $total ?></h1> -->
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
	

	<?php for ($i=0;$i<$pages;$i++){?>
		<li>
			<a href='index.php?page=<?php echo $i; ?>'> 
			<?php echo $i ;?></a>
		</li>
	<?php } ?>

</ul>
</div>
</div>
</body>
<script src="js/jquery-1.11.1.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script type="text/javascript">
	(function($){
    var ms = {
        init:function(totalsubpageTmep,args){
            return (function(){
                ms.fillHtml(totalsubpageTmep,args);
                ms.bindEvent(totalsubpageTmep,args);
            })();
        },
        //填充html
        fillHtml:function(totalsubpageTmep,args){
            return (function(){
                totalsubpageTmep="";
                // 页码大于等于4的时候，添加第一个页码元素
                if(args.currPage!=1 && args.currPage>=4 && args.totalPage!=4) {
                    totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' class='geraltTb_pager' data-go='' >"+1+"</a></li>";
                }
                /* 当前页码>4, 并且<=总页码，总页码>5，添加“···”*/
                if(args.currPage-2>2 && args.currPage<=args.totalPage && args.totalPage>5) {
                    totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' class='geraltTb_' data-go='' >...</a></li>";
                }
                /* 当前页码的前两页 */
                var start = args.currPage-2;
                /* 当前页码的后两页 */
                var end = args.currPage+2;

                if((start>1 && args.currPage<4) || args.currPage==1) {
                    end++;
                }
                if(args.currPage>args.totalPage-4 && args.currPage>=args.totalPage) {
                    start--;
                }
                for(; start<=end; start++) {
                    if(start<=args.totalPage && start>=1) {
                        totalsubpageTmep += "<li class='ali'><a href='index.php?page="+start+"' class='geraltTb_pager' data-go='' >"+start+"</a></li>";
                    }
                }
                if(args.currPage+2<args.totalPage-1 && args.currPage>=1 && args.totalPage>5) {
                    totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' class='geraltTb_' data-go='' >...</a></li>";
                }

                if(args.currPage!=args.totalPage && args.currPage<args.totalPage-2 && args.totalPage!=4) {
                    totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' class='geraltTb_pager' data-go='' >"+args.totalPage+"</a></li>";
                }
                $(".pagination").html(totalsubpageTmep);
            })();
        },
        //绑定事件
        bindEvent:function(totalsubpageTmep,args){
            return (function(){
                totalsubpageTmep.on("click","a.geraltTb_pager",function(event){
                    var current = parseInt($(this).text());
                    ms.fillHtml(totalsubpageTmep,{"currPage":current,"totalPage":args.totalPage,"turndown":args.turndown});
                    if(typeof(args.backFn)=="function"){
                        args.backFn(current);
                    }
                });
            })();
        }
    }
    $.fn.createPage = function(options){       
        ms.init(this,options);
    }
})(jQuery);
</script>

	
<script type="text/javascript">
    $(function(){
        $(".pagination").createPage({
            totalPage:<?php echo $pages ?>,
            currPage:<?php echo $page ?>,
            backFn:function(p){
                console.log("回调函数："+p);
            }
        });
    })
</script>

</html>
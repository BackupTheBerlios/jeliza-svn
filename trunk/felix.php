<?
header("Expires: Mon, 26 Jul 1997 05:00:00 GMT");    // Datum aus Vergangenheit
header("Last-Modified: " . gmdate("D, d M Y H:i:s") . " GMT"); 
                                                     // immer geändert
header("Cache-Control: no-store, no-cache, must-revalidate");  // HTTP/1.1
header("Cache-Control: post-check=0, pre-check=0", false);
header("Pragma: no-cache");  

error_reporting(E_ALL | E_NOTICE);

$name = "jeliza.php";

define("TRUE", "0");
define("FALSE", "1");


?>
<html>
<head>
<title>Felix</title>
</head>
<body>

<h2>Felix</h2>

<?

require "jeliza-ultra.php";

if (@strlen($_GET["fra"]) > 0) {
?>

Sie sagten: <?=noSonderZeichen($_GET["fra"]);?>

<div style="height: 360px; width: 300px; background-image: url(tft.gif);">
<div style="position: relative; top: 50px; left: 30px; height: 180px; width: 160px;
	scroll: auto;">
<?=getAnt($_GET["fra"]);?>
</div>
</div>
<?
}


?>

<form method="get" action="<?=$name;?>">
	Etwas sagen: <input type="text" name="fra" is="fra" value="<?=@$_GET["fra"];?>" size="30" />
</form>

</body>
</html>


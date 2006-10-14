<?
header("Expires: Mon, 26 Jul 1997 05:00:00 GMT");    // Datum aus Vergangenheit
header("Last-Modified: " . gmdate("D, d M Y H:i:s") . " GMT"); 
                                                     // immer geändert
header("Cache-Control: no-store, no-cache, must-revalidate");  // HTTP/1.1
header("Cache-Control: post-check=0, pre-check=0", false);
header("Pragma: no-cache");  

error_reporting(E_ALL | E_NOTICE);

$keycache = array();

$name = "jeliza.php";

define("TRUE", "a");
define("FALSE", "z");

include "head.php";
?>


<h2>JEliza (online Version)</h2>

<?

require "jeliza-ultra.php";

if (@strlen($_GET["fra"]) > 0) {
?>

Sie sagten: "<u><?=noSonderZeichen($_GET["fra"]);?></u>"

<div style="height: 360px; width: 300px; background-image: url(tft.gif);">
<div style="position: relative; top: 50px; left: 30px; height: 190px; width: 220px;
	overflow-x:hidden; overflow-y:auto;">
<?=getAnt($_GET["fra"]);?>
</div>
</div>
<?
}


?>

<form method="get" action="<?=$name;?>">
	Etwas sagen: <input type="text" name="fra" is="fra" value="<?=@$_GET["fra"];?>" size="30" />
</form>


<? include "foot.php"; ?>

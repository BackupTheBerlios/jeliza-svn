<?
## PHP-Script das PHP-Script-Aufrufe von Tobias schulz Webserver moeglich macht ##

####################################### Allgemeines ########################################

$args = $_SERVER["argv"];
$dir = substr($args[1],0,strrpos($args[1],"/"));
/*$dir = substr($dir,0,-1);
$dir = substr($dir,0,strrpos($dir,"/"));*/

chdir($dir);


####################################### Variablen ########################################
if($args[2] == "POST"){
	# POST-Vars
	if ($fp=fopen("php://stdin","r")) {
			$line = fgets($fp);
			$all .= $line;
		fclose($fp);
	}
	$_POST2 = explode("&", $all);
	for($x=0;$x<=count($_POST2);$x++) {
		$tmp = explode("=", $_POST2[$x]);
		$_POST[$tmp[0]] = $tmp[1];
	}
}

if($args[2] == "GET"){
	# GET-Vars
	$all .= $args[3];
	$_GET2 = explode("&", $all);
	for($x=0;$x<=count($_POST2) - 1;$x++) {
        	$tmp = explode("=", $_GET2[$x]);
	        $_GETT[$tmp[0]] = $tmp[1];
	}
}


include($args[1]);

$_ENV["HTTP_HOST"]= "my";

$_SERVER["SCRIPT_FILENAME"] = $args[1];
$SCRIPT_FILENAME = $args[1];

?>

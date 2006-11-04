<?

echo "<html><head><title>Informationen zu " . $_GET['ip'] . "</title></head><body><br />";
echo "<br />";
echo "<br />";
echo "<h2>IP-Adresse: " . $_GET['ip'] . "</h2>";
echo "<dl>";
echo "<dt>nslookup</dt><dd><pre>";
system("nslookup '" . $_GET['ip'] . "'");
echo "</pre></dd><dt>ping</dt><dd><pre>";
system("ping -R -c 10 -s 512 '" . $_GET['ip'] . "'");


?>

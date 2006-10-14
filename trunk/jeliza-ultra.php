<?

function ichToDu($ant) {
	$ant = " " . $ant . " ";

	$ant = str_replace(" Du ", " XDXuX ", $ant);
	$ant = str_replace(" Dein", " XDXeXiXnX", $ant);
	$ant = str_replace(" Dir ", " XDXiXrX ", $ant);
	$ant = str_replace(" Dich ", " XDXiXcXhX ", $ant);

	$ant = str_replace(" du ", " XDXuX ", $ant);
	$ant = str_replace(" dein", " XDXeXiXnX", $ant);
	$ant = str_replace(" dir ", " XDXiXrX ", $ant);
	$ant = str_replace(" dich ", " XDXiXcXhX ", $ant);

	$ant = str_replace(" ich ", " Du ", $ant);
	$ant = str_replace(" mein", " Dein", $ant);
	$ant = str_replace(" mir ", " Dir ", $ant);
	$ant = str_replace(" mich ", " Dich ", $ant);

	$ant = str_replace(" Ich ", " du ", $ant);
	$ant = str_replace(" Mein", " dein", $ant);
	$ant = str_replace(" Mir ", " dir ", $ant);
	$ant = str_replace(" Mich ", " dich ", $ant);

	$ant = str_replace(" wir ", " XiXhXrX ", $ant);
	$ant = str_replace(" Wir ", " XIXhXrX ", $ant);
	$ant = str_replace(" unser", " XuXnXsXeXrX", $ant);
	$ant = str_replace("Unser", "XUXnXsXeXrX", $ant);
	$ant = str_replace(" unsere", " XeXuXrXeX ", $ant);
	$ant = str_replace(" uns", " XeXuXcXhX ", $ant);

	$ant = str_replace(" ihr ", " wir ", $ant);
	$ant = str_replace(" Ihr ", " Wir ", $ant);
	$ant = str_replace(" euer", " unser", $ant);
	$ant = str_replace(" eure ", " unsere", $ant);
	$ant = str_replace(" euch ", " uns", $ant);

	$ant = str_replace(" XiXhXrX ", " ihr ", $ant);
	$ant = str_replace(" XIXhXrX ", " Ihr ", $ant);
	$ant = str_replace(" XuXnXsXeXrX", " euer", $ant);
	$ant = str_replace("XUXnXsXeXrX", "Euer", $ant);
	$ant = str_replace(" XeXuXrXeX ", " eure", $ant);
	$ant = str_replace(" XeXuXcXhX ", " Euch", $ant);

	$ant = str_replace(" XDXuX ", " ich ", $ant);
	$ant = str_replace(" XDXeXiXnX", " mein", $ant);
	$ant = str_replace(" XDXiXrX ", " mir ", $ant);
	$ant = str_replace(" XDXiXcXhX ", " mich ", $ant);
	return $ant;
}

function isQues($line) {
	$line = strtolower($line);
	if (substr_count($line, '?') > 0
			|| substr_count($line, 'wie') > 0
			|| substr_count($line, 'was') > 0
			|| substr_count($line, 'wer') > 0
			|| substr_count($line, 'wo') > 0
			|| substr_count($line, 'wann') > 0
			|| substr_count($line, 'warum') > 0
			|| substr_count($line, 'wieso') > 0
			|| substr_count($line, 'weshalb') > 0
			|| substr_count($line, 'welch') > 0
	) {
		return true;
	}

	return false;
}

function isKey($key) {

	if (@$GLOBALS['keycache'][$key] == FALSE) {
		return false;
	}
	if (@$GLOBALS['keycache'][$key] == TRUE) {
		return true;
	}

	
	$nokeys = explode(" ", "ja nein aber und oder" .
			" doch der die das ich du er sie es" .
			" meiner deiner seiner ihrer seiner mir dir" .
			" ihm ihr ihm mich dich ihn sie es wir" .
			" ihr sie unser euer ihrer uns euch ihnen" .
			" uns euch sie mein meine meiner meines" .
			" meinem meinen dein deine deiner deines deinem" .
			" deinen sein seine seiner seines seinem seinen" .
			" ihr ihre ihrer ihres ihrem ihren unser" .
			" unsere unseres unserem unserer unseren euer" .
			" eures eueres eurer euerer eure euere euren" .
			" eueren der die das die dessen deren dessen" .
			" deren derer dem der dem denen den die" .
			" das die dieser diese dieses diese dieses" .
			" dieser dieses dieser diesem dieser diesem" .
			" diesen diesen diese dieses diese jemand" .
			" niemand jemandes niemandes jemandem niemandem" .
			" jemanden niemanden " .
			" bin bist ist sind seid hab habe hast hat haben " .
			" was wer wie wo wann warum wieso weshalb" .
			" ein eine einer eins eines "
		);
	foreach ($nokeys as $num => $nokey) {
		if (trim($key) == trim($nokey)) {
			$GLOBALS['keycache'][$key] = FALSE;
			return false;
		}
	}
	$GLOBALS['keycache'][$key] = TRUE;
	return true;
}

function learn($satz) {
	if (isQues(trim($satz))) {
		return;
	}

	$satz = ichToDu($satz);

	$lines = explode("||", file_get_contents ('megajeliza/default/jeliza.sat.brn'));
	foreach ($lines as $line_num => $line) {
		$line = trim($line);
		if (strlen($line) > 2) {
			if (trim(strtolower($satz)) == trim(strtolower($line))) {
				return;
			}
		}
	}
	

//	echo "Learning " . $satz . "<br>";

	$h = fopen ("megajeliza/default/jeliza.sat.brn", "a");
	fwrite($h, "||" . $satz);
	fclose($h);
}

function noSonderZeichen($fra) {
	$fra = str_replace("ß", "ss", $fra);
	$fra = str_replace("ä", "ae", $fra);
	$fra = str_replace("ö", "oe", $fra);
	$fra = str_replace("ü", "ue", $fra);
	return $fra;
}

function readline($han) {
   $o = "";
   $c = "";
   while ($c!="\r"&&$c!="\n") {
       $o.= $c;
       $c = fread($han, 1);
   }
   return $o;
}

function getAntUltra($fra) {
	srand ((double)microtime()*1000000);
	$bestKeyCount = 0;
	$ants = array();
	$keys = explode(" ", str_replace("?", "", str_replace("!", "", str_replace(".", "", 
			str_replace(",", "", str_replace(";", "", $fra))))));
	$lines = explode("||", file_get_contents ('megajeliza/default/jeliza.sat.brn'));
	foreach ($lines as $line_num => $line) {
		$line = trim($line);
		if (strlen($line) > 2) {
//			echo "Line #<b>{$line_num}</b> : " . htmlspecialchars($line) . "<br>\n";
			$tmp = ' ' . $line . ' ';
			$goodKeys = 0;
			foreach ($keys as $num => $key) {
					if (isKey(strtolower($key)) 
							&& substr_count(strtolower($tmp), 
							strtolower(' ' . trim($key) . ' ')) > 0) {
//						echo $key . '';
						$goodKeys++;
						if ($goodKeys > $bestKeyCount) {
							$bestKeyCount = $goodKeys;
						}
					}
			}
	
			$ants[] = $goodKeys . '#' . trim($tmp);
//			echo $goodKeys . "##" . trim($tmp) . "<br />\n"; 
		}
	}
	
	$i = 0;
	$ants2 = array();
	foreach ($ants as $num => $antCdt) {
		if (substr_count(strtolower($antCdt), 
				strtolower($bestKeyCount . '#')) > 0) {
			$ants2[] = str_replace('#', '', strstr($antCdt, '#'));
			$i++;
		}
	}
	
	if ($i < 1) {
		return "Aha";
	}

	$ant = $ants2[rand(0, $i - 1)];
	
	learn($fra);

	return $ant;
}

function getAntSimple($fra) {
		if (substr_count(strtolower($fra), strtolower("tschue")) > 0)
			return "Auf Wiedersehen.";
		if (substr_count(strtolower($fra), strtolower("tschoe")) > 0)
			return "Auf Wiedersehen.";
		if (substr_count(strtolower($fra), strtolower("auf wiedersehen")) > 0)
			return "Auf Wiedersehen.";
		if (substr_count(strtolower($fra), strtolower("bis bald")) > 0)
			return "Auf Wiedersehen.";
		if (substr_count(strtolower($fra), strtolower("bis nachher")) > 0)
			return "Auf Wiedersehen.";
		if (substr_count(strtolower($fra), strtolower("auf bald")) > 0)
			return "Auf Wiedersehen.";

		if (substr_count(strtolower($fra), strtolower("danke")) > 0)
			return "Fuer was bedankst du dich?";
		if (substr_count(strtolower($fra), strtolower("bitte")) > 0)
			return "Warum sagst du bitte? Ich bin eine Maschine.";

		if (substr_count(strtolower($fra), strtolower("wieviel uhr")) > 0)
			return date("H:i:s");
		if (substr_count(strtolower($fra), strtolower("wie viel uhr")) > 0)
			return date("H:i:s");
		if (substr_count(strtolower($fra), strtolower("wie spaet")) > 0)
			return date("H:i:s");
		if (substr_count(strtolower($fra), strtolower("wie spät")) > 0)
			return date("H:i:s");

		if (substr_count(strtolower($fra), strtolower("der wievielte")) > 0)
			return "Es ist der " . date("d.M.Y");
		if (substr_count(strtolower($fra), strtolower("der wie vielte")) > 0)
			return "Es ist der " . date("d.M.Y");
		if (substr_count(strtolower($fra), strtolower("den wievielte")) > 0)
			return "Es ist der " . date("d.M.Y");
		if (substr_count(strtolower($fra), strtolower("den wie vielte")) > 0)
			return "Es ist der " . date("d.M.Y");
		
		return "";
}

function getAntWasIst($fra) {
	if (substr_count(strtolower($fra), "was ist ") < 1) {
		return "";
	} else {
		$fra = str_replace("?", "", $fra);
		$w = str_replace("was ist ", "", strtolower($fra));
		$w{0} = strtoupper($w{0});
		
#		$xml = file_get_contents("http://de.wikipedia.org/wiki/Spezial:Export/" . $w);

		$fp = fsockopen ("de.wikipedia.org", 80, $errno, $errstr, 30);
		if (!$fp) {
			echo "$errstr ($errno)<br />\n";
		}

#		$lines = explode("\n", $xml);
		$mode = 0;
#		foreach ($lines as $line_num => $line) {
		fputs ($fp, "GET /wiki/Spezial:Export/" . $w . " HTTP/1.0\r\n\r\n");
	   while (!feof($fp)) {
			$line = readline($fp);
			$line = trim($line);
			echo $line . "<br>\n";
		   if (strlen($line) > 2) {
		   	if ($mode == 3) {
		   		return $line;
		   	}
				if ($mode == 1) {
					if (substr_count(strtolower($line), '{{') > 0) {
						$mode = 2;
					}
				}
				if (substr_count(strtolower($line), '}}') > 0 && $mode != 3) {
					$mode = 1;
				}
				if ($mode == 1) {
					$mode = 3;
				}
				if (substr_count(strtolower($line), '<text xml:space="preserve">') > 0) {
					$mode = 1;
				}
			}
		}
	   fclose($fp);			
			
	}
	return "Das weiss ich nicht!";
}

function getAnt($fra) {
	$fra = noSonderZeichen($fra);
	
	$ant = getAntSimple($fra);
	
#	if ($ant == "") {
#		$ant = getAntWasIst($fra);
#	}

	if ($ant == "") {
		$ant = getAntUltra($fra);
	}

	$h = fopen ("jeliza-web.log", "a");
	$date = date("d M Y, H:i:s");
	fwrite($h, "\n" . $date . " : User:   " . $fra);
	fwrite($h, "\n" . $date . " : JEliza: " . $ant);
	fclose($h);

	return $ant;
}



?>

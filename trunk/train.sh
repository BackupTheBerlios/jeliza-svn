echo -n > tmp.jwwf
echo -n > tmp
for x in 1 2 3 4 5 6 7 8 9 0 ; do
for x in 1 2 3 4 5 6 7 8 9 0 ; do
for x in 1 2 3 4 5 6 7 8 9 0 ; do
	echo -n > tmp2
	fortune | grep -v -- "--" | sed 's@ä@ae@g' | sed 's@ü@ue@g' | sed 's@ö@oe@g' | sed 's@ß@ss@g' | grep -v -i stilb | grep -v -i kinder > tmp
	for x in `cat tmp`; do echo -n "$x " >> tmp2; done
	cat tmp2
	echo
	cat tmp2 >> tmp.jwwf
	echo >> tmp.jwwf
	sleep 0.5
	echo
done
done
done

sh find-dep-cpps.sh | sed 's@include/Poco/Util/@src/@g' | sed 's@include/Poco/Net/@src/@g' > tmp3
cat tmp3 | sed 's@include/Poco/@src/@g' | sed 's@h @cpp @g' > tmp2
cat tmp2 | sort | uniq > tmp
rm tmp2
sh tmp

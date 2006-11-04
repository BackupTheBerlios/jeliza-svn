#!/bin/sh -x
cd bin
jar cf ../jeliza.jar * 
cd ..
export OLDDIR=`pwd`
mkdir /tmp/bin_extra$$
cd /tmp/bin_extra$$
jar xf ${OLDDIR}/lib/bin_extra.jar 
jar uf ${OLDDIR}/jeliza.jar *
cd ${OLDDIR}


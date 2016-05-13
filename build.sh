#!/usr/bin/env bash

VERSION="1.6.8-beta1"

LTS_BIN="${BASH_SOURCE-$0}"
LTS_BIN="$(dirname "${LTS_BIN}")"
LTS_Bin_Dir="$(cd "${LTS_BIN}"; pwd)"

# delete old dist dir
[ -d ${LTS_Bin_Dir}/dist ] && rm -rf ${LTS_Bin_Dir}/dist 

cd $LTS_Bin_Dir

mvn clean install -U -DskipTests

Dist_Bin_Dir="$LTS_Bin_Dir/dist/lts-$VERSION-bin"
mkdir -p $Dist_Bin_Dir

Dist_Bin_Dir="$(cd "$(dirname "${Dist_Bin_Dir}/.")"; pwd)"

mkdir -p $Dist_Bin_Dir

# 打包
Startup_Dir="$LTS_Bin_Dir/lts-startup/"
cd $Startup_Dir
mvn clean assembly:assembly -DskipTests -Pdefault

cp -rf $Startup_Dir/target/lts-bin/lts/*  $Dist_Bin_Dir

mkdir -p $Dist_Bin_Dir/war/jetty/lib
mvn clean assembly:assembly -DskipTests -Plts-admin
cp -rf $Startup_Dir/target/lts-bin/lts/lib  $Dist_Bin_Dir/war/jetty
cp -rf $LTS_Bin_Dir/lts-admin/target/lts-admin-$VERSION.war $Dist_Bin_Dir/war/lts-admin.war

cp ${LTS_Bin_Dir}/lts-jobclient/target/lts-jobclient-${VERSION}.jar ${LTS_Bin_Dir}/dist/lts-${VERSION}-bin/lib
cp ${LTS_Bin_Dir}/jobclient.sh ${LTS_Bin_Dir}/dist/lts-${VERSION}-bin/bin

#!/bin/sh

mvn clean package -DskipTests
rm env/repz.jar
cp target/repz.jar ./env
cd env
proxychains4 ./repz.jar --server.port=8083
cd ..


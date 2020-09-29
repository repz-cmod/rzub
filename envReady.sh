#!/bin/sh

mvn clean package -DskipTests
rm env/repz.jar
cp target/repz.jar ./env
cd env
./repz.jar
cd ..


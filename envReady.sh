#!/bin/sh

mvn clean package -DskipTests
rm env/rzub.jar
cp target/rzub.jar ./env
cd env
./rzub.jar --server.port=8083
cd ..


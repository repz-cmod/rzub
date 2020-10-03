@echo off
mvn clean package -DskipTests
mkdir run
copy .\target\repz.jar .\run
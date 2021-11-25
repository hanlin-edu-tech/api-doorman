#! /bin/bash

echo -e "*** Performance Testing ***\n" > performance-testing/report.txt

echo -e "\n====== no reverse proxy ======\n" >> performance-testing/report.txt
sid=`docker run --cpus="2" --memory="1g" --rm -d -p 8181:8181 --env QUARKUS_HTTP_PORT=8080 hotdog929/performance-testing-quarkus-native`
groovy performance-testing/tester.groovy >> performance-testing/report.txt 2>&1
docker stop $sid
sleep 1m

echo -e "\n====== api-doorman ======\n" >> performance-testing/report.txt
sid=`docker run --cpus="2" --memory="1g" --rm -d -p 8181:8181 --env QUARKUS_HTTP_PORT=8181 hotdog929/performance-testing-quarkus-native`
pid=`docker run --cpus="2" --memory="1g" --rm -d -p 8080:8080 api-doorman-native`
groovy performance-testing/tester.groovy >> performance-testing/report.txt 2>&1
docker stop $sid
docker stop $pid
sleep 1m

echo -e "\n====== nginx ======\n" >> performance-testing/report.txt
sid=`docker run --cpus="2" --memory="1g" --rm -d -p 8181:8181 --env QUARKUS_HTTP_PORT=8181 hotdog929/performance-testing-quarkus-native`
pid=`docker run --cpus="2" --memory="1g" --rm -d -p 8080:8080 nginx-proxy`
docker stop $sid
docker stop $pid
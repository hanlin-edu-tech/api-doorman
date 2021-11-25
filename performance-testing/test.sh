#! /bin/bash

echo -e "*** Performance Testing ***\n" > performance-testing/report.txt

echo -e "\n====== direct ======\n" >> performance-testing/report.txt
sid=`docker run --cpus="2" --memory="1g" --rm -d -p 8080:8080 hotdog929/performance-testing-quarkus-native`
echo "= init =" >> performance-testing/report.txt
groovy performance-testing/tester.groovy >> performance-testing/report.txt 2>&1
sleep 1m
echo "= warmup =" >> performance-testing/report.txt
groovy performance-testing/tester.groovy >> performance-testing/report.txt 2>&1
docker stop $sid
sleep 1m

echo -e "\n====== api-doorman-native ======\n" >> performance-testing/report.txt
sid=`docker run --cpus="2" --memory="1g" --rm -d -p 8181:8080 hotdog929/performance-testing-quarkus-native`
pid=`docker run --cpus="2" --memory="1g" --rm -d -p 8080:8080 -:Wq-add-host host.docker.internal:host-gateway hotdog929/api-doorman-native`
echo "= init =" >> performance-testing/report.txt
groovy performance-testing/tester.groovy >> performance-testing/report.txt 2>&1
sleep 1m
echo "= warmup =" >> performance-testing/report.txt
groovy performance-testing/tester.groovy >> performance-testing/report.txt 2>&1
docker stop $sid
docker stop $pid
sleep 1m

echo -e "\n====== nginx-proxy ======\n" >> performance-testing/report.txt
sid=`docker run --cpus="2" --memory="1g" --rm -d -p 8181:8181 --env QUARKUS_HTTP_PORT=8181 hotdog929/performance-testing-quarkus-native`
pid=`docker run --cpus="2" --memory="1g" --rm -d -p 8080:8080 --add-host host.docker.internal:host-gateway hotdog929/nginx-proxy`
echo "= init =" >> performance-testing/report.txt
groovy performance-testing/tester.groovy >> performance-testing/report.txt 2>&1
sleep 1m
echo "= warmup =" >> performance-testing/report.txt
groovy performance-testing/tester.groovy >> performance-testing/report.txt 2>&1
docker stop $sid
docker stop $pid
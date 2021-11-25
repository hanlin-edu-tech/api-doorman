#! /bin/bash

docker build -f performance-testing/nginx/Dockerfile -t nginx-proxy .

docker build -f performance-testing/api-doorman/Dockerfile.native -t api-doorman-native .
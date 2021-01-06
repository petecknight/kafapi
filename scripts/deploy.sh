#!/usr/bin/env bash
./gradlew clean build
docker build --tag petecknight/kafapi:1.0.0 .
kubectl apply -f kubernetes/deployment.yaml

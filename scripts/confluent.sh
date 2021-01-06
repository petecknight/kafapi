#!/usr/bin/env bash

confluent local services destroy
confluent local services start
kafka-topics --zookeeper localhost:2181 --create --topic messages --partitions 24 --replication-factor 1
#kafka-topics --zookeeper localhost:2181 --alter --topic messages --partitions 24
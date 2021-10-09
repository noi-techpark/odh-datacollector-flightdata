# Flightdata

This project contains sources and tools to record flight data via [MQTT](https://mqtt.org/) and to store it in [DynamoDB](https://aws.amazon.com/dynamodb/).

## General architecture

- a [mosquitto](https://mosquitto.org/) instance listens for sensor data, the MQTT protocol is used
- a [Apache Camel](https://camel.apache.org/) instance listens for data on mosquitto and writes it into DynamoDB

## Scripts

Take a look at the [scripts](./scripts) folder to find helpful scripts.

### mosquitto-docker-start-.sh

The script [mosquitto-docker-start.sh](./scripts/mosquitto-docker-start.sh) starts a local docker container named `mosquitto` that runs a mosquitto instance and exposes ports 1883 and 9001.

### mosquitto-docker-sub-to-official-test-server.sh

The script [mosquitto-docker-sub-to-official-test-server.sh](./scripts/mosquitto-docker-sub-to-official-test-server.sh) starts a local running docker container named `mosquitto-sub-test.mosquitto.org` that subscribes to all topics on the server `test.mosquitto.org`. No ports are exposed. The output is written to stdout.

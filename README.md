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

## Integrator

The `integrator` folder provides sources for an Apache Camel instance that reads from a local MQTT instance and writes to a DynamoDB instance.

In order to configure the `integrator` you have to provide the following ENV variables:

- AWS_ACCESS_KEY_ID
- AWS_SECRET_ACCESS_KEY
- AWS_REGION

After the ENV variables are configured, the `integrator` instance can be started in development mode running the following command from the repository root folder:

```bash
mvn -f integrator compile quarkus:dev
```

You can also use the script [integrator-start-dev.sh](./scripts/integrator-start-dev.sh) to start `integrator` in development mode.

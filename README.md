# Flightdata

This project contains sources and tools to record flight data via [MQTT](https://mqtt.org/) and to store it in [DynamoDB](https://aws.amazon.com/dynamodb/).

## Table of Contents

- [General architecture](#general-architecture)
- [Getting started](#getting-started)
- [Scripts](scripts)

## General architecture

- a [mosquitto](https://mosquitto.org/) instance listens for sensor data, the MQTT protocol is used
- a [Apache Camel](https://camel.apache.org/) instance listens for data on mosquitto and writes it into DynamoDB

## Getting started

These instructions will get you a copy of this mono repository and prepare it for development.

### Prerequisites

To build the projects in the repository, the following prerequisites must be met:

- Docker to start a [mosquitto](https://mosquitto.org/) instance as MQTT broker
- Java JDK 11 or higher (e.g. [OpenJDK](https://openjdk.java.net/)) and [Maven](https://maven.apache.org/) 3.x to build and launch the integration pipeline based on Apache Camel

### Installing

Get a copy of the repository, e.g. by cloning it from the following location:

```bash
https://github.com/gappc/noi-flightdata.git
```

Change directory:

```bash
cd noi-flightdata/
```

Start `mosquitto` running the script `mosquitto-docker-local-start.sh` located inside the `scripts` folder. This script starts `mosquitto` in a Docker container and exposes its ports 1883 and 9001:

```bash
./scripts/mosquitto-docker-local-start.sh
```

To subscribe to all topics on the running `mosquitto` instance, use the script `mosquitto-docker-local-sub.sh` located inside the `scripts` folder:

```bash
./scripts/mosquitto-docker-local-sub.sh
```

To publish a message to the `topic/test` topic on the `mosquitto` instance, use the script `mosquitto-docker-local-pub.sh` located inside the `scripts` folder. This script takes the message payload as its argument:

```bash
./scripts/mosquitto-docker-local-pub.sh 'Hello World!'
```

To start the Apache Camel pipeline that listens for messages on `mosquitto` and publishes these messages to DynamoDB, run the script `integrator-start-dev.sh` located inside the `scripts` folder. But first you have to provide the following ENV variables to configure the access to your DynamoDB instance:

- AWS_ACCESS_KEY_ID
- AWS_SECRET_ACCESS_KEY
- AWS_REGION

After these ENV variables are set, start the integrator:

```bash
./scripts/integrator-start-dev.sh
```

You can now publish messages to `mosquitto` and see how they are written to DynamoDB.

> Hint: take a look at the [scripts](#scripts) section to get a description of the available scripts.

## Scripts

This section provides an overview of the available scripts that come with this project.

### mosquitto-docker-local-start.sh

The script [mosquitto-docker-local-start.sh](./scripts/mosquitto-docker-local-start.sh) uses the official Docker image [eclipse-mosquitto](https://hub.docker.com/_/eclipse-mosquitto/) to start a local Docker container named `mosquitto`. The container runs a mosquitto instance and exposes its ports 1883 and 9001.

### mosquitto-docker-local-pub.sh

The script [mosquitto-docker-local-pub.sh](./scripts/mosquitto-docker-local-pub.sh) uses the official Docker image [eclipse-mosquitto](https://hub.docker.com/_/eclipse-mosquitto/) to start a local Docker container named `mosquitto_pub` that can be used to publish messages to a local MQTT broker. The script expects one argument which is the payload. The payload is then published to the local MQTT broker on topic `test/topic`.

> Hint: Use [mosquitto-docker-local-start.sh](#mosquitto-docker-local-start.sh) to start a local MQTT broker.

### mosquitto-docker-local-sub.sh

The script [mosquitto-docker-local-sub.sh](./scripts/mosquitto-docker-local-sub.sh) uses the official Docker image [eclipse-mosquitto](https://hub.docker.com/_/eclipse-mosquitto/) to start a local Docker container named `mosquitto_pub` that can be used to subscribe to a local MQTT broker on all topics.

> Hint: Use [mosquitto-docker-local-start.sh](#mosquitto-docker-local-start.sh) to start a local MQTT broker.

### mosquitto-docker-sub-to-official-test-server.sh

The script [mosquitto-docker-sub-to-official-test-server.sh](./scripts/mosquitto-docker-sub-to-official-test-server.sh) uses the official Docker image [eclipse-mosquitto](https://hub.docker.com/_/eclipse-mosquitto/) to start a local Docker container named `mosquitto-sub-test.mosquitto.org` that subscribes to all topics on the server `test.mosquitto.org`. No ports are exposed. The output is written to stdout.

The reason for the script to exist is that it provides an easy start point to play around with MQTT. The MQTT broker at `test.mosquitto.org` should be online and pusblish data 24 / 7.

### integrator-start-dev.sh

This script starts the Apache Camel integration on localhost.

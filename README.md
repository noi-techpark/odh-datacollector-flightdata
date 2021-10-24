# Flightdata

This project contains sources and tools to record flight data via [MQTT](https://mqtt.org/) and to store it in a [PostgreSQL](https://www.postgresql.org/) database.

## Table of Contents

- [General architecture](#general-architecture)
- [Getting started](#getting-started)
- [Scripts](scripts)

## General architecture

- a [mosquitto](https://mosquitto.org/) instance listens for sensor data, the MQTT protocol is used
- a [Apache Camel](https://camel.apache.org/) instance listens for data on mosquitto and writes it into PostgreSQL
- a [PostgreSQL](https://www.postgresql.org/) instance where data is written to by Apache Camel

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

Start `mosquitto` running the script `mosquitto-docker-local-start.sh` located inside the `scripts` folder. This script starts `mosquitto` in a Docker container named `mosquitto` and exposes its ports 1883 and 9001:

```bash
./scripts/mosquitto-docker-local-start.sh
```

To subscribe to all topics on the running `mosquitto` instance, use the script `mosquitto-docker-local-sub.sh` located inside the `scripts` folder:

```bash
./scripts/mosquitto-docker-local-sub.sh
```

To publish a message to the `topic/test` topic on the `mosquitto` instance, use the script `mosquitto-docker-local-pub.sh` located inside the `scripts` folder. This script takes the message payload as its argument. Please be sure to provide **valid JSON** as payload, because PostgreSQL expects JSON as payload using its `JSONB` datatype:

```bash
./scripts/mosquitto-docker-local-pub.sh '{"text": "Hello World!"}'
```

Start `PostgreSQL` running the script `postgres-docker-local-start.sh` located inside the `scripts` folder. This script starts `PostgreSQL` in a Docker container named `postgres` and exposes its port 5432:

```bash
./scripts/postgres-docker-local-start.sh
```

> Note: the credentials used to start the `postgres` container must be the same that are used in the integrator.

To start the Apache Camel integrator that listens for messages on `mosquitto` and publishes these messages to PostgreSQL, run the script `integrator-start-dev.sh` located inside the `scripts` folder. Apache Camel uses the PostgreSQL configuration provided in [application.properties](./integrator/src/main/resources/application.properties). It is advised to change that configuration, e.g. by providing the followgin ENV variables:

- `DATABASE_URL`, e.g. `jdbc:postgresql://localhost:5432/flightdata`
- `DATABASE_NAME`, e.g. `flightdata`
- `DATABASE_USER`, e.g. `flightdata_user`
- `DATABASE_PASS`, e.g. `flightdata_pass`

> Note: the configured PostgreSQL credentials must be the same that are used to start the `postgres` container.

To start the integrator run the following script:

```bash
./scripts/integrator-start-dev.sh
```

You can now publish messages to `mosquitto` and see how they are written to PostgreSQL.

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

### postgres-docker-local-start.sh

The script [postgres-docker-local-start.sh](/scripts/postgres-docker-local-start.sh) uses the official Docker image for [PostgreSQL](https://hub.docker.com/_/postgres/) to start a local container named `postgres` that runs a PostgreSQL instance. Please take a look at the script to see the credentials that are used.

> Note: the credentials used to start the container must be the same that are used in the integrator.

### integrator-start-dev.sh

The script [postgres-docker-local-start.sh](/scripts/integrator-start-dev.sh) starts the Apache Camel integrator on localhost. This script uses no Docker container at all, so please be sure to have Java and Maven installed (see [prerequisites](#prerequisites)).

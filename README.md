# Flightdata

This project contains sources and tools to record flight data via [MQTT](https://mqtt.org/) and to store it in a [PostgreSQL](https://www.postgresql.org/) database.

## Table of Contents

- [General architecture](#general-architecture)
- [Getting started](#getting-started)
- [Development](#development)
- [Scripts](#scripts)
- [Information](#information)

## General architecture

- a [mosquitto](https://mosquitto.org/) instance listens for sensor data, the MQTT protocol is used
- a [Apache Camel](https://camel.apache.org/) instance listens for data on mosquitto and writes it into PostgreSQL. In addition, this instance provides a simple REST endpoint at path `/api` that can be used to read the data stored in PostgreSQL (experimental)
- a [PostgreSQL](https://www.postgresql.org/) instance where data is written to by Apache Camel

## Getting started

These instructions will get you a copy of this mono repository and prepare it for development. Please be aware that the provided implementation and configuration is yet not suitable for productive deployment (work in progress).

### Prerequisites

The project supports [Docker Compose](https://docs.docker.com/compose/) and provides a `docker-compose.yml` that can be used to start all required parts of the application.

To build the projects in the repository, the following prerequisites must be met:

- Docker
- Docker Compose
- Java JDK 11 or higher (e.g. [OpenJDK](https://openjdk.java.net/)) and [Maven](https://maven.apache.org/) 3.x if you want to build and launch the integration pipeline based on Apache Camel locally (not strictly necessary but nice to have for development)

### Installing

Get a copy of the repository, e.g. by cloning it from the following location:

```bash
https://github.com/gappc/noi-flightdata.git
```

Change directory:

```bash
cd noi-flightdata/
```

Make a copy of the `env.example` file named `.env`. The `.env` file contains the configuration and can be adjusted by your needs:

```bash
cp env.example .env
```

Start `docker-compose`:

```bash
docker-compose up
```

Docker Compose should now start three containers:

- mosquitto (MQTT)
- postgres (Database)
- integrator (Apache Camel)

> Note that the first launch may take some time, because the Docker images must be pulled and the `integrator` container has to download all necessary Maven dependencies.

To subscribe to all topics on the running `mosquitto` instance, use the script `mosquitto-docker-local-sub.sh` located inside the `scripts` folder:

```bash
./scripts/mosquitto-docker-local-sub.sh
```

To publish a message to the `topic/test` topic on the `mosquitto` instance, use the script `mosquitto-docker-local-pub.sh` located inside the `scripts` folder. This script takes the message payload as its argument. Please be sure to provide **valid JSON** as payload, because PostgreSQL expects JSON as payload using its `JSONB` datatype:

```bash
./scripts/mosquitto-docker-local-pub.sh '{"text": "Hello World!"}'
```

Data that is published to the MQTT broker should now be stored in the PostgreSQL instance. Use any database client you want to interact with PostgreSQL.

The `integrator` container provides also a simple REST endpoint to read the data stored in PostgreSQL. You can access it at port 8080 using the path `/api`, e.g. [http://localhost:8080/api](http://localhost:8080/api).

> Note that the `/api` endpoint returns at most 100 results only.

## Development

For development purpose, you can start any of the Docker Compose containers individually, e.g. `docker-compose up mosquitto`.

Usually you want to work on the `integrator`. The integrator reads from a MQTT broker (mosquitto) and writes to a PostgreSQL database. A common setup for development is therefor to launch mosquitto and PostgreSQL in their respective Docker containers and to start on `integrator` locally. This way you can e.g. easily debug the integrator. To get the described setup, follow the steps below.

Start `mosquitto` and `postgres` containers using docker-compose:

```bash
docker-compose up mosquitto postgres
```

Run the integrator locally in dev mode (please note that you need to have Java 11 and Maven 3.x installed):

```bash
mvn -f ./integrator/pom.xml compile quarkus:dev
```

This should get you started with development.

> Note that you don't have to start the `integrator` locally for development. You can start it as container together with the other containers with `docker-compose up` as described in the [Installing](#installing) section. The only drawback is, that it is not that easy to attach a debugger to the integrator inside the Docker container.

## Scripts

This section provides an overview of the available scripts that come with this project.

### mosquitto-docker-local-pub.sh

The script [mosquitto-docker-local-pub.sh](./scripts/mosquitto-docker-local-pub.sh) uses the official Docker image [eclipse-mosquitto](https://hub.docker.com/_/eclipse-mosquitto/) to start a local Docker container named `mosquitto_pub` that can be used to publish messages to a local MQTT broker. The script expects one argument which is the payload. The payload is then published to the local MQTT broker on topic `test/topic`.

> Hint: Use [mosquitto-docker-local-start.sh](#mosquitto-docker-local-start.sh) to start a local MQTT broker.

### mosquitto-docker-local-sub.sh

The script [mosquitto-docker-local-sub.sh](./scripts/mosquitto-docker-local-sub.sh) uses the official Docker image [eclipse-mosquitto](https://hub.docker.com/_/eclipse-mosquitto/) to start a local Docker container named `mosquitto_pub` that can be used to subscribe to a local MQTT broker on all topics.

> Hint: Use [mosquitto-docker-local-start.sh](#mosquitto-docker-local-start.sh) to start a local MQTT broker.

### mosquitto-docker-sub-to-official-test-server.sh

The script [mosquitto-docker-sub-to-official-test-server.sh](./scripts/mosquitto-docker-sub-to-official-test-server.sh) uses the official Docker image [eclipse-mosquitto](https://hub.docker.com/_/eclipse-mosquitto/) to start a local Docker container named `mosquitto-sub-test.mosquitto.org` that subscribes to all topics on the server `test.mosquitto.org`. No ports are exposed. The output is written to stdout.

The reason for the script to exist is that it provides an easy start point to play around with MQTT. The MQTT broker at `test.mosquitto.org` should be online and pusblish data 24 / 7.

## Information

### Support

For support, please contact [Christian Gapp](https://github.com/gappc).

### Contributing

If you'd like to contribute, please fork the repository and use a feature branch. Pull requests are warmly welcome.

### Versioning

This project uses [SemVer](https://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/noi-techpark/it.bz.opendatahub.databrowser/tags).

### License

The code in this project is licensed under the MIT license. See the LICENSE file for more information.

### Authors

- **Christian Gapp** - *Initial work* - [gappc](https://github.com/gappc)

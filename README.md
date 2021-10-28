# Flightdata

This project contains sources and tools to record flight data via [MQTT](https://mqtt.org/) and to store it in a [PostgreSQL](https://www.postgresql.org/) database.

## Table of Contents

- [General architecture](#general-architecture)
- [Getting started](#getting-started)
- [Configuration](#configuration)
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
git clone https://github.com/gappc/noi-flightdata.git
```

Change directory:

```bash
cd noi-flightdata/
```

Make a copy of the `env.example` file named `.env`. The `.env` file contains the configuration and can be adjusted by your needs:

> Note: the configuration in `env.example` allows anonymous MQTT access. If you want to enable MQTT authentication, use the file `env-with-auth.example` instead and refer to the [MQTT authentication](#mqtt-authentication) for further information.

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

To publish a message to the `flightdata` topic on the `mosquitto` instance, use the script `mosquitto-docker-local-pub.sh` located inside the `scripts` folder. This script takes the message payload as its argument. Please be sure to provide **valid JSON** as payload, because PostgreSQL expects JSON as payload using its `JSONB` datatype:

```bash
./scripts/mosquitto-docker-local-pub.sh '{"text": "Hello World!"}'
```

Data that is published to the MQTT broker should now be stored in the PostgreSQL instance. Use any database client you want to interact with PostgreSQL.

The `integrator` container provides also a simple REST endpoint to read the data stored in PostgreSQL. You can access it at port 8080 using the path `/api`, e.g. [http://localhost:8080/api](http://localhost:8080/api).

> Note that the `/api` endpoint returns at most 100 results only.

## Configuration

### Environment variables

If you use `docker-compose` you can configure MQTT and PostgreSQL properties using environment variables. The environment variables may also be provided by a `.env` file.

The project provides two environment example files that can be used as base configuration:

- copy the [env.example](./env.example) file to `.env` to use MQTT without authentication
- copy the [env-with-auth.example](./env-with-auth.example) file to `.env` to use MQTT with authentication

A description of the environment variables can be found in the example files mentioned above.

### MQTT authentication

This README.md mostly describes a project usage where MQTT allows anonymous access. This is fine for development purpose, but should not be done in production environments. That's why the project can also be configured with MQTT authentication enabled.

If you take a look at [env-with-auth.example](./env-with-auth.example) you will find an environment configuration that enables MQTT authentication. Copy that file to `.env` to use it as base for your own configuration with MQTT authentication enabled:

```bash
cp env-with-auth.example .env
```

The environment configuration defines [mosquitto-with-auth.conf](./docker/mosquitto/mosquitto-with-auth.conf) to be used as mosquitto config file using the `MOSQUITTO_CONF` environment variable. This mosquitto config contains a property `password_file` that references the password file [passwordfile.example](./docker/mosquitto/conf.d/passwordfile.example) as it is mounted inside the docker container (take a look at the [docker-compose.yml](./docker-compose.yml) file to see how the mount points are defined).

The password file defines one example user `user1` and its hashed password `password1`

> Hint: [https://mosquitto.org/documentation/authentication-methods/](https://mosquitto.org/documentation/authentication-methods/) provides more information about the password file specification and tooling.
>
> Use the script `mosquitto-docker-local-update-passwordfile.sh` located inside the `scripts` folder to hash the passwords of a plain-text password file in place. Take a look at `mosquitto-docker-local-update-passwordfile.sh` description in the [Scripts](#scripts) section for more information. **Attention**: the script does not check if the passwords are already hashed. If you apply the script several times to a password file, each time the passwords will be rehashed in place, rendering them useless.

Since MQTT is now password protected, the `integrator` must also authenticate against the MQTT in order to be able to subscribe to topics. Set the `INTEGRATOR_MQTT_USER` and `INTEGRATOR_MQTT_PASS` environment variables (e.g. in your `.env` file) to a user/password combination that is defined by your password file. If you use the provided `env-with-auth.example` file you will see, that `INTEGRATOR_MQTT_USER` is set to `user1` and `INTEGRATOR_MQTT_PASS` is set to `password1`, which are the values defined in the password file and were mentioned above.

From here on, your MQTT instance has authentication enabled and your integrator is configured with the according credentials. Modify the password file as you want, but please keep in mind that a valid user / password combination must be defined for `INTEGRATOR_MQTT_USER` and `INTEGRATOR_MQTT_PASS`.

### Further MQTT configuration

You can configure the MQTT further by adjusting the mosquitto config file that is mounted by `docker-compose`. Set the `MOSQUITTO_CONF` environment variable to define which mosquitto config file to use.

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

### mosquitto-docker-local-update-passwordfile.sh

The script [mosquitto-docker-local-update-passwordfile.sh](./scripts/mosquitto-docker-local-update-passwordfile.sh) uses the official Docker image [eclipse-mosquitto](https://hub.docker.com/_/eclipse-mosquitto/) to start a local Docker container named `mosquitto-update-password`. The script uses `mosquitto_passwd` under the hood to hash the passwords of the provided password file in place. The script expects the fully qualified path to the password file as parameter.

**Attention**: the script does not check if the passwords are already hashed. If you apply the script several times to a password file, each time the passwords will be rehashed, rendering them useless.

Below you find an example contents of a plain-text password file whose passwords are hashed by the script:

```text
user1:password1
```

> Note: the password file may contain several users, each one on a new line.

When you apply the script to that file, its outcome may look as follows:

```text
user1:$7$101$cNdSny7311rTLYeB$SSRWvaddDft6AnqSEtwfNoIQRVMOL2vlkPJWmlHMSYwbRQF4HZenS/CZO2H8K6tKl8tXDnJmPLaIVHLGfR+7YA==
```

You can see that the password was hashed. This file is now a valid `mosquitto` password file.

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

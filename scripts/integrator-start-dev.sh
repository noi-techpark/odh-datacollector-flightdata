#!/bin/bash

SCRIPT_RELATIVE_DIR=$(dirname "${BASH_SOURCE[0]}")

mvn -f "$SCRIPT_RELATIVE_DIR/../integrator/pom.xml" compile quarkus:dev
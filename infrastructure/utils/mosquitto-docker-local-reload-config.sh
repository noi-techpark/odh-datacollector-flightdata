#!/bin/bash

docker exec noi-flightdata_mosquitto_1 sh -c "kill -SIGHUP 1"

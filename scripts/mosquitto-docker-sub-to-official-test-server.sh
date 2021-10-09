#!/bin/bash

echo "Subscribing to test.mosquitto.org using running docker container named \"mosquitto\""

docker run -it --rm --name mosquitto-sub-test.mosquitto.org eclipse-mosquitto:2.0.12 sh -c "mosquitto_sub -h test.mosquitto.org -t '#' -v"

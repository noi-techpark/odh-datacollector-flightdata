#!/bin/bash

echo "Subscribing to all topics"

docker run -it --rm --name mosquitto-sub --network="host" eclipse-mosquitto:2.0.12 sh -c "mosquitto_sub -t '#' -v"

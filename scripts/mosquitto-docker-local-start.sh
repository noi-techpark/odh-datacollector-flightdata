#!/bin/bash

docker run -it --rm -p 1883:1883 -p 9001:9001 --name mosquitto --network="host" eclipse-mosquitto:2.0.12

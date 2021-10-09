#!/bin/bash

docker run -it -p 1883:1883 -p 9001:9001 --name mosquitto eclipse-mosquitto:2.0.12

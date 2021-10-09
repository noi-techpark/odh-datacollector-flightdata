#!/bin/bash

PAYLOAD=$1

if [[ -z $PAYLOAD ]]
then
  echo "Please provide a payload as parameter"
  exit 1
fi

docker run -it --rm --name mosquitto-pub --network="host" eclipse-mosquitto:2.0.12 sh -c "mosquitto_pub -t 'test/topic' -m '$PAYLOAD'"

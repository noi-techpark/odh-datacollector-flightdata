#!/bin/bash

FILE_PATH=$1

if [[ -z $FILE_PATH ]]
then
  echo "Please provide the full qualified path of the password file to update"
  exit 1
fi

docker run -it --rm --name mosquitto-update-password --network="host" -v "${FILE_PATH}:/tmp/password.pwd" -u "$(id -u ${USER})" eclipse-mosquitto:2.0.12 sh -c "mosquitto_passwd -U /tmp/password.pwd"

#!/bin/bash

docker-compose -f ./mongodb/docker-compose.yml up -d

mvn clean install -DskipTests

mvn azure-functions:run -DenableDebug
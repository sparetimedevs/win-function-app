#!/bin/bash

mvn clean install -DskipTests

mvn azure-functions:package

mvn azure-functions:run -DenableDebug
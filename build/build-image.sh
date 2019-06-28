#!/bin/bash
# this script creates the docker image needed for compiling the data set report
if [[ $0 != ./build/* ]]; then
  echo "Please run this script from the root of our repository!"
  exit -1
fi
docker build --tag=dzhw/dataset-report-task --build-arg JAR_FILE=$1 .

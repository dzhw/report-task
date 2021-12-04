#!/bin/bash
# this script gives an example for running the report generation
# in the docker container connecting to localhost
docker run -it --network=host dzhw/report-task java -jar /app/report-task.jar --task.id=dat-gra2005-ds2$ --task.version=1.2.3 --task.onBehalfOf=rreitmann --task.type=DATASET_REPORT --task.language=de --mdm.endpoint=http://127.0.0.1:8080

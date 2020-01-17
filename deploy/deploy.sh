#!/bin/bash
# this script deploys the container image to Amazon ECR
if [[ $0 != ./deploy/* ]]; then
  echo "Please run this script from the root of our repository!"
  exit -1
fi
$(aws ecr get-login --no-include-email --region eu-central-1 --profile mdm)
mvn dockerfile:push dockerfile:push@push-image-latest
if [ $? -ne 0 ]; then
    echo "docker push failed!"
    exit -1
fi

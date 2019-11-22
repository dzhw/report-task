#!/bin/bash
# this script deploys the project with a given profile to the correct space in
# cloudfoundry
if [[ $0 != ./deploy/* ]]; then
  echo "Please run this script from the root of our repository!"
  exit -1
fi
USERNAME="$1"
PASSWORD="$2"
TRAVIS_BRANCH="$3"
DOCKER_USER="$4"
DOCKER_PASSWORD="$5"
$(aws ecr get-login --no-include-email --region eu-central-1)
mvn dockerfile:push dockerfile:push@push-image-latest
if [ $? -ne 0 ]; then
    echo "docker push failed!"
    exit -1
fi
if [ "${TRAVIS_BRANCH}" = "master" ]; then
  PROFILE="prod"
fi
if [ "${TRAVIS_BRANCH}" = "test" ]; then
  PROFILE="test"
fi
if [ -z ${PROFILE} ]; then
  PROFILE="dev"
fi
if [ -z ${USERNAME} ] || [ -z ${PASSWORD} ]; then
  cf login -o DZHW -s ${PROFILE} -a https://api.run.pivotal.io
else
  cf login -o DZHW -s ${PROFILE} -u ${USERNAME} -p ${PASSWORD} -a https://api.run.pivotal.io
fi
if [ $? -ne 0 ]; then
    echo "cf login failed!"
    exit -1
fi
VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
cf push -f deploy/manifest.yml --var version=${VERSION}
if [ $? -ne 0 ]; then
    echo "cf push failed!"
    exit -1
fi

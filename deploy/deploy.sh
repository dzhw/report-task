#!/bin/bash
# this script deploys the container image to Amazon ECR
if [[ $0 != ./deploy/* ]]; then
  echo "Please run this script from the root of our repository!"
  exit -1
fi
PROFILE="$1"
TRAVIS_BRANCH="$2"
if [ "${PROFILE}" = "unused" ]; then
  PROFILE="dev"
fi
if [ "${TRAVIS_BRANCH}" = "test" ]; then
  PROFILE="test"
fi
if [ "${TRAVIS_BRANCH}" = "master" ]; then
  PROFILE="prod"
fi
if [ -z ${PROFILE} ]; then
  echo "Please provide a valid profile e.g.: $0 dev"
  exit -1
fi
AWS_CRED=($(aws sts assume-role --role-arn arn:aws:iam::347729458675:role/Admin --role-session-name travis-deployment --query 'Credentials.[AccessKeyId,SecretAccessKey,SessionToken]' --output text))
export AWS_ACCESS_KEY_ID=${AWS_CRED[0]}
export AWS_SECRET_ACCESS_KEY=${AWS_CRED[1]}
export AWS_SESSION_TOKEN=${AWS_CRED[2]}
$(aws ecr get-login --no-include-email --region eu-central-1)
mvn -P${PROFILE} dockerfile:push dockerfile:push@push-image-latest
if [ $? -ne 0 ]; then
    echo "docker push failed!"
    exit -1
fi

[![Build Status](https://travis-ci.org/dzhw/dataset-report-task.svg?branch=development)](https://travis-ci.org/dzhw/dataset-report-task)[![codecov](https://codecov.io/gh/dzhw/dataset-report-task/branch/development/graph/badge.svg)](https://codecov.io/gh/dzhw/dataset-report-task)[![Known Vulnerabilities](https://snyk.io//test/github/dzhw/dataset-report-task/badge.svg?targetFile=pom.xml)](https://snyk.io//test/github/dzhw/dataset-report-task?targetFile=pom.xml)
[![](https://images.microbadger.com/badges/version/dzhw/dataset-report-task.svg)](https://microbadger.com/images/dzhw/dataset-report-task "Get your own version badge on microbadger.com")
[![](https://images.microbadger.com/badges/image/dzhw/dataset-report-task.svg)](https://microbadger.com/images/dzhw/dataset-report-task "Get your own image badge on microbadger.com")![Docker Pulls](https://img.shields.io/docker/pulls/dzhw/dataset-report-task.svg)![Docker Stars](https://img.shields.io/docker/stars/dzhw/dataset-report-task.svg)

# Dataset Report Task

This repository contains the implementation for a containerized [Spring Cloud Task]. The task generates a Dataset Report by getting the relevant data from our [MDM], compiling a PDF and uploading it to the [MDM].

## Developers
Developers need to have at least `maven` and `docker` on their machines. Currently you need to install java 11 sdk on your system. On Ubuntu you should use [SDKMAN!].

The following environment variables have to be set for running the JUnit test:
```shell
MDM_ENDPOINT
MDM_TASK_USER
MDM_TASK_PASSWORD
```

The docker image can be build with:
```shell
mvn clean install
```

If you want to run the task against an [MDM] instance running on your local machine, you can run:
```shell
docker run -it --network=host dzhw/dataset-report-task java -jar /app/dataset-report-task.jar --task.dataSetId=dat-gra2005-ds2$ --task.version=1.2.3 --task.onBehalfOf=dataprovider --mdm.username=${MDM_TASK_USER} --mdm.password=${MDM_TASK_PASSWORD} --mdm.endpoint=http://127.0.0.1:8080
```

For further configuration options you should get familiar with [Spring Boot @ConfigurationProperties](https://www.baeldung.com/configuration-properties-in-spring-boot) and have a look into `src/main/java/eu/dzhw/fdz/metadatamanagement/tasks/datasetreporttask/config`.

## Template Editing
The latex files which are compiled within this task are generated with [FreeMarker]. The templates can be edited under `/src/main/resources/template/`. Changes to the latex styles and images can be made here:
`/latex-packages/doc/`

## Continous Integration
Every commit to the branches `development`, `test` or `master` will be pushed to [Docker Hub] and deployed to the corresponding space on [PWS], which is a [Cloudfoundry] instance. [TravisCI] is used for executing the build and pushing to [PWS].

## Issues
If you find any issues or have questions regarding this task, feel free to file an issue in our [MDM].

[MDM]: https://github.com/dzhw/metadatamanagement "Metadatamanagement"
[FreeMarker]: https://freemarker.apache.org/
[PWS]: https://run.pivotal.io/
[Cloudfoundry]: https://www.cloudfoundry.org/
[TravisCI]: https://travis-ci.org/
[Spring Cloud Task]: https://spring.io/projects/spring-cloud-task
[SDKMAN!]: https://sdkman.io/
[Docker Hub]: https://hub.docker.com/r/dzhw/dataset-report-task

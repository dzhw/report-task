[![Build and Deploy](https://github.com/dzhw/report-task/workflows/Build%20and%20Deploy/badge.svg)](https://github.com/dzhw/report-task/actions)[![codecov](https://codecov.io/gh/dzhw/report-task/branch/development/graph/badge.svg)](https://codecov.io/gh/dzhw/report-task)[![Known Vulnerabilities](https://snyk.io//test/github/dzhw/report-task/badge.svg?targetFile=pom.xml)](https://snyk.io//test/github/dzhw/report-task?targetFile=pom.xml)

# Report Task

This repository contains the implementation for a containerized [Spring Cloud Task]. The task generates reports by getting the relevant data from our [MDM], compiling a PDF and uploading it to the [MDM].

## Developers
Developers need to have at least `maven` and `docker` on their machines. Currently you need to install java 16 sdk on your system. On Ubuntu you should use [SDKMAN!].

The following environment variables have to be set for running the JUnit test:
```shell
MDM_ENDPOINT
MDM_TASK_USER
MDM_TASK_PASSWORD
```

The docker image can be build with:
```shell
mvn -Pdev clean install
```

If you want to run the task against an [MDM] instance running on your local machine, you can run:
```shell
docker run -it --network=host dzhw/report-task java -jar /app/report-task.jar --task.id=dat-gra2005-ds2$ --task.version=1.2.3 --task.onBehalfOf=dataprovider --task.language=de --task.type=DATA_SET_REPORT --mdm.username=${MDM_TASK_USER} --mdm.password=${MDM_TASK_PASSWORD} --mdm.endpoint=http://127.0.0.1:8080
```

For further configuration options you should get familiar with [Spring Boot @ConfigurationProperties](https://www.baeldung.com/configuration-properties-in-spring-boot) and have a look into `src/main/java/eu/dzhw/fdz/metadatamanagement/tasks/reporttask/config`.

## Template Editing
The latex files which are compiled within this task are generated with [FreeMarker]. The templates can be edited under `/src/main/resources/template/`. Changes to the latex styles and images can be made here:
`/latex-packages/doc/`

## Continous Integration
Every commit to the branches `development`, `test` or `master` will be pushed to [Amazon ECR]. [Github Actions] are used for executing the build and pushing to [AWS].

## Issues
If you find any issues or have questions regarding this task, feel free to file an issue in our [MDM].

[MDM]: https://github.com/dzhw/metadatamanagement "Metadatamanagement"
[FreeMarker]: https://freemarker.apache.org/
[AWS]: https://aws.amazon.com/?nc2=h_lg
[Amazon ECR]: https://aws.amazon.com/ecr/?nc1=h_ls
[Github Actions]: https://github.com/dzhw/report-task/actions
[Spring Cloud Task]: https://spring.io/projects/spring-cloud-task
[SDKMAN!]: https://sdkman.io/

#
# dzhw/dataset-report-task
#
# This is an image with a basic TeX Live installation and
# additional resources for DZHW-FDZ variable reports.
#
# Source: https://github.com/dzhw/dataset-report-task
#
FROM ubuntu:19.10
MAINTAINER René Reitmann <reitmann@dzhw.eu>
ARG JAR_FILE

RUN apt-get update &&\
    apt-get install -f -y language-pack-en language-pack-de

ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en' LC_ALL='en_US.UTF-8' DEBIAN_FRONTEND=noninteractive

# install texlive
RUN apt-get install -f -y apt-utils &&\
    apt-get install -f -y texlive-latex-base texlive-lang-german texlive-science fontconfig make

# install additional latex packages
RUN apt-get install -f -y texlive-latex-extra

# install calibri package
RUN mkdir /usr/share/texlive/texmf-dist/tex/latex/calibri
COPY latex-packages/fonts/Calibri /usr/share/texlive/texmf-dist/
RUN echo "Map Calibri.map" >> /usr/share/texlive/texmf-dist/web2c/updmap.cfg
RUN fc-cache && texhash && mktexlsr && updmap-sys

# install java
# see https://github.com/AdoptOpenJDK/openjdk-docker/blob/master/11/jdk/ubuntu/Dockerfile.hotspot.releases.full
RUN apt-get install -f -y curl
RUN set -eux; \
    ARCH="$(dpkg --print-architecture)"; \
    case "${ARCH}" in \
       aarch64|arm64) \
         ESUM='894a846600ddb0df474350037a2fb43e3343dc3606809a20c65e750580d8f2b9'; \
         BINARY_URL='https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.3%2B7/OpenJDK11U-jdk_aarch64_linux_hotspot_11.0.3_7.tar.gz'; \
         ;; \
       amd64|x86_64) \
         ESUM='23cded2b43261016f0f246c85c8948d4a9b7f2d44988f75dad69723a7a526094'; \
         BINARY_URL='https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.3%2B7/OpenJDK11U-jdk_x64_linux_hotspot_11.0.3_7.tar.gz'; \
         ;; \
       armhf) \
         ESUM='3fbe418368e6d5888d0f15c4751139eb60d9785b864158a001386537fa46f67e'; \
         BINARY_URL='https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.3%2B7/OpenJDK11U-jdk_arm_linux_hotspot_11.0.3_7.tar.gz'; \
         ;; \
       s390x) \
         ESUM='c80e775d96c4b6edf399414503d28788060829c345abc575fc731f9e4d68b3bc'; \
         BINARY_URL='https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.3%2B7/OpenJDK11U-jdk_s390x_linux_hotspot_11.0.3_7.tar.gz'; \
         ;; \
       ppc64el|ppc64le) \
         ESUM='25bce2f738cfc7c027da08e533bf3ede65e2767eae8eb9fcb46e92ee6aea7607'; \
         BINARY_URL='https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.3%2B7/OpenJDK11U-jdk_ppc64le_linux_hotspot_11.0.3_7.tar.gz'; \
         ;; \
       *) \
         echo "Unsupported arch: ${ARCH}"; \
         exit 1; \
         ;; \
    esac; \
    curl -LfsSo /tmp/openjdk.tar.gz ${BINARY_URL}; \
    echo "${ESUM} */tmp/openjdk.tar.gz" | sha256sum -c -; \
    mkdir -p /opt/java/openjdk; \
    cd /opt/java/openjdk; \
    tar -xf /tmp/openjdk.tar.gz --strip-components=1; \
    rm -rf /tmp/openjdk.tar.gz;

ENV JAVA_HOME=/opt/java/openjdk \
    PATH="/opt/java/openjdk/bin:$PATH"

# COPY the spring boot task jar
COPY ${JAR_FILE} /app/dataset-report-task.jar
COPY latex-packages/doc /app/doc

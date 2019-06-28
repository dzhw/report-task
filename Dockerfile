#
# dzhw/dataset-report-task
#
# This is an image with a basic TeX Live installation and,
# additional resources for DZHW-FDZ variable reports.
# Source: https://github.com/dzhw/dataset-report-task/
# License: GNU GENERAL PUBLIC LICENSE, Version 3, 29 June 2007
# The license applies to the way the image is built, while the
# software components inside the image are under the respective
# licenses chosen by their respective copyright holders.
#
FROM thomasweise/texlive
MAINTAINER Robert Birkelbach <birkelbach@dzhw.eu>
ARG JAR_FILE

RUN mkdir /usr/share/texlive/texmf-dist/tex/latex/calibri

COPY latex-packages/fonts/Calibri /usr/share/texlive/texmf-dist/
RUN echo "Map Calibri.map" >> /usr/share/texlive/texmf-dist/web2c/updmap.cfg
RUN fc-cache && texhash && mktexlsr && sudo updmap-sys

# install java
# see https://github.com/AdoptOpenJDK/openjdk-docker/blob/master/11/jdk/ubuntu/Dockerfile.hotspot.releases.full
ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en' LC_ALL='en_US.UTF-8'

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

ENTRYPOINT ["/bin/__boot__.sh"]

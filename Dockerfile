#
# dzhw/dataset-report-task
#
# This is an image with a basic TeX Live installation and
# additional resources for DZHW-FDZ dataset reports.
#
# Source: https://github.com/dzhw/dataset-report-task
#
FROM adoptopenjdk:15.0.2_7-jre-hotspot-focal
MAINTAINER René Reitmann <reitmann@dzhw.eu>
ARG JAR_FILE

# install texlive
RUN apt-get update \
 &&  apt-get install -y --no-install-recommends \
     texlive-latex-base texlive-lang-german texlive-lang-english texlive-science texlive-latex-extra fontconfig make\
 &&  rm -rf /var/lib/apt/lists/*

# install calibri package
RUN mkdir /usr/share/texlive/texmf-dist/tex/latex/calibri
COPY latex-packages/fonts/Calibri /usr/share/texlive/texmf-dist/
RUN echo "Map Calibri.map" >> /usr/share/texlive/texmf-dist/web2c/updmap.cfg
RUN fc-cache && texhash && mktexlsr && updmap-sys

# copy other document assets
COPY latex-packages/doc /app/doc
# COPY the spring boot task jar
COPY ${JAR_FILE} /app/dataset-report-task.jar

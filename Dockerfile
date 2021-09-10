#
# dzhw/report-task
#
# This is an image with a basic TeX Live installation and
# additional resources for FDZ-DZHW reports.
#
# Source: https://github.com/dzhw/report-task
#
FROM openjdk:16.0.2-slim
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
COPY ${JAR_FILE} /app/report-task.jar

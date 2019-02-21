#
# dzhw/dsreport-docker
#
# This is an image with a basic TeX Live installation and,
# additional resources for DZHW-FDZ variable reports.
# Source: https://github.com/dzhw/dsreport-docker/
# License: GNU GENERAL PUBLIC LICENSE, Version 3, 29 June 2007
# The license applies to the way the image is built, while the
# software components inside the image are under the respective
# licenses chosen by their respective copyright holders.
#
FROM thomasweise/texlive
MAINTAINER Robert Birkelbach <birkelbach@dzhw.eu>
RUN mkdir /usr/share/texlive/texmf-dist/tex/latex/calibri

ADD latex-packages/Calibri /usr/share/texlive/texmf-dist/
RUN echo "Map Calibri.map" >> /usr/share/texlive/texmf-dist/web2c/updmap.cfg
RUN fc-cache && texhash && mktexlsr && sudo updmap-sys

ENTRYPOINT ["/bin/__boot__.sh"]

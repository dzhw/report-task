#
# dzhw/variablereport
#
# This is an image with a basic TeX Live installation and,
# additional resources for DZHW-FDZ variable reports.
# Source: https://github.com/dzhw/variablereport-docker/
# License: GNU GENERAL PUBLIC LICENSE, Version 3, 29 June 2007
# The license applies to the way the image is built, while the
# software components inside the image are under the respective
# licenses chosen by their respective copyright holders.
#
FROM thomasweise/texlive
MAINTAINER Robert Birkelbach <birkelbach@dzhw.eu>
RUN mkdir /usr/share/texlive/texmf-dist/tex/latex/calibri

ADD latex-packages/Calibri/fonts/enc /usr/share/texlive/texmf-dist/fonts/enc
ADD latex-packages/Calibri/fonts/map /usr/share/texlive/texmf-dist/fonts/map
ADD latex-packages/Calibri/fonts/tfm/microsoft/Calibri /usr/share/texlive/texmf-dist/fonts/tfm/public/microsoft/Calibri
ADD latex-packages/Calibri/fonts/vf /usr/share/texlive/texmf-dist/fonts/vf
ADD latex-packages/Calibri/fonts/opentype /usr/share/texlive/texmf-dist/fonts/opentype
ADD latex-packages/Calibri/fonts/type1 /usr/share/texlive/texmf-dist/fonts/type1
ADD latex-packages/Calibri/tex/latex/microsoft/Calibri /usr/share/texlive/texmf-dist/tex/latex
RUN texhash && mktexlsr

ENTRYPOINT ["/bin/__boot__.sh"]
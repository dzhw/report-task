#!/bin/bash
# this script compiles the latex report into a pdf
if [[ $0 != ./bin/* ]]; then
  echo "Please run this script from the root of our repository!"
  exit -1
fi
rm ./output/* -f
OUTPUT="$1"
touch --no-create ${OUTPUT}
if [ $? -ne 0 ]; then
  echo "Please provide a valid output file (e.g.: ./output/report.pdf)!"
  exit -1
fi
docker run -v "${PWD}/input":/doc/ -t -i dzhw/dsreport-docker make clean
docker run -v "${PWD}/input":/doc/ -t -i dzhw/dsreport-docker make
cp ./input/Main.pdf ${OUTPUT}

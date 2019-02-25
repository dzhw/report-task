#!/bin/bash
# this script compiles the latex report into a pdf
if [[ $0 != ./bin/* ]]; then
  echo "Please run this script from the root of our repository!"
  exit -1
fi
rm ./output/* -f
INPUT_DIRECTORY=`realpath $1`
if [[ ! -d "${INPUT_DIRECTORY}" ]]; then
  echo "The input directory '${INPUT_DIRECTORY}' does not exist!"
  exit -1
fi
OUTPUT="$2"
touch --no-create ${OUTPUT}
if [ $? -ne 0 ]; then
  echo "Please provide a valid output file (e.g.: ./output/report.pdf)!"
  exit -1
fi
docker run -v "${INPUT_DIRECTORY}":/doc/ -t -i dzhw/dsreport-docker make clean
docker run -v "${INPUT_DIRECTORY}":/doc/ -t -i dzhw/dsreport-docker make
cp ${INPUT_DIRECTORY}/Main.pdf ${OUTPUT}

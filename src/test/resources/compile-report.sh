#!/bin/bash
# this script compiles the latex report into a pdf during integration tests
INPUT_DIRECTORY=`realpath ./target/test-classes/doc`
if [[ ! -d "${INPUT_DIRECTORY}" ]]; then
  echo "The input directory '${INPUT_DIRECTORY}' does not exist!"
  exit -1
fi
OUTPUT="./target/report.pdf"
rm ${OUTPUT} -f
./ta
docker run -v "${INPUT_DIRECTORY}":/doc/ dzhw/dataset-report-task make clean
docker run -v "${INPUT_DIRECTORY}":/doc/ dzhw/dataset-report-task make
cp ${INPUT_DIRECTORY}/Main.pdf ${OUTPUT}

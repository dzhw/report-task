#!/bin/bash
# this script simulates compilation of the latex report into a pdf during integration tests
INPUT_DIRECTORY="./target/test-classes/doc"
OUTPUT="./target/report.pdf"
cp ${INPUT_DIRECTORY}/Main.pdf ${OUTPUT}

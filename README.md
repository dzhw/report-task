# Erstellung von Dataset-Reports

Um einen Datensatzreport zu erstellen, muss zunächst das `template` Verzeichnis ins MDM hochgeladen werden.

Man erhält ein zip-File, welches in das `input` Verzeichnis extrahiert werden muss.

Das benötigte Docker Image kann man mit folgendem Befehl erstellen:
```shell
./bin/build-image.sh
```

Anschließend wird mit folgendem Befehl ein PDF aus den LaTex Dateien in dem `input` Verzeichnis erstellt und in das `output` Verzeichnis kopiert.
```shell
./bin/compile-report.sh ./output/report.pdf
```

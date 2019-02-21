# Erstellung von Dataset-Reports

Um einen Datensatzreport zu erstellen, ist zunächst der Template-Ordner ins MDM
hochzuladen. Diesen findet man [hier](https://github.com/dzhw/metadatamanagement-io/tree/master/datasetreport/template).

Man erhält ein zip-File, welches extrahiert werden muss.
Wenn man das dsreport-docker repo gecloned hat, wechselt man in den image
Ordner, führt den docker build Befehl aus und startet das image wie folgt:

```
cd image
docker build --tag=dsreport-docker .
docker run -v "pathtogeneratedtexfiles":/doc/ -t -i dsreport-docker
```

Anschließend muss noch `make` bzw. `make clean && make` ausgeführt werden.

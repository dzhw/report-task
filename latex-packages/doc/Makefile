 filename=Main.tex

pdf:
	pdflatex -synctex=1 -interaction=errorstopmode -file-line-error -halt-on-error ${filename}
	pdflatex -synctex=1 -interaction=errorstopmode -file-line-error -halt-on-error ${filename}

clean:
	rm -f ${filename}.{ps,pdf,log,aux,out,dvi,bbl,blg}

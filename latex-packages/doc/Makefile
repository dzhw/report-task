filename=Main.tex

# pdflatex is invoked twice to add page numbers to the table of content
pdf:
	pdflatex -synctex=1 -interaction=errorstopmode -file-line-error -halt-on-error ${filename}
	pdflatex -synctex=1 -interaction=errorstopmode -file-line-error -halt-on-error ${filename}

clean:
	rm -f ${filename}.{ps,pdf,log,aux,out,dvi,bbl,blg}

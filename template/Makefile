 filename=Main.tex

pdf:
	pdflatex -synctex=1 -interaction=nonstopmode ${filename}
	pdflatex -synctex=1 -interaction=nonstopmode ${filename}

clean:
	rm -f ${filename}.{ps,pdf,log,aux,out,dvi,bbl,blg}

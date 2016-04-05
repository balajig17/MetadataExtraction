import subprocess
import json
import ast
import tika
tika.initVM()
from tika import parser
import os
import sys
from json import JSONDecoder
import time
import glob

def gdal_results(file_name,output_file):
	command_sub = "java -jar /Users/ayesh/Desktop/ContentDetection/Assignment2/grobidparser-resources/tika-app-1.12.jar -m "+file_name
	first = subprocess.check_output(command_sub, shell=True)
	output_file.write(first)

with open("output_file.txt",'w') as opFile:
	files = glob.glob('/Users/ayesh/Desktop/ContentDetection/Assignment2/GDAL_DATA/*')
	for every_file in files:
		print os.getcwd()
		fname =  os.path.basename(every_file)
		fname = every_file
		extension = os.path.splitext(fname)[1]
		if extension != '.py':
			gdal_results(fname,opFile)
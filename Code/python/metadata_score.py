import tika
tika.initVM()
from tika import parser
import glob
import os

def metadata_score(filename,doi=True,index_s=True):
	try:
		parsed = parser.from_file(filename)
		metadata = parsed["metadata"]
		print metadata
		total_score = 0
		good_metadata_score = 0
		doi_score = 0
		index_score = 0
		good_metadata = ['Author','Content-Type','Last-Modified','Creation-Date','title']
		for m in metadata:
			if m in good_metadata:
				good_metadata_score += 1
		if doi:
			doi_score = 1
		if index_s:
			index_score += 1
		total_score = (good_metadata_score*0.12) + (doi_score*0.2) + (index_s*0.2)
		return total_score
	except KeyError:
		return 0


#files = glob.glob('/Users/ayesh/Desktop/ContentDetection/Assignment2/grobid/papers/*')
files = glob.glob('/Users/ayesh/Desktop/ContentDetection/Assignment2/output/*')
for every_file in files:
	parsed = parser.from_file(every_file)
	#print parsed
	metadata = parsed["metadata"]
	print metadata_score(every_file)
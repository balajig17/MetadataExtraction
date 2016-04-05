import subprocess
import json
import ast
import tika
tika.initVM()
from tika import parser
import glob
import scholar
import os
import sys
from json import JSONDecoder
import time

def send_multiple_scholar_requests(author1, author2,filename):
	print 'here'
	try:
		c = 0
		first = subprocess.check_output('python scholar.py --author' +author1+ '" --json', shell=True)
		second = subprocess.check_output('python scholar.py --author' +author2+ '" --json', shell=True)
		print '----------------------------------------------'
		if first is not None:
			c = first.count('Cluster ID')
		if second is not None:
			c += second.count('Cluster ID')
			if c >= 20:
				with open("../output/"+filename+'.txt','w') as m:
					m.write(first)
					m.write(second)
			else:
				print 'Not enough similar docs'
		else:
			print 'Nothing from Scholar'
		print '----------------------------------------------'
	except subprocess.CalledProcessError:
		print 'Called Process Error '

def send_scholar_request(author, title,filename):

	print 'In scholar request. Author is:', author
	c = 0
	authors = []
	try:
		authors = author.split(',')
		if len(authors) > 1:
			send_multiple_scholar_requests(authors[0],authors[1],filename)
	except AttributeError:
		print 'One author only.'
	else:
		try:
			print 'Sleeping'
			time.sleep(5)
			print 'Continuing'
			first = subprocess.check_output('python scholar.py --author "' +author+ '" --json', shell=True)
			print '----------------------------------------------'
			if first is not None:
				c = first.count('Cluster ID')
				if c >= 20:
					with open("../output/"+filename+'.txt','w') as m:
						m.write(first)
				else:
					print 'Not enough similar docs'
			else:
				print 'Nothing from Scholar'
			print '----------------------------------------------'
		except subprocess.CalledProcessError:
			print 'Called Process Error'

def send_curl_request(file_path,file_name):
	print 'Sending curl request'
	curl_req = "curl -T "+file_path+" -H \"Content-Disposition: attachment;filename="+file_name+"\" http://localhost:9998/rmeta"
	return_code = subprocess.check_output(curl_req, shell=True)
	if len(return_code) > 0:
		try:
			d = ast.literal_eval(return_code)
			author = d[0]['Author']
			title = d[0]['title']
			
			send_scholar_request(author, title, file_name)
		except KeyError,SyntaxError:
			print 'No author or title'



files = glob.glob('/Users/ayesh/Desktop/ContentDetection/Assignment2/Data/*')
other_path = '/Users/ayesh/Desktop/ContentDetection/Assignment2/Data/'
for every_file in files:
	all_data = ""
	filename = os.path.basename(every_file)
	actual_filename = os.path.splitext(filename)[0]
	output_filename = os.path.splitext(actual_filename)[0]
	send_curl_request(every_file,filename)
	# with open("new_op/"+output_filename,'w') as o:
	# 	with open(every_file,'r') as p:
	# 		all_dat = p.readlines()
	# 		for x in all_dat:
	# 			all_data += x
	# 		o.write(all_data)
	# 	#print type(received_output)
	# 	#print received_output[0]
	# 	o.write(str(received_output[0]))
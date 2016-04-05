__author__ = 'canmenekse'
import os
from os.path import isfile, join
import re
import json



def  writeFileToJson(json,filename):
    file=open(filename,'w')
    file.write(json)
    print("created " + filename)
    file.close()




path = "/Users/canmenekse/Downloads/grobidData"

files = onlyfiles = [f for f in os.listdir(path) if (join(path, f))]
reiter = re.finditer( r'({.*?Cluster.*?})',"{ Cluster }", re.M|re.I|re.S);
for match in reiter:
    print("Matched Cluster");
    print(match.group(1));
i = 0
all_keys=['Cluster ID','Versions','URL','Title','Excerpt','Citations','related_articles','Year','Versions list','Citations list']
jsonStrLst=[]
newJson = dict();
for file in files:
   if i<150:
    with open(join(path,file), 'r') as grobidFile:
        print(file)
        newJson.clear()
        if(file[0]!='.'):
            data=grobidFile.read().replace('\n', '')
            #tikaJson=""
            #tikaJsonMatch = re.search(r"({'access_permission:can_modify'.*)",data,re.M|re.I|re.S)
            #if(tikaJsonMatch):
            #    tikaJson=tikaJsonMatch.group(1)
            #    print(json.dumps(tikaJson))
            #    #tika_content = json.loads(tikaJson)
            #    #print(tika_content)
            titleMatch = re.search(r"'title': '(.*?)'",data,re.M|re.I|re.S)
            title=""
            if titleMatch:
                title=titleMatch.group(1)
            else:
                print("not matched");
            authorMatch = re.search(r"'Author': '(.*?)',",data,re.M|re.I|re.S)
            if authorMatch:
                authorName = authorMatch.group(1);
            reiter = re.finditer( r'({.*?Cluster ID.*?})',data, re.M|re.I|re.S);
            counter = 0
            for match in reiter:
                counter+=1
                matched = match.group(1)
                jsonObj = json.loads(matched);
                for key in all_keys:
                   if key in jsonObj:
                        content = jsonObj[key];
                        del jsonObj[key]
                        jsonObj[key+str(counter)]=content
                        new_key = key+str(counter)
                        newJson[key+str(counter)]=content
                newJson["Author"]=authorName
                newJson["Doc_Title"]=title
                jsonStrLst.append(str(jsonObj))

               # print("Matched");
               # print(match.group(1));

            metadata=dict();
            metadata['metadata']=newJson

            print(json.dumps(metadata))
            writeFileToJson(json.dumps(metadata),file)
            #print("Match count is " + str(counter));
        i+=1




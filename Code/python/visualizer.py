import os
import json
import solr
import jsonpickle
import simplejson
import string
import random

#text: size:


class children:
    name =""
    size=0;
    def __init__(self):
        self.name=""
        self.size=0

class sweetWord:
    name=""
    children=[]
    total_occurences=0;

    def __init__(self,name):
        self.name = name
        self.children = [None] * 5
        self.total_occurences=0;
        self.children[0]=children();
        self.children[0].name="#1"
        self.children[1]=children();
        self.children[1].name="#2"
        self.children[2]=children();
        self.children[2].name="#3"
        self.children[3]=children();
        self.children[3].name="#4"
        self.children[4]=children();
        self.children[4].name="#5"

class Author:
    name=""
    children=[]
    def __init__(self,title):
        self.name = title
        self.children=[]
    def add_related_publication(self, publication_name):

        child = children();
        if child not in self.children:
            child.name = str(publication_name)
            child.size = 1
            self.children.append(child)
class Author_Pub:
    name = ""
    size=0;
    def __init__(self,name):
        self.name= name
        self.size=0



def add_wcloud_occurences(doc):
    for i in range(1,6):
        key = "sweetName" + str(i)
        #print(str(key))
        if key in doc:
            word = doc[key][0];
            if word in d:
                d[word]=d[word]+1
            else:
                d[word]=0;

def convertToJson(lst):
    concat=""
    jsons=[];
    for item in lst:
        json = "{" + '"text":"' +str(item[0])+ '" , "size":'+str(item[1])+ "}"
        jsons.append(json)
    joined = ",".join(jsons)
    joined="["+joined+"]"
    return joined


def convertToSVN(lst):
    concat=""
    jsons=[];
    print("")
    for item in lst:
        json = "{" + '"text":"' +str(item[0])+ '" , "size":'+str(item[1])+ "}"
        jsons.append(json)
    joined = ",".join(jsons)
    joined="["+joined+"]"
    print(joined)


def add_years(doc):
      for i in range(1,21):
            dateKey = "Year"+str(i)
            if dateKey in doc:
                year = str(doc[dateKey][0])
                if year in year_dict:
                    year_dict[year] = year_dict[year]+1
                else:
                    year_dict[year] = 1;





def extract_places(doc):
    for i in range(1,4):
         key1="loc" + str(i)
         key2="lat" + str(i)
         key3="long" + str(i)
         if key1 in doc:
             place_name = doc[key1][0]
             if place_name not in places:
                 places.append(doc[key1][0])
                 lats.append(doc[key2][0])
                 longs.append(doc[key3][0])

def createGeoSvn():
    file=open("geo.csv",'w')
    file.write("lat,long,loc"+"\n");
    for i in range(0,len(places)):
            processed_place = places[i].replace(" ","_")
            file.write(str(lats[i]) + "," + str(longs[i]) + ","+ processed_place+"\n")

    file.close()


def incrementOccurence(occurenceNum,word):
    h_bar_chart_dic[word].children[occurenceNum-1].size=h_bar_chart_dic[word].children[occurenceNum-1].size+1



def add_related_for_title_dendogram(doc):
  key = "Author"
  if   key in doc:
    author = str(doc[key])

    if author in dendogram_dic:
        authorObj = dendogram_dic[author]
    else:
        authorObj = Author(author)
        dendogram_dic[author] = authorObj


    for i in range(1,21):
            relatedTitleKey = "Title"+str(i)
            if relatedTitleKey in doc:
                new_publication = doc[relatedTitleKey]
                authorObj.add_related_publication(new_publication)



class ContentTypeItem:
    type=""
    value=-1
    def __init__(self,name):
        self.type=name
        self.value=1




def add_content_type(doc):
    key = "Content-Type"
    if key in doc:
        contentType = doc[key][0]
        if contentType in content_type_dic:
            content_type_dic[contentType].value= content_type_dic[contentType].value+1
        else:
            content_type_dic[contentType] = ContentTypeItem(contentType)







def add_occurences_for_hbarChart(doc):
    for i in range(1,6):
        key = "sweetName" + str(i)
        #print(str(key))
        if key in doc:
            word = doc[key][0];
            if word in h_bar_chart_dic:
                h_bar_chart_dic[word].total_occurences= h_bar_chart_dic[word].total_occurences+1

            else:
                h_bar_chart_dic[word]=sweetWord(word)

            incrementOccurence(i,word);

def add_occurences_author_and_pub_count(doc):
    key = "Author"
    if key in doc:
        author = doc[key][0]
        if author in author_pubs_dict:
            authorObj = author_pubs_dict[author]
        else:
            authorObj = Author_Pub(author)
            author_pubs_dict[author]=authorObj
        for i in range(0,21):
            titleKey = "Title"+str(i)
            if titleKey in doc:
                authorObj.size= authorObj.size+1;



def create_pie_chart_info():
    for key,value in content_type_dic.items():
        content_type_lst.append(content_type_dic[key])
    contentTypeJson = jsonpickle.encode(content_type_lst,unpicklable=False)
    contentTypeJson="{" +'"types":'+ contentTypeJson +"}"
    writeFile("filetypes.json",contentTypeJson)

def create_word_cloud_info():
    lst=[]
    for key, value in d.items():
        pair = [key,value]
        if pair[1]>50:
            #Also they look way huge we can scale it
            pair[1]=int(pair[1]/10)
            lst.append(pair)
    lst.sort(key=lambda x: x[1],reverse=True)
    lstJson = convertToJson(lst);
    writeFile("wordcloud.json",lstJson)


def create_bar_chart_info():
    sumOfPublications=0;
    for key,value in year_dict.items():
        sumOfPublications+=year_dict[key]
        pair =[key,value]
        years_lst.append(pair)
    years_lst.sort(key=lambda x: x[0],reverse=True)
    tsv= "letter"+"\t"+"count"+"\n"
    for i in range (0,len(years_lst)):
            pair = years_lst[i]
            #new_ratio = ((pair[1] * 100.0)/sumOfPublications)/100
            #years_lst[i][1]=new_ratio
            tsv+= str(years_lst[i][0]) + "\t" +str(years_lst[i][1])+"\n"

    writeFile("bar_chart.tsv",tsv)


def create_bubble_chart_info():
    author_pubs_lst=[]
    for key,value in author_pubs_dict.items():
        author_pubs_lst.append(author_pubs_dict[key])
    authors_pub_JSON= jsonpickle.encode(author_pubs_lst,unpicklable=False)
    authors_pub_JSON=authors_pub_JSON[1:]
    authors_pub_JSON=authors_pub_JSON[:-1]
    authors_pub_JSON = '{ "name" : "flare", "children":[' + authors_pub_JSON + " ]}"

    writeFile("bubble.json",authors_pub_JSON)


def create_heirearchical_bar_chart_info():
    sweet_lst=[]
    for key,value in h_bar_chart_dic.items():
        if(h_bar_chart_dic[key].total_occurences>0):
            sweet_lst.append(h_bar_chart_dic[key])
    sweet_lst = sorted(sweet_lst, key=lambda x: x.total_occurences, reverse=True)
    sweet_lst_small =[]
    added=0
    for item in sweet_lst:
        if added<50:
            sweet_lst_small.append(item)
            added+=1
    sweet_json = jsonpickle.encode(sweet_lst_small,unpicklable=False)
    sweet_json = sweet_json[1:]
    sweet_json = sweet_json[:-1]
    sweet_json = '{ "name" : "flare", "children":[' + sweet_json + " ]}"

    writeFile("heirarchical_bar.json",sweet_json)





def create_tree_chart_info():
    pubs_lst=[]
    for key,value in dendogram_dic.items():
        pubs_lst.append(dendogram_dic[key])
    pubs_json = jsonpickle.encode(pubs_lst,unpicklable=False)
    pubs_json= pubs_json[1:]
    pubs_json=pubs_json[:-1]
    pubs_json= '{ "name" : "flare", "children":[' + pubs_json + " ]}"

    writeFile("authors.json",pubs_json)


def writeFile(filename,data):
    text_file = open(filename, "w")
    text_file.write(data)
    text_file.close()


year_dict = dict();

h_bar_chart_dic=dict();
content_type_dic = dict();
dendogram_dic=dict();
author_pubs_dict=dict();
authors_lst=[]
years_lst = []
content_type_lst=[]
places=[]
lats=[]
longs=[]
d = dict();
yearCounter=dict();
connection = solr.Solr("http://localhost:8983/solr/polrsolr");
select = solr.SearchHandler(connection,"/select");


response =select.__call__ (q="*",start=0, rows=19000)
pdfResponse =select.__call__ (q='Content-Type:"application/pdf"',start=0, rows=50)






for doc in pdfResponse:
    add_related_for_title_dendogram(doc)
    add_years(doc)
    add_occurences_author_and_pub_count(doc)

for doc in response:
    add_wcloud_occurences(doc)
    add_occurences_for_hbarChart(doc)
    extract_places(doc)
    add_content_type(doc)











create_tree_chart_info()
create_bar_chart_info()
create_heirearchical_bar_chart_info()
create_bubble_chart_info()
create_word_cloud_info()
create_pie_chart_info()
createGeoSvn()
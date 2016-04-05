import solr
from subprocess import call
import os
import shutil

newFolder = "Files"

def writeFile(filename,data):
    text_file = open(filename, "w")
    text_file.write(data)
    text_file.close()


def generateEditValue():
    currentPath = os.getcwd()
    pathToSimilarityFolder = os.path.join(currentPath,"similarity");
    pathToGeneratorFile = os.path.join(currentPath,"similarity","edit-value-similarity.py ")
    pathToFiles = os.path.join(currentPath,"Files")
    pathToCSV=os.path.join(pathToSimilarityFolder,"k.csv")
    full_command = "python " + pathToGeneratorFile + " --inputDir " + pathToFiles+ " --outCSV " + pathToCSV
    os.system(full_command);

def generateCosineSimilarity():
    currentPath = os.getcwd()
    pathToSimilarityFolder = os.path.join(currentPath,"similarity");
    pathToGeneratorFile = os.path.join(currentPath,"similarity","cosine_similarity.py ")
    pathToFiles = os.path.join(currentPath,"Files")
    pathToCSV=os.path.join(pathToSimilarityFolder,"k.csv")
    full_command = "python " + pathToGeneratorFile + " --inputDir " + pathToFiles+ " --outCSV " + pathToCSV
    os.system(full_command);


def runClusterJaccard():

    currentPath = os.getcwd()
    pathToSimilarityFolder = os.path.join(currentPath,"similarity");
    os.chdir(pathToSimilarityFolder)
    full_command = "python cluster-scores.py"
    print(full_command)
    os.system(full_command)
    os.chdir(currentPath)

    foldername = "cluster_jaccard"
    htmlFileName="cluster-d3.html"
    jsonFileName = "clusters.json"
    PathToHTML = os.path.join(pathToSimilarityFolder,htmlFileName)
    PathToJSON = os.path.join(pathToSimilarityFolder,jsonFileName)
    visPath = os.path.join(currentPath,"vis",foldername)
    TargetHTML = os.path.join(visPath,htmlFileName)
    TargetJSON = os.path.join(visPath,jsonFileName)
    shutil.copy(PathToHTML,TargetHTML)
    shutil.copy(PathToJSON,TargetJSON)


def runCluster(foldername):
    currentPath = os.getcwd()
    pathToSimilarityFolder = os.path.join(currentPath,"similarity");
    os.chdir(pathToSimilarityFolder)
    full_command = "python edit-cosine-cluster.py k.csv"
    os.system(full_command)
    os.chdir(currentPath)

    htmlFileName="cluster-d3.html"
    jsonFileName = "clusters.json"
    PathToHTML = os.path.join(pathToSimilarityFolder,htmlFileName)
    PathToJSON = os.path.join(pathToSimilarityFolder,jsonFileName)
    visPath = os.path.join(currentPath,"vis",foldername)
    TargetHTML = os.path.join(visPath,htmlFileName)
    TargetJSON = os.path.join(visPath,jsonFileName)
    shutil.copy(PathToHTML,TargetHTML)
    shutil.copy(PathToJSON,TargetJSON)


def runCirclePacking(foldername):

    currentPath = os.getcwd()
    pathToSimilarityFolder = os.path.join(currentPath,"similarity");
    os.chdir(pathToSimilarityFolder)
    full_command = "python edit-cosine-circle-packing.py k.csv"
    os.system(full_command)
    os.chdir(currentPath)

    htmlFileName="circlepacking.html"
    jsonFileName = "circle.json"
    PathToHTML = os.path.join(pathToSimilarityFolder,htmlFileName)
    PathToJSON = os.path.join(pathToSimilarityFolder,jsonFileName)
    visPath = os.path.join(currentPath,"vis",foldername)
    TargetHTML = os.path.join(visPath,htmlFileName)
    TargetJSON = os.path.join(visPath,jsonFileName)
    shutil.copy(PathToHTML,TargetHTML)
    shutil.copy(PathToJSON,TargetJSON)

def runJaccardCirclePacking():
    currentPath = os.getcwd()
    pathToSimilarityFolder = os.path.join(currentPath,"similarity");
    os.chdir(pathToSimilarityFolder)
    full_command = "python circle-packing.py"
    os.system(full_command)
    os.chdir(currentPath)

    foldername="circle_jaccard"
    htmlFileName="circlepacking.html"
    jsonFileName = "circle.json"
    PathToHTML = os.path.join(pathToSimilarityFolder,htmlFileName)
    PathToJSON = os.path.join(pathToSimilarityFolder,jsonFileName)
    visPath = os.path.join(currentPath,"vis",foldername)
    TargetHTML = os.path.join(visPath,htmlFileName)
    TargetJSON = os.path.join(visPath,jsonFileName)
    shutil.copy(PathToHTML,TargetHTML)
    shutil.copy(PathToJSON,TargetJSON)

def runComposite():

    currentPath = os.getcwd()
    pathToSimilarityFolder = os.path.join(currentPath,"similarity");
    foldername="composite"
    htmlFileName="compositeViz.html"
    jsonFileName = "circle.json"
    PathToHTML = os.path.join(pathToSimilarityFolder,htmlFileName)
    PathToJSON = os.path.join(pathToSimilarityFolder,jsonFileName)
    visPath = os.path.join(currentPath,"vis",foldername)
    TargetHTML = os.path.join(visPath,htmlFileName)
    TargetJSON = os.path.join(visPath,jsonFileName)
    shutil.copy(PathToHTML,TargetHTML)
    shutil.copy(PathToJSON,TargetJSON)




def runLevelCluster():
    currentPath = os.getcwd()
    pathToSimilarityFolder = os.path.join(currentPath,"similarity");
    os.chdir(pathToSimilarityFolder)
    full_command = "python generateLevelCluster.py"
    print(full_command)
    os.system(full_command)
    os.chdir(currentPath)

    foldername="cluster_level"
    htmlFileName="levelCluster-d3.html"
    jsonFileName = "circle.json"
    PathToHTML = os.path.join(pathToSimilarityFolder,htmlFileName)
    PathToJSON = os.path.join(pathToSimilarityFolder,jsonFileName)
    visPath = os.path.join(currentPath,"vis",foldername)
    TargetHTML = os.path.join(visPath,htmlFileName)
    TargetJSON = os.path.join(visPath,jsonFileName)
    shutil.copy(PathToHTML,TargetHTML)
    shutil.copy(PathToJSON,TargetJSON)


def runCommands():
    currentPath = os.getcwd()
    pathToSimilarityFolder = os.path.join(currentPath,"similarity");
    os.chdir(pathToSimilarityFolder)
    pathToGeneratorFile = "similarity.py"
    pathToFiles = os.path.join(currentPath,"Files")
    pathToCSV=os.path.join(pathToSimilarityFolder,"k.csv")
    full_command = "python " + pathToGeneratorFile + " -f " + pathToFiles
    print(full_command)
    os.system(full_command);
    os.chdir(currentPath)




    currentPath = os.getcwd()
    pathToSimilarityFolder = os.path.join(currentPath,"similarity");
    os.chdir(pathToSimilarityFolder)
    pathToGeneratorFile = "value-similarity.py"
    pathToFiles = os.path.join(currentPath,"Files")
    pathToCSV=os.path.join(pathToSimilarityFolder,"k.csv")
    full_command = "python " + pathToGeneratorFile + " -f " + pathToFiles
    print(full_command)
    os.system(full_command);
    os.chdir(currentPath)
    #python similarity.py -f [directory of files]


def runSimilarity():
    runCommands()
    generateEditValue();
    runClusterJaccard()
    runComposite()
    runLevelCluster()
    runCirclePacking("circle_edit_distance");
    runCluster("cluster_edit_distance")
    runJaccardCirclePacking()


    generateCosineSimilarity()
    runCirclePacking("circle_cosine_distance")
    runCluster("cluster_cosine_distance")

def getFilesWithkey(response,key):
    desiredNum=60;
    currentNum=0
    for doc in response:
        if key in doc and currentNum<desiredNum and "content" in doc:
            currentNum+=1
            fileUrl = doc['file_url'][0]
            filename=fileUrl.rsplit('/', 1)[-1]
            print("Processing " + filename)

            writeFile(os.path.join(newFolder,filename),str(doc['content'][0]))
    runSimilarity()

def recreateDirectory():
    if not os.path.exists("Files"):
        os.makedirs("Files")
    if not os.path.exists("vis"):
        os.makedirs("vis")

    currentPath=os.getcwd();

    os.chdir(currentPath)
    shutil.rmtree('vis')
    #shutil.rmtree("Files")
    if os.path.isfile(os.path.join(currentPath,"similarity","k.csv")):
        os.remove(os.path.join(currentPath,"similarity","k.csv"))
    if os.path.isfile(os.path.join(currentPath,"similarity","similarity-scores.txt")):
        #x=59
        os.remove(os.path.join(currentPath,"similarity","similarity-scores.txt"))
    os.makedirs("Files")
    os.makedirs("vis")
    os.makedirs(os.path.join("vis","circle_cosine_distance"))
    os.makedirs(os.path.join("vis","circle_edit_distance"))
    os.makedirs(os.path.join("vis","cluster_edit_distance"))
    os.makedirs(os.path.join("vis","cluster_cosine_distance"))
    os.makedirs(os.path.join("vis","cluster_jaccard"))
    os.makedirs(os.path.join("vis","circle_jaccard"))
    os.makedirs(os.path.join("vis","cluster_level"))
    os.makedirs(os.path.join("vis","composite"))




#connection = solr.Solr("http://localhost:8983/solr/polrsolr");
#select = solr.SearchHandler(connection,"/select");


#response =select.__call__ (q="*",start=0, rows=500)
#print("query returned")



#if not os.path.exists(newFolder):
#    os.makedirs(newFolder)

recreateDirectory()
#runCommands()
#getFilesWithkey(response,"loc1")
runSimilarity()












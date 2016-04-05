
import json
import solr
import os
import operator


def compute_similarity():
    union_feature_names = set()
    file_parsed_data = {}
    resemblance_scores = {}
    file_metadata = {}
    solr_handle = solr.Solr("http://localhost:8984/solr/polrsolr")
    select = solr.SearchHandler(solr_handle, "/select")
    row_count = 1000
    union_feature_names = set()
    response = select.__call__(q="*", rows = row_count)
    for file_data in response.results:
        file_parsed = []
        try:
            file_id = file_data['id']
            file_metadata[file_id] = file_data
            for meta_key in file_data:
                value = str(file_data[meta_key])
                if isinstance(value, list):
                    value = ", ".join(file_data[meta_key])
                file_parsed.append(str(meta_key.strip(' ').encode('utf-8') + ": " + value.strip(' ').encode('utf-8')))
            file_parsed_data[file_id] = set(file_parsed)
            union_feature_names = union_feature_names | set(file_parsed_data[file_id])
        except KeyError:
            continue
    features_count = len(union_feature_names)

    for file_id in file_parsed_data:
        overlap = {}
        overlap = file_parsed_data[file_id] & set(union_feature_names)
        resemblance_scores[file_id] = float(len(overlap))/features_count

    sorted_resemblance_scores = sorted(resemblance_scores.items(), key=operator.itemgetter(1), reverse=True)

    with open("similarity-scores.txt", "w") as f:
        f.write("Resemblance : \n")
        print str(sorted_resemblance_scores)
        for tuple in sorted_resemblance_scores:
            print tuple
            f.write(tuple[0]+","+str(tuple[1]) + "," + tuple[0] + "," + convertUnicode(file_metadata[tuple[0]])+'\n')


def convertUnicode(fileDict):
    fileUTFDict = {}
    for key in fileDict:
        if isinstance(key, unicode) :
            key = key.encode('utf-8').strip()
            value = fileDict.get(key)
        if isinstance(value, unicode) :
            value = value.encode('utf-8').strip()
        fileUTFDict[key] = value
    return str(fileUTFDict)





def main():
    compute_similarity()


if __name__ == '__main__':
    main()
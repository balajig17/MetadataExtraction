
import itertools
import solr
import editdistance
import csv

def stringify(attribute_value):
    if isinstance(attribute_value, list):
        return str((", ".join(attribute_value)).encode('utf-8').strip())
    else:
        return str(attribute_value.encode('utf-8').strip())


def calculate_edit(allKeys):

    with open("csvOutput", "wb") as outF:
        a = csv.writer(outF, delimiter=',')
        a.writerow(["x-coordinate","y-coordinate","Similarity_score"])
        na_metadata = []
        solr_handle = solr.Solr("http://localhost:8984/solr/polrsolr")
        select = solr.SearchHandler(solr_handle, "/select")
        row_count = 1000
        response = select.__call__(q="*", rows = row_count)
        file_metadata_list = {}
        files_list = []
        for file_data in response.results:
            file_metadata_list[file_data['id']] = file_data
            files_list.append(file_data['id'])

        files_tuple = itertools.combinations(files_list,2)
        for file_1, file_2 in files_tuple:
            try:
                row_edit_distance = [file_1, file_2]
                file1_data = file_metadata_list[file_1]
                file2_data = file_metadata_list[file_2]

                intersect_features = set(file1_data.keys()) & set(file2_data.keys())

                intersect_features = [feature for feature in intersect_features if feature not in na_metadata ]

                file_edit_distance = 0.0
                for feature in intersect_features:

                    file1_feature_value = stringify(str(file1_data[feature]))
                    file2_feature_value = stringify(str(file2_data[feature]))

                    if len(file1_feature_value) == 0 and len(file2_feature_value) == 0:
                        feature_distance = 0.0
                    else:
                        feature_distance = float(editdistance.eval(file1_feature_value, file2_feature_value))/(len(file1_feature_value) if len(file1_feature_value) > len(file2_feature_value) else len(file2_feature_value))

                    file_edit_distance += feature_distance

                if allKeys:
                    file1_only_features = set(file1_data.keys()) - set(intersect_features)
                    file1_only_features = [feature for feature in file1_only_features if feature not in na_metadata]

                    file2_only_features = set(file2_data.keys()) - set(intersect_features)
                    file2_only_features = [feature for feature in file2_only_features if feature not in na_metadata]

                    file_edit_distance += len(file1_only_features) + len(file2_only_features)       # increment by 1 for each disjunct feature in (A-B) & (B-A), file1_disjunct_feature_value/file1_disjunct_feature_value = 1
                    file_edit_distance /= float(len(intersect_features) + len(file1_only_features) + len(file2_only_features))

                else:
                    file_edit_distance /= float(len(intersect_features))    #average edit distance

                row_edit_distance.append(1-file_edit_distance)
                a.writerow(row_edit_distance)

            except KeyError:
                continue


def main():
    calculate_edit(allKeys=True)


if __name__ == '__main__':
    main()
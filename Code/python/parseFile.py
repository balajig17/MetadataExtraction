import tika
from tika import parser
import json
import os
from doi import lstu

tika_url = "http://localhost:9001"
host_url = "http://localhost:9010"
local_path = "/Users/Balaji/Desktop/CSCI-599/HW2/Data"
dest_path = "/Users/Balaji/Desktop/CSCI-599/HW2/JSONData"


def parse_all_files(path):
    for subdir, dirs, files in os.walk(path):
        folders = subdir.split(local_path)
        print "Parsing all files in " + folders[1] + " directory."
        for file_name in files:
            if file_name.startswith(".DS_Store"):
                continue
            try:
                file_data = parser.from_file(os.path.join(subdir, file_name), tika_url)
                file_url = host_url+str(os.path.join(folders[1], file_name))
                short_url = lstu.shorten_url(file_url)
                print short_url
                file_data['metadata']['id'] = short_url
                file_data['metadata']['file_url'] = file_url
                file_handle = open(os.path.join(dest_path, file_name), "w")
                file_handle.write(json.dumps(file_data, indent=4, sort_keys=True))
                file_handle.close()
            except:
                pass
    print "Finished parsing files in directory."


def main():
    pass
    parse_all_files(local_path)


if __name__ == "__main__":
    main()

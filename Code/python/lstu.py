import requests
import json
import sys

url = "http://polar.usc.edu"


def shorten_url(file_url):
    params = {"lsturl": file_url, "format": "json"}
    response = requests.post(url+"/a", data=params)
    response_json = json.loads(response.content)
    return response_json['short']


def main():
    file_url = sys.argv[1]
    shorten_url(file_url)


if __name__ == "__main__":
    main()

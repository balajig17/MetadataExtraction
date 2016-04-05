from __future__ import division

import os

from html5lib import HTMLParser, treewalkers, treebuilders


# Parse the data using the Tag Ratio algorithm and return the relevant data
def parse(f):
    p = HTMLParser(tree=treebuilders.getTreeBuilder("dom"))
    doc = p.parse(f)
    walker = treewalkers.getTreeWalker("dom")

    tokens = []
    bintokens = []

    waitfor = None

    for tok in walker(doc):

        if waitfor:
            if tok["type"] == waitfor[0] and tok["name"] == waitfor[1]:
                waitfor = None
            continue

        if tok["type"] == "StartTag" and tok["name"] in ("link", "script", "style"):
            waitfor = ("EndTag", tok["name"])

        if tok["type"] in ("EndTag", "StartTag", "EmptyTag", "Comment"):
            bintokens.append(1)
            tokens.append(tok)

        elif tok["type"] in ("Characters",):
            for tok1 in tok["data"].split():
                bintokens.append(0)
                tokens.append({"type": "Characters", "data": tok1})

        elif tok["type"] in ("SpaceCharacters", "Doctype"):
            pass

        else:
            raise ValueError("unrecognizable token type: %r" % tok)

    cumbintokens = [bintokens[0]]

    for tok in bintokens[1:]:
        cumbintokens.append(cumbintokens[-1] + tok)

    length = len(cumbintokens)

    midx = None
    m = None

    for i in range(length):
        for j in range(i + 1, length):
            end_tag = cumbintokens[-1] - cumbintokens[j]
            start_tag = cumbintokens[i]
            text_between = (j - i) - (cumbintokens[j] - cumbintokens[i])
            nm = end_tag + start_tag + text_between

            if not midx or nm > m:
                midx = i, j
                m = nm

    i, j = midx
    return serialize_tokens(tokens[i:j + 1])


def serialize_tokens(tokens):
    return " ".join(x.get("data") for x in tokens if x["type"] == "Characters")


def main():
    mime_types = ['text/html', 'application_rss+xml', 'application_xhtml+xml', 'application_xml']

    # Read the data from the following path
    data_files = '/Users/Balaji/Desktop/CSCI-599/HW2/SortedData/text/html'

    for path, dirs, files in os.walk(data_files):
        dirs.sort()
        path_spl = path.split('/')
        content_type = path_spl[len(path_spl) - 1].replace('_', '/')
        for f in sorted(files):
            if f not in '.DS_Store':
                if content_type in mime_types:
                    f1 = open(path + '/' + f)
                    # Get the relevant data after applying the tag ration algorithm
                    relevant_data = parse(f1)
                    updated_file = open(path + '/' + f, 'w+')
                    # Write the relevant data with the same name
                    updated_file.write(relevant_data.encode('ascii', 'ignore'))


if __name__ == '__main__':
    main()

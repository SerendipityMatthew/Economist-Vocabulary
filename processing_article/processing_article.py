with open("article", "r") as file:
    article_file = file.readlines()

with open("OALECD_History_20190811.txt", "r") as file:
    vocabulary_dict = file.readlines()


def eliminate_duplicate_word(dict):
    eliminated = []
    for word in dict:
        w = word.replace("\n", "")
        if w is not None and w not in eliminated:
            eliminated.append(w)

    return eliminated


def process_article_get_para(article):
    processed = []
    for n in article:
        processed.append(n.replace("\n", ""))
    return processed


def process_para_get_lines(para):
    lines = []
    li = para.split(". ")
    for l in li:
        if not l.endswith("."):
            l1 = l.__add__(".")
            lines.append(l1)
    return lines


def main():
    para_list = process_article_get_para(article_file)
    line_list = []
    for x in para_list:
        d = process_para_get_lines(x)
        for line in d:
            line_list.append(line)

    for l in line_list:
        pass
        # print(l)

    eliminated = eliminate_duplicate_word(vocabulary_dict)
    print(eliminated)


if __name__ == "__main__":
    main()

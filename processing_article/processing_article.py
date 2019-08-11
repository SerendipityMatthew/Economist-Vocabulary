with open("article", "r") as file:
    article_file = file.readlines()

with open("OALECD_History_20190811.txt", "r") as file:
    vocabulary_dict = file.readlines()


# 消除重复的单词
def eliminate_duplicate_word(dictionary):
    eliminated = []
    for word in dictionary:
        w = word.replace("\n", "")
        if w is not None and w not in eliminated:
            eliminated.append(w)

    return eliminated


# 处理每一篇文章, 获取每一篇文章的段落
def process_article_get_para(article):
    processed = []
    for n in article:
        processed.append(n.replace("\n", ""))
    return processed


# 处理每一个段落, 获得段落里的每一个句子
def process_para_get_lines(para):
    lines = []
    li = para.split(". ")
    for l in li:
        if not l.endswith("."):
            l1 = l.__add__(".")
            l = l1
        lines.append(l)
    return lines


# word -----> [sentence01, sentence02, sentence03]
# [sentence]   ------> [word01, word02, word03]
# 匹配生词和句子
def match_sentence_and_vocabulary(sentence, vocabulary_list):
    # sentence_list = str(sentence).split(" ")
    # for word in sentence_list:
    #     w = word
    #     if "," in word:
    #         w = word.replace(",", "")
    #     if "." in word:
    #         w = word.replace(".", "")
    #     if ":" in word:
    #         w = word.replace(":", "")
    #
    #     if w in vocabulary_list:
    #         return [w, {sentence}]

    splatted_sentence = sentence.replace(":", "").replace(",", "").replace("?", "").replace("!", "").split(" ")

    for vocabulary in vocabulary_list:
        for word in splatted_sentence:
            if word.lower().__eq__(vocabulary.lower()):
                return [word, sentence]

    return None


def main():
    para_list = process_article_get_para(article_file)
    line_list = []
    for para in para_list:
        d = process_para_get_lines(para)
        for line in d:
            line_list.append(line)

    eliminated = eliminate_duplicate_word(vocabulary_dict)

    word_sentence = []
    for line in line_list:
        word_sentence_map = match_sentence_and_vocabulary(line, eliminated)
        if word_sentence_map is not None:
            word_sentence.append(word_sentence_map)

    for l in word_sentence:
        print(l)


if __name__ == "__main__":
    main()

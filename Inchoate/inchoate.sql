
CREATE TABLE issue (id INTEGER PRIMARY KEY AUTOINCREMENT, cover_image_url TEXT, is_downloaded BOOLEAN, issue_url TEXT);
CREATE TABLE article (id INTEGER AUTOINCREMENT, issue_date TEXT, section TEXT, 
    audio_url TEXT, locale_audio_url TEXT, title TEXT, fly_title TEXT, main_article_image TEXT, 
    article_image TEXT, article_rubric TEXT, 
    audio_duration TEXT, is_bookmark BOOLEAN, issue_id INTEGER);
CREATE TABLE paragraph ( id INTEGER AUTOINCREMENT, issue_date TEXT, section TEXT, title TEXT, paragraph_content TEXT, aritcle_id INTEGER);

// IEconomistService.aidl
package com.xuwanjin.inchoate.timber_style;

// Declare any non-default types here with import statements
import com.xuwanjin.inchoate.model.Article;
import com.xuwanjin.inchoate.model.Issue;

interface IEconomistService {
    void openFile(String filePath);
    void stop();
    void pause();
    void play();
    void playTheRest(in Article article, in Issue issue);
    void next();
    boolean isPlaying();
    void seekToPosition(int position);
    int getCurrentPosition();
    int getDuration();
    void releasePlayer();
}

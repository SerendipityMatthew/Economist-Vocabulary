package com.xuwanjin.inchoate.player;


import androidx.annotation.Nullable;

import com.xuwanjin.inchoate.model.Article;

import java.util.List;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/5/16
 * Time: 6:02 PM
 * Desc: IPlayer
 */
public interface IPlayer {
    void setPlayList(List<Article> articleList);

    boolean play();

    boolean play(Article article, boolean isPlayWholeIssue);

    boolean play(List<Article> articleList, int startIndex);

    boolean playNext();

    boolean playLast();

    boolean pause();

    boolean isPlaying();

    int getProgress();

    Article getPlayingSong();

    boolean seekTo(int progress);


    void registerCallback(Callback callback);

    void unregisterCallback(Callback callback);

    void removeCallbacks();

    void releasePlayer();

    interface Callback {

        void onSwitchLast(@Nullable Article last);

        void onSwitchNext(@Nullable Article next);

        void onComplete(@Nullable Article next);

        void onPlayStatusChanged(boolean isPlaying);
    }
}

package me.xuwanjin.economistvocabulary.model;

import java.util.ArrayList;

public class Word {
    public ArrayList<String> belongedSentence;
    public ArrayList<Word> synonymsList;
    public ArrayList<Word> antonymsList;

    public ArrayList<String> getBelongedSentence() {
        return belongedSentence;
    }

    public void setBelongedSentence(ArrayList<String> belongedSentence) {
        this.belongedSentence = belongedSentence;
    }

    public ArrayList<Word> getSynonymsList() {
        return synonymsList;
    }

    public void setSynonymsList(ArrayList<Word> synonymsList) {
        this.synonymsList = synonymsList;
    }

    public ArrayList<Word> getAntonymsList() {
        return antonymsList;
    }

    public void setAntonymsList(ArrayList<Word> antonymsList) {
        this.antonymsList = antonymsList;
    }

    @Override
    public String toString() {
        return "Word{" +
                "belongedSentence=" + belongedSentence +
                ", synonymsList=" + synonymsList +
                ", antonymsList=" + antonymsList +
                '}';
    }
}

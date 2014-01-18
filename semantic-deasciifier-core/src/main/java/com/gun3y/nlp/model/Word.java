package com.gun3y.nlp.model;

import org.apache.commons.lang3.StringUtils;

public class Word {
    private String stemmedWord;

    private String word;

    private boolean deasciified;

    private boolean marked;

    public Word() {
	this.word = StringUtils.EMPTY;
	this.stemmedWord = StringUtils.EMPTY;
	this.deasciified = false;
    }

    public Word(String word) {
	this();
	this.word = word;
	this.stemmedWord = word;
    }

    public Word(String word, String stemmedWord) {
	this(word);
	this.stemmedWord = stemmedWord;
    }

    public Word(String word, String stemmedWord, boolean deasciified) {
	this(word, stemmedWord);
	this.deasciified = deasciified;
    }

    public String getStemmedWord() {
	return stemmedWord;
    }

    public void setStemmedWord(String stemmedWord) {
	this.stemmedWord = stemmedWord;
    }

    public String getWord() {
	return word;
    }

    public void setWord(String word) {
	this.word = word;
    }

    @Override
    public String toString() {
	return this.word + "[" + this.stemmedWord + ":" + this.deasciified + "]";
    }

    public boolean isDeasciified() {
	return deasciified;
    }

    public void setDeasciified(boolean deasciified) {
	this.deasciified = deasciified;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (deasciified ? 1231 : 1237);
	result = prime * result + ((stemmedWord == null) ? 0 : stemmedWord.hashCode());
	result = prime * result + ((word == null) ? 0 : word.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this.stemmedWord != null && obj != null && obj instanceof Word) {
	    return this.stemmedWord.equals(((Word) obj).stemmedWord);
	}
	return false;
    }

    public boolean isMarked() {
	return marked;
    }

    public void setMarked(boolean marked) {
	this.marked = marked;
    }

}

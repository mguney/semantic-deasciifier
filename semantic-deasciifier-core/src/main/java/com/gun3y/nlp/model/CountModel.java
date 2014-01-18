package com.gun3y.nlp.model;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("CountModel")
public class CountModel {

    @Id
    private String id;

    private int count;

    private int ngram;

    public CountModel() {

    }

    public CountModel(String id, int count, int ngram) {
	super();
	this.id = id;
	this.count = count;
	this.ngram = ngram;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public int getCount() {
	return count;
    }

    public void setCount(int count) {
	this.count = count;
    }

    public int getNgram() {
	return ngram;
    }

    public void setNgram(int ngram) {
	this.ngram = ngram;
    }

}

package com.gun3y.nlp.model;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("model")
public class Model {

    @Id
    private String id;

    private float probability;

    public Model() {
	super();
    }

    public Model(String id, float probability) {
	super();
	this.id = id;
	this.probability = probability;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public float getProbability() {
	return probability;
    }

    public void setProbability(float probability) {
	this.probability = probability;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append(getClass().getName()).append(" {\n\tid: ").append(id).append("\n\tprobability: ").append(probability).append("\n}");
	return builder.toString();
    }

}

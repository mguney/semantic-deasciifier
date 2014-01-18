package com.gun3y.nlp.model;

public class Pair<K, V> {

    private K key;

    private V value;

    public K getKey() {
	return this.key;
    }

    public V getValue() {
	return this.value;
    }

    public Pair(K key, V value) {
	super();
	this.key = key;
	this.value = value;
    }

}

package com.gun3y.nlp.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.gun3y.nlp.model.CountModel;
import com.gun3y.nlp.model.Word;
import com.gun3y.nlp.mongo.MongoManager;

public class NGramHelper {
    private static final Logger LOGGER = Logger.getLogger(NGramHelper.class);

    public static void main2(String[] args) {
	URL url = NGramHelper.class.getClassLoader().getResource("data.txt");
	File dataFile = new File(url.getPath());
	String data = FileHelper.readFile(dataFile);

	String[] d = data.split("\n");
	System.out.println(d.length);

	// List<String> extractedWords = FileHelper.extractWords();
	// LOGGER.info("Kelimeler getirildi.");
	//
	// List<Word> wordList = DeasciifierHelper.analyzeWords(extractedWords);
	// LOGGER.info("Kelimeler analiz edildi.");
	//
	// Map<String, Float> unigrams = NGramHelper.createUniGrams(wordList);
	// LOGGER.info("Unigram modeller oluşturuldu. Toplam:" +
	// unigrams.size());
	//
	// Map<String, Float> bigrams = NGramHelper.createBiGrams(wordList);
	// LOGGER.info("Bigram modeller oluşturuldu. Toplam:" + bigrams.size());
	//
	// Map<String, Float> trigrams = NGramHelper.createTriGrams(wordList);
	// LOGGER.info("Trigram modeller oluşturuldu. Toplam:" +
	// trigrams.size());

    }

    public static void main(String[] args) throws Exception {
	// MongoManager.getInstance().removeAllCounts();
	// URL url = NGramHelper.class.getClassLoader().getResource("data.txt");
	// File dataFile = new File(url.getPath());
	//
	// int buffer = 500000;
	// System.out.println("UNIGRAM");
	// createUniGrams(dataFile, buffer);
	// System.out.println("BIGRAM");
	// createBiGrams(dataFile, buffer);
	// System.out.println("TRIGRAM");
	// createTriGrams(dataFile, buffer);

    }

    private static List<String> readLines(BufferedReader bis, int count) throws IOException {
	List<String> retList = new ArrayList<String>();
	String s;
	for (int i = 0; i < count && (s = bis.readLine()) != null; i++) {
	    retList.add(s);
	}

	return retList;
    }

    public static String toNGgramString(List<Word> inputs) {
	if (inputs == null || inputs.isEmpty()) {
	    return StringUtils.EMPTY;
	}

	if (inputs.size() == 1) {
	    return inputs.get(0).getStemmedWord();
	}
	List<String> tempList = new ArrayList<String>();
	for (int i = 0; i < inputs.size(); i++) {
	    tempList.add(inputs.get(i).getStemmedWord());
	}

	Collections.sort(tempList);

	StringBuilder builder = new StringBuilder();
	for (String word : tempList) {
	    builder.append(word).append("$");
	}
	builder.deleteCharAt(builder.lastIndexOf("$"));
	return builder.toString();
    }

    public static void createUniGrams(File dataFile, int buffer) throws IOException {
	FileInputStream fis = new FileInputStream(dataFile);
	BufferedReader bis = new BufferedReader(new InputStreamReader(fis, "utf-8"));
	int count = 0;
	while (true) {
	    long start = System.currentTimeMillis();
	    List<Word> analyzeWords = DeasciifierHelper.analyzeWords(readLines(bis, buffer));
	    System.out.println("Kelimeleri getirme: " + (System.currentTimeMillis() - start));
	    if (analyzeWords == null || analyzeWords.isEmpty()) {
		break;
	    }
	    start = System.currentTimeMillis();
	    for (Word word : analyzeWords) {
		String stem = word.getStemmedWord();
		update(stem, 1);
		count++;
	    }
	    System.out.println("Kelimeleri yükleme: " + (System.currentTimeMillis() - start));
	}
	System.out.println(count);
	bis.close();
    }

    public static Map<String, Float> createUniGrams(List<Word> wordList) {
	Map<String, Float> unigrams = new HashMap<String, Float>();
	if (wordList == null || wordList.isEmpty()) {
	    return unigrams;
	}

	Map<String, Integer> uniGramsCountMap = new HashMap<String, Integer>();
	int count = 0;
	for (Word word : wordList) {
	    String stem = word.getStemmedWord();
	    updateMap(uniGramsCountMap, stem);
	    count++;
	}
	float total = (float) count;
	for (Entry<String, Integer> entry : uniGramsCountMap.entrySet()) {
	    unigrams.put(entry.getKey(), ((float) entry.getValue()) / total);
	}
	return unigrams;
    }

    private static void updateMap(Map<String, Integer> map, String val) {
	if (map.containsKey(val)) {
	    map.put(val, map.get(val) + 1);
	}
	else {
	    map.put(val, 1);
	}
    }

    private static void update(String val, int ngram) {
	if (!DeasciifierHelper.containsEnglishLetter(val)) {
	    return;
	}

	CountModel model = MongoManager.getInstance().getCountModelById(val);
	if (model == null) {
	    MongoManager.getInstance().insertModel(new CountModel(val, 1, ngram));
	}
	else {
	    MongoManager.getInstance().updateModel(model);
	}
    }

    public static void createBiGrams(File dataFile, int buffer) throws IOException {
	FileInputStream fis = new FileInputStream(dataFile);
	BufferedReader bis = new BufferedReader(new InputStreamReader(fis, "utf-8"));
	int count = 0;
	Word lastWord = null;
	while (true) {
	    long start = System.currentTimeMillis();
	    List<Word> analyzeWords = DeasciifierHelper.analyzeWords(readLines(bis, buffer));
	    System.out.println("Kelimeleri getirme: " + (System.currentTimeMillis() - start));
	    if (analyzeWords == null || analyzeWords.size() < 2) {
		break;
	    }
	    start = System.currentTimeMillis();

	    if (lastWord != null) {
		String ngram = toNGgramString(Arrays.asList(lastWord, analyzeWords.get(0)));
		update(ngram, 2);
		count++;
	    }
	    for (int i = 0; i < analyzeWords.size() - 1; i++) {
		String ngram = toNGgramString(Arrays.asList(analyzeWords.get(i), analyzeWords.get(i + 1)));
		update(ngram, 2);
		count++;
	    }
	    lastWord = analyzeWords.get(analyzeWords.size() - 1);

	    System.out.println("Kelimeleri yükleme: " + (System.currentTimeMillis() - start));
	}
	System.out.println(count);
	bis.close();
    }

    public static Map<String, Float> createBiGrams(List<Word> wordList) {
	Map<String, Float> bigrams = new HashMap<String, Float>();
	if (wordList == null || wordList.size() < 2) {
	    return bigrams;
	}

	Map<String, Integer> biGramsCountMap = new HashMap<String, Integer>();
	int count = 0;
	for (int i = 0; i < wordList.size() - 1; i++) {
	    String ngram = toNGgramString(Arrays.asList(wordList.get(i), wordList.get(i + 1)));
	    updateMap(biGramsCountMap, ngram);
	    count++;
	}

	float total = (float) count;
	// float minProb = 1 / total;
	for (Entry<String, Integer> entry : biGramsCountMap.entrySet()) {
	    bigrams.put(entry.getKey(), ((float) entry.getValue()) / total);
	}

	return bigrams;
    }

    public static void createTriGrams(File dataFile, int buffer) throws IOException {
	FileInputStream fis = new FileInputStream(dataFile);
	BufferedReader bis = new BufferedReader(new InputStreamReader(fis, "utf-8"));
	int count = 0;
	Word lastWord1 = null;
	Word lastWord2 = null;
	while (true) {
	    long start = System.currentTimeMillis();
	    List<Word> analyzeWords = DeasciifierHelper.analyzeWords(readLines(bis, buffer));
	    System.out.println("Kelimeleri getirme: " + (System.currentTimeMillis() - start));
	    if (analyzeWords == null || analyzeWords.size() < 3) {
		break;
	    }
	    start = System.currentTimeMillis();

	    if (lastWord1 != null) {
		String ngram = toNGgramString(Arrays.asList(lastWord1, lastWord2, analyzeWords.get(0)));
		update(ngram, 3);
		count++;
	    }
	    if (lastWord2 != null) {
		String ngram = toNGgramString(Arrays.asList(lastWord2, analyzeWords.get(0), analyzeWords.get(1)));
		update(ngram, 3);
		count++;
	    }
	    for (int i = 0; i < analyzeWords.size() - 2; i++) {
		String ngram = toNGgramString(Arrays.asList(analyzeWords.get(i), analyzeWords.get(i + 1), analyzeWords.get(i + 2)));
		update(ngram, 3);
		count++;
	    }
	    lastWord1 = analyzeWords.get(analyzeWords.size() - 2);
	    lastWord2 = analyzeWords.get(analyzeWords.size() - 1);

	    System.out.println("Kelimeleri yükleme: " + (System.currentTimeMillis() - start));
	}
	System.out.println(count);
	bis.close();
    }

    public static Map<String, Float> createTriGrams(List<Word> wordList) {
	Map<String, Float> trigrams = new HashMap<String, Float>();
	if (wordList == null || wordList.size() < 3) {
	    return trigrams;
	}

	Map<String, Integer> triGramsCountMap = new HashMap<String, Integer>();
	int count = 0;
	for (int i = 0; i < wordList.size() - 2; i++) {
	    String ngram = toNGgramString(Arrays.asList(wordList.get(i), wordList.get(i + 1), wordList.get(i + 2)));
	    updateMap(triGramsCountMap, ngram);
	    count++;
	}

	float total = (float) count;
	// float minProb = 1 / total;
	for (Entry<String, Integer> entry : triGramsCountMap.entrySet()) {
	    trigrams.put(entry.getKey(), ((float) entry.getValue()) / total);
	}

	return trigrams;
    }
}

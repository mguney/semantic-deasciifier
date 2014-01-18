package com.gun3y.nlp.deasciifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.gun3y.nlp.helper.DeasciifierHelper;
import com.gun3y.nlp.helper.NGramHelper;
import com.gun3y.nlp.model.CountModel;
import com.gun3y.nlp.model.Pair;
import com.gun3y.nlp.model.Word;
import com.gun3y.nlp.mongo.MongoManager;

public class SemanticDeasciifier {

    private static final Logger LOGGER = Logger.getLogger(SemanticDeasciifier.class);

    public String deasciify(String input) {
	if (StringUtils.isBlank(input)) {
	    return StringUtils.EMPTY;
	}
	LOGGER.info(input);

	String[] words = input.split("\\s+");

	return deasciify(Arrays.asList(words));
    }

    public String deasciify(List<String> input) {

	if (input == null || input.isEmpty()) {
	    return StringUtils.EMPTY;
	}
	List<Word> deasciifiedWords = new ArrayList<Word>();
	List<Word> wordList = DeasciifierHelper.analyzeWords(input);
	for (Word word : wordList) {
	    word.setDeasciified(!DeasciifierHelper.containsEnglishLetter(word.getWord()));
	    //
	    // if (word.isMarked()) {
	    // word.setDeasciified(true);
	    // }

	    LOGGER.info(word.toString());
	}
	if (wordList.isEmpty()) {
	    return StringUtils.EMPTY;
	}
	if (wordList.size() <= 3) {
	    deasciifiedWords.addAll(deasciify(wordList, wordList.size()));
	}
	else {
	    deasciifiedWords.addAll(deasciify(wordList, 3));
	}

	return DeasciifierHelper.convertToString(deasciifiedWords);
    }

    private List<Word> deasciify(List<Word> words, int window) {
	if (words == null || words.isEmpty()) {
	    return Collections.emptyList();
	}

	LOGGER.info("Window:" + words);
	LOGGER.info("Windows Size:" + window);

	if (isAllDeasciified(words)) {
	    return words;
	}

	List<Word> retWords = new ArrayList<Word>();
	if (window < 1) {
	    Deasciifier deasciifier = new Deasciifier();
	    for (Word word : words) {
		Word deasciifiedWord = new Word(deasciifier.deasciify(word.getWord()));
		deasciifiedWord.setDeasciified(true);
		retWords.add(deasciifiedWord);
	    }
	    LOGGER.info("Deasciified: " + retWords);
	    return retWords;
	}

	for (int i = 0; i < words.size() - window + 1; i++) {
	    List<Word> windowList = sublist(words, i, i + window);
	    LOGGER.info("Kontrol Edilen:" + windowList);
	    List<Word> deasciifiedWindow = deasciifyWindow(windowList);
	    if (deasciifiedWindow == null) {
		LOGGER.info("Bulunamadı: " + windowList);
		deasciifiedWindow = deasciify(windowList, window - 1);
		if (retWords.isEmpty()) {
		    retWords.addAll(deasciifiedWindow);
		}
		else {
		    retWords.add(deasciifiedWindow.get(deasciifiedWindow.size() - 1));
		}

	    }
	    else if (retWords.isEmpty()) {
		retWords.addAll(deasciifiedWindow);
		LOGGER.info("Bulundu:" + deasciifiedWindow);
	    }
	    else if (!deasciifiedWindow.isEmpty()) {
		retWords.add(deasciifiedWindow.get(deasciifiedWindow.size() - 1));
		LOGGER.info("Bulundu:" + deasciifiedWindow);
	    }
	}

	return retWords;
    }

    private <T> List<T> sublist(List<T> list, int start, int end) {
	List<T> accList = new LinkedList<T>();
	for (int i = start; i < end; i++) {
	    accList.add(list.get(i));
	}
	return accList;
    }

    private List<Word> deasciifyWindow(List<Word> words) {
	int min = Integer.MIN_VALUE;
	if (isAllDeasciified(words)) {
	    return words;
	}
	List<List<Word>> similarWords = DeasciifierHelper.generateSimilarWords(words);
	List<Pair<Integer, Integer>> indices = new ArrayList<Pair<Integer, Integer>>();
	for (int i = 0; i < similarWords.size(); i++) {
	    String ngram = NGramHelper.toNGgramString(similarWords.get(i));

	    CountModel model = MongoManager.getInstance().getCountModelById(ngram);
	    if (model == null) {
		indices.add(new Pair<Integer, Integer>(i, min));
	    }
	    else {
		indices.add(new Pair<Integer, Integer>(i, model.getCount()));
	    }
	}

	Collections.sort(indices, new Comparator<Pair<Integer, Integer>>() {
	    @Override
	    public int compare(Pair<Integer, Integer> arg0, Pair<Integer, Integer> arg1) {
		return Integer.compare(arg1.getValue(), arg0.getValue());
	    }
	});

	if (!indices.isEmpty() && indices.get(0).getValue() > min) {
	    List<Word> list = similarWords.get(indices.get(0).getKey());

	    if (words.size() != list.size()) {
		LOGGER.error("Window boyutları uyuşmadı! ");
	    }
	    else {
		for (int i = 0; i < words.size(); i++) {
		    Word word = words.get(i);
		    word.setDeasciified(true);
		    word.setWord(list.get(i).getWord());
		    word.setStemmedWord(list.get(i).getStemmedWord());
		}
	    }

	    return words;
	}

	return null;
    }

    private boolean isAllDeasciified(List<Word> words) {
	if (words == null || words.isEmpty()) {
	    return false;
	}
	for (Word word : words) {
	    if (!word.isDeasciified()) {
		return false;
	    }
	}
	return true;
    }

    public static void main(String[] args) {
	SemanticDeasciifier semanticDeasciifier = new SemanticDeasciifier();
	String input = "ali";
	String asciiInput = DeasciifierHelper.asciify(input);

	String deasciifiedInput = semanticDeasciifier.deasciify(asciiInput);

	System.out.println(input);
	System.out.println(asciiInput);
	System.out.println(deasciifiedInput);
	System.out.println(input.equals(deasciifiedInput.trim()));

    }
}

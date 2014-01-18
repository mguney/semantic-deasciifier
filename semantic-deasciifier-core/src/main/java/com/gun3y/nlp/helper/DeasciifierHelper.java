package com.gun3y.nlp.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import net.zemberek.erisim.Zemberek;
import net.zemberek.tr.yapi.TurkiyeTurkcesi;
import net.zemberek.yapi.Kelime;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.gun3y.nlp.deasciifier.Deasciifier;
import com.gun3y.nlp.model.Word;
import com.gun3y.nlp.mongo.MongoManager;

public class DeasciifierHelper {

    private static final Logger LOGGER = Logger.getLogger(DeasciifierHelper.class);

    public static Zemberek ZEMBEREK = new Zemberek(new TurkiyeTurkcesi());

    public static Set<Character> CHARS = new HashSet<Character>();

    static {
	CHARS.add('c');
	CHARS.add('C');
	CHARS.add(Deasciifier.DEASCII_TR_LOWER_C);
	CHARS.add(Deasciifier.DEASCII_TR_UPPER_C);
	CHARS.add('u');
	CHARS.add('U');
	CHARS.add(Deasciifier.DEASCII_TR_LOWER_U);
	CHARS.add(Deasciifier.DEASCII_TR_UPPER_U);
	CHARS.add('g');
	CHARS.add('G');
	CHARS.add(Deasciifier.DEASCII_TR_LOWER_G);
	CHARS.add(Deasciifier.DEASCII_TR_UPPER_G);
	CHARS.add('ı');
	CHARS.add('I');
	CHARS.add(Deasciifier.DEASCII_TR_LOWER_I);
	CHARS.add(Deasciifier.DEASCII_TR_UPPER_I);
	CHARS.add('s');
	CHARS.add('S');
	CHARS.add(Deasciifier.DEASCII_TR_LOWER_S);
	CHARS.add(Deasciifier.DEASCII_TR_UPPER_S);
	CHARS.add('o');
	CHARS.add('O');
	CHARS.add(Deasciifier.DEASCII_TR_LOWER_O);
	CHARS.add(Deasciifier.DEASCII_TR_UPPER_O);
    }

    public static List<List<Word>> cartesian(List<List<Word>> words) {
	List<List<Word>> generatedSentences = new ArrayList<List<Word>>();
	int solutions = 1;
	for (int i = 0; i < words.size(); i++) {
	    solutions *= words.get(i).size();
	}
	for (int i = 0; i < solutions; i++) {
	    int j = 1;
	    List<Word> wordList = new ArrayList<Word>();
	    for (List<Word> row : words) {
		wordList.add(row.get((i / j) % row.size()));
		j *= row.size();
	    }
	    generatedSentences.add(wordList);
	}

	return generatedSentences;
    }

    public static List<List<Word>> generateSimilarWords(List<Word> words) {
	if (words == null || words.isEmpty()) {
	    return Collections.emptyList();
	}
	List<List<Word>> tempWordList = new ArrayList<List<Word>>();
	for (int i = 0; i < words.size(); i++) {
	    List<Word> gWords = generateSimilarWords(words.get(i));
	    tempWordList.add(gWords);
	}

	return cartesian(tempWordList);

    }

    public static List<Word> generateSimilarWords(Word word) {
	List<Word> wordList = new ArrayList<Word>();
	if (word == null) {
	    return wordList;
	}
	if (word.isDeasciified()) {
	    wordList.add(word);
	    return wordList;
	}

	String rawWord = word.getWord();
	if (StringUtils.isBlank(rawWord)) {
	    return wordList;
	}
	List<StringBuilder> builders = new ArrayList<StringBuilder>();
	builders.add(new StringBuilder());

	char[] wordChars = rawWord.toCharArray();
	for (char c : wordChars) {
	    if (Deasciifier.CHAR_MAP.containsKey(c)) {
		List<StringBuilder> tempBuilders = new ArrayList<StringBuilder>();
		for (StringBuilder builder : builders) {
		    tempBuilders.add(new StringBuilder(builder.toString()));
		}

		for (StringBuilder builder : tempBuilders) {
		    builder.append(c);
		}
		for (StringBuilder builder : builders) {
		    builder.append(Deasciifier.CHAR_MAP.get(c));
		}

		builders.addAll(tempBuilders);

	    }
	    else {
		for (StringBuilder builder : builders) {
		    builder.append(c);
		}
	    }
	}

	wordList.addAll(analyzeWords(builders));

	return wordList;
    }

    public static boolean containsEnglishLetter(String str) {
	if (StringUtils.isBlank(str)) {
	    return false;
	}

	char[] chars = str.toCharArray();
	for (char c : chars) {
	    if (CHARS.contains(c)) {
		return true;
	    }
	}

	return false;
    }

    public static String convertToString(Collection<Word> words) {
	StringBuilder stringBuilder = new StringBuilder();
	if (words != null && !words.isEmpty()) {
	    for (Word word : words) {
		stringBuilder.append(word.getWord()).append(" ");
	    }
	}
	return stringBuilder.toString();
    }

    public static String asciify(String text) {
	if (StringUtils.isBlank(text)) {
	    return StringUtils.EMPTY;
	}

	return ZEMBEREK.asciiyeDonustur(text);
    }

    public static void loadModelToMongo() {
	MongoManager.getInstance().removeAll();
	LOGGER.info("Veritabanı temizlendi");

	List<String> extractedWords = FileHelper.extractWords();
	LOGGER.info("Kelimeler getirildi.");

	List<Word> wordList = analyzeWords(extractedWords);
	LOGGER.info("Kelimeler analiz edildi.");

	Map<String, Float> unigrams = NGramHelper.createUniGrams(wordList);
	LOGGER.info("Unigram modeller oluşturuldu. Toplam:" + unigrams.size());

	Map<String, Float> bigrams = NGramHelper.createBiGrams(wordList);
	LOGGER.info("Bigram modeller oluşturuldu. Toplam:" + bigrams.size());

	Map<String, Float> trigrams = NGramHelper.createTriGrams(wordList);
	LOGGER.info("Trigram modeller oluşturuldu. Toplam:" + trigrams.size());

	MongoManager.getInstance().insertModel(unigrams.entrySet());
	LOGGER.info("Unigram modeller veritabanına yüklendi.");

	MongoManager.getInstance().insertModel(bigrams.entrySet());
	LOGGER.info("Bigram modeller veritabanına yüklendi.");

	MongoManager.getInstance().insertModel(trigrams.entrySet());
	LOGGER.info("Trigram modeller veritabanına yüklendi.");
    }

    private static Kelime findLongestWord(Kelime[] wordList) {
	Kelime pivot = wordList[0];
	for (int i = 1; i < wordList.length; i++) {
	    if (pivot.kok().icerik().length() < wordList[i].kok().icerik().length()) {
		pivot = wordList[i];
	    }
	}
	return pivot;
    }

    // private static List<Word> findSynonyms(String rawWord, Kelime[] wordList)
    // {
    // if (wordList == null || wordList.length == 0) {
    // return null;
    // }
    // List<Word> words = new ArrayList<Word>();
    // for (Kelime kelime : wordList) {
    // Word w = new Word(rawWord, kelime.kok().icerik());
    // if (!words.contains(w)) {
    // words.add(w);
    // }
    //
    // }
    // return words;
    // }

    public static List<Word> analyzeWords(List<?> rawWordList) {
	Vector<Word> wordList = new Vector<Word>();

	if (rawWordList == null || rawWordList.isEmpty()) {
	    return wordList;
	}
	for (Object rawWord : rawWordList) {
	    Kelime[] kelimeCozumle = ZEMBEREK.kelimeCozumle(rawWord.toString());
	    if (kelimeCozumle != null && kelimeCozumle.length > 0) {
		Kelime findLongestWord = findLongestWord(kelimeCozumle);
		Word word = new Word(rawWord.toString(), findLongestWord.kok().icerik());
		word.setMarked(true);
		wordList.add(word);
	    }
	    else {
		wordList.add(new Word(rawWord.toString(), rawWord.toString()));
	    }
	}

	return wordList;
    }
}

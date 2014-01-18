package com.gun3y.nlp.deasciifier;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.gun3y.nlp.helper.DeasciifierHelper;
import com.gun3y.nlp.helper.FileHelper;

public class TestDeasciifier {

    static File TEST_DATA = new File(TestDeasciifier.class.getClassLoader().getResource("test.txt").getPath());

    public static void main(String[] args) {
	String testData = FileHelper.readFile(TEST_DATA, "utf-8");

	String[] lines = testData.split("\n");

	SemanticDeasciifier deasciifier = new SemanticDeasciifier();
	int total = 0;
	int english = 0;
	int success = 0;
	int fail = 0;
	Set<String> s = new HashSet<String>();
	Set<String> f = new HashSet<String>();
	for (String line : lines) {
	    List<String> wordList = new ArrayList<String>();
	    List<String> asciifiedWordList = new ArrayList<String>();
	    String[] words = line.split("\\s+");
	    for (String word : words) {
		if (StringUtils.isNotBlank(word) && word.length() > 1) {
		    String tempWord = StringUtils.isAlphanumeric(word.charAt(0) + "") ? word : word.substring(1);
		    tempWord = StringUtils.isAlphanumeric(word.charAt(word.length() - 1) + "") ? tempWord : tempWord.substring(0, word.length() - 1);
		    if (StringUtils.isNotBlank(tempWord)) {
			wordList.add(tempWord);
			asciifiedWordList.add(DeasciifierHelper.asciify(tempWord));

			if (DeasciifierHelper.containsEnglishLetter(tempWord))
			    english++;

			total++;
		    }
		}
	    }

	    String deasciifiedText = deasciifier.deasciify(asciifiedWordList);
	    String[] deasciifiedWords = deasciifiedText.split("\\s+");
	    if (deasciifiedWords.length != wordList.size()) {
		System.out.println("!!!!!!!!!!!!!!!!!!!!!! bişiler yanlış");
	    }

	    for (int i = 0; i < deasciifiedWords.length; i++) {
		if (deasciifiedWords[i].equals(wordList.get(i))) {
		    if (DeasciifierHelper.containsEnglishLetter(deasciifiedWords[i]))
			success++;
		    s.add(deasciifiedWords[i]);
		}
		else {
		    fail++;
		    f.add(deasciifiedWords[i]);
		    System.out.println(asciifiedWordList.get(i) + " " + deasciifiedWords[i]);
		}
	    }
	}

	System.out.println(total + " " + english + " " + success + " " + fail);
	System.out.println(s.size());
	System.out.println(f.size());

    }
}

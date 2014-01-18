package com.gun3y.nlp.deasciifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gun3y.nlp.helper.FileHelper;

public class Deasciifier {

    public static final char DEASCII_TR_LOWER_C = '\u00E7';
    public static final char DEASCII_TR_UPPER_C = '\u00C7';
    public static final char DEASCII_TR_LOWER_G = '\u011F';
    public static final char DEASCII_TR_UPPER_G = '\u011E';
    public static final char DEASCII_TR_LOWER_I = '\u0131';
    public static final char DEASCII_TR_UPPER_I = '\u0130';
    public static final char DEASCII_TR_LOWER_O = '\u00F6';
    public static final char DEASCII_TR_UPPER_O = '\u00D6';
    public static final char DEASCII_TR_LOWER_S = '\u015F';
    public static final char DEASCII_TR_UPPER_S = '\u015E';
    public static final char DEASCII_TR_LOWER_U = '\u00FC';
    public static final char DEASCII_TR_UPPER_U = '\u00DC';

    static Map<String, Map<String, Integer>> patternMap = null;

    static Map<String, String> asciifyMap = new HashMap<String, String>();

    static Map<String, String> downcaseAsciifyMap = new HashMap<String, String>();

    static String[] uppercaseLetters = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
	    "X", "Y", "Z" };

    static Map<String, String> upcaseAccentsMap = new HashMap<String, String>();

    static Map<String, String> toggleAccentMap = new HashMap<String, String>();

    public static Map<Character, Character> CHAR_MAP = new HashMap<Character, Character>();

    static {
	CHAR_MAP.put('c', DEASCII_TR_LOWER_C);
	CHAR_MAP.put('C', DEASCII_TR_UPPER_C);
	CHAR_MAP.put('g', DEASCII_TR_LOWER_G);
	CHAR_MAP.put('G', DEASCII_TR_UPPER_G);
	CHAR_MAP.put('i', DEASCII_TR_LOWER_I);
	CHAR_MAP.put('I', DEASCII_TR_UPPER_I);
	CHAR_MAP.put('o', DEASCII_TR_LOWER_O);
	CHAR_MAP.put('O', DEASCII_TR_UPPER_O);
	CHAR_MAP.put('s', DEASCII_TR_LOWER_S);
	CHAR_MAP.put('S', DEASCII_TR_UPPER_S);
	CHAR_MAP.put('u', DEASCII_TR_LOWER_U);
	CHAR_MAP.put('U', DEASCII_TR_UPPER_S);

	for (Entry<Character, Character> entry : CHAR_MAP.entrySet()) {
	    asciifyMap.put(entry.getValue() + "", entry.getKey() + "");
	    downcaseAsciifyMap.put(entry.getValue() + "", Character.toLowerCase(entry.getKey()) + "");
	    upcaseAccentsMap.put(entry.getValue() + "", Character.toUpperCase(entry.getKey()) + "");

	    toggleAccentMap.put(entry.getKey() + "", entry.getValue() + "");
	    toggleAccentMap.put(entry.getValue() + "", entry.getKey() + "");
	}

	for (String c : uppercaseLetters) {
	    downcaseAsciifyMap.put(c, c.toLowerCase());
	    downcaseAsciifyMap.put(c.toLowerCase(), c.toLowerCase());

	    upcaseAccentsMap.put(c, c.toLowerCase());
	    upcaseAccentsMap.put(c.toLowerCase(), c.toLowerCase());
	}

	upcaseAccentsMap.put("i", "i");
	upcaseAccentsMap.put("I", "I");
	upcaseAccentsMap.put(DEASCII_TR_UPPER_I + "", "i");
	upcaseAccentsMap.put(DEASCII_TR_LOWER_I + "", "I");

	Gson gson = new Gson();
	patternMap = gson.fromJson(FileHelper.readPatternTable(), new TypeToken<Map<String, Map<String, Integer>>>() {
	}.getType());
    }

    private String deasciifiedText;

    private int turkishContextSize = 10;

    public static void main(String[] args) {
	Deasciifier deasciifier = new Deasciifier();
	System.out.println(deasciifier.deasciify("gozlukcu, oduncu, agac"));
	// for (Entry<String, Map<String, Integer>> e1 :
	// deasciifier.turkishPatternTable.entrySet()) {
	// for (Entry<String, Integer> e2 : e1.getValue().entrySet()) {
	// System.out.println(e1.getKey() + " " + e2.getKey() + " " +
	// e2.getValue());
	// }
	// }
    }

    public static String setCharAt(String mystr, int pos, String c) {
	return mystr.substring(0, pos).concat(c).concat(mystr.substring(pos + 1, mystr.length()));
    }

    public String turkishToggleAccent(String c) {
	String result = toggleAccentMap.get(c);
	return (result == null) ? c : result;
    }

    public static String repeatString(String haystack, int times) {
	StringBuilder tmp = new StringBuilder();
	for (int i = 0; i < times; i++)
	    tmp.append(haystack);

	return tmp.toString();
    }

    public boolean turkishMatchPattern(int index, Map<String, Integer> dlist) {
	int rank = dlist.size() * 2;
	String str = turkishGetContext(index, turkishContextSize);

	int start = 0;
	int end = 0;
	int _len = str.length();

	while (start <= turkishContextSize) {
	    end = turkishContextSize + 1;
	    while (end <= _len) {
		String s = str.substring(start, end);

		Integer r = dlist.get(s);

		if (r != null && Math.abs(r) < Math.abs(rank)) {
		    rank = r;
		}
		end++;
	    }
	    start++;
	}
	return rank > 0;
    }

    public static String charAt(String source, int index) {
	return new Character(source.charAt(index)).toString();
    }

    public String turkishGetContext(int idx, int size) {
	String s = repeatString(" ", (1 + (2 * size)));
	s = setCharAt(s, size, "X");

	int i = size + 1;
	boolean space = false;
	int index = idx;
	index++;

	String currentChar;

	while (i < s.length() && !space && index < this.deasciifiedText.length()) {
	    currentChar = this.deasciifiedText.charAt(index) + "";

	    String x = downcaseAsciifyMap.get(currentChar);

	    if (x == null) {
		if (!space) {
		    i++;
		    space = true;
		}
	    }
	    else {
		s = setCharAt(s, i, x);
		i++;
		space = false;
	    }
	    index++;
	}

	s = s.substring(0, i);

	index = idx;
	i = size - 1;
	space = false;

	index--;

	while (i >= 0 && index >= 0) {
	    currentChar = this.deasciifiedText.charAt(index) + "";
	    String x = upcaseAccentsMap.get(currentChar);

	    if (x == null) {
		if (!space) {
		    i--;
		    space = true;
		}
	    }
	    else {
		s = setCharAt(s, i, x);
		i--;
		space = false;
	    }
	    index--;
	}

	return s;
    }

    public boolean turkishNeedCorrection(int index) {
	String ch = this.deasciifiedText.charAt(index) + "";

	String tr = asciifyMap.get(ch);

	if (tr == null) {
	    tr = ch;
	}

	Map<String, Integer> pl = patternMap.get(tr.toLowerCase());

	boolean m = false;

	if (pl != null) {
	    m = turkishMatchPattern(index, pl);

	}

	if (tr.equals("I")) {
	    return ch.equals(tr) ? !m : m;
	}
	else {
	    return ch.equals(tr) ? m : !m;
	}
    }

    public String deasciify(String text) {
	if (StringUtils.isBlank(text)) {
	    return StringUtils.EMPTY;
	}

	this.deasciifiedText = text;

	for (int index = 0; index < text.length(); index++) {
	    String c = text.charAt(index) + "";

	    if (turkishNeedCorrection(index)) {
		deasciifiedText = setCharAt(deasciifiedText, index, turkishToggleAccent(c));
	    }
	    else {
		deasciifiedText = setCharAt(deasciifiedText, index, c);
	    }
	}

	return deasciifiedText;

    }
}

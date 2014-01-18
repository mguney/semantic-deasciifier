package com.gun3y.nlp.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import net.zemberek.araclar.turkce.YaziIsleyici;
import net.zemberek.islemler.cozumleme.KelimeCozumleyici;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.gun3y.nlp.deasciifier.SemanticDeasciifier;

public class FileHelper {

    private static final Logger LOGGER = Logger.getLogger(FileHelper.class);

    private static final String ROOT_FOLDER = "a";

    private static List<File> getFiles() {
	List<File> retFiles = new ArrayList<File>();
	File rootDir = new File(ROOT_FOLDER);
	if (rootDir.isDirectory()) {
	    for (File writerFolder : rootDir.listFiles()) {
		if (writerFolder.isDirectory()) {
		    File[] files = writerFolder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String arg1) {
			    return arg1.endsWith(".txt");
			}
		    });
		    retFiles.addAll(Arrays.asList(files));
		}
	    }
	}
	LOGGER.info("Dosyalar bulundu.");

	return retFiles;
    }

    public static String readFile(File f) {
	return readFile(f, "windows-1254");
    }

    public static String readFile(File f, String encoding) {
	if (f == null || encoding == null) {
	    return StringUtils.EMPTY;
	}
	String res = StringUtils.EMPTY;
	try {
	    res = readFile(f.getPath(), encoding);
	}
	catch (Exception e) {
	    LOGGER.error(e);
	}

	return res;
    }

    private static String readFile(String fileName, String encoding) throws Exception {
	StringBuffer sb = new StringBuffer();
	FileInputStream fis = new FileInputStream(new File(fileName));
	//
	BufferedReader bis = new BufferedReader(new InputStreamReader(fis, encoding));
	String s;
	while ((s = bis.readLine()) != null) {
	    sb.append(s);
	    sb.append("\n");
	}
	bis.close();
	return sb.toString();
    }

    private static List<String> readFiles(String encoding) {
	List<String> retList = new ArrayList<String>();
	List<File> files = getFiles();
	for (File file : files) {
	    retList.add(readFile(file, encoding));
	}
	LOGGER.info("Dosyalar okundu.");

	return retList;
    }

    private static boolean customCheck(String str) {
	return !str.contains(".") && !containsDigit(str);
    }

    public static boolean containsDigit(String s) {
	boolean containsDigit = false;

	if (s != null) {
	    for (char c : s.toCharArray()) {
		if (containsDigit = Character.isDigit(c)) {
		    break;
		}
	    }
	}

	return containsDigit;
    }

    public static void extractWordsAndWriteTo(String fileName, String encoding) {

	File file = new File(fileName);
	if (!file.exists()) {
	    try {
		file.createNewFile();
	    }
	    catch (IOException e) {
		LOGGER.error(e);
	    }
	}
	FileWriter fw = null;
	BufferedWriter bw = null;
	try {
	    fw = new FileWriter(file.getAbsoluteFile());
	    bw = new BufferedWriter(fw);

	    KelimeCozumleyici cozumleyici = DeasciifierHelper.ZEMBEREK.cozumleyici();

	    List<File> files = getFiles();

	    for (File f : files) {
		FileInputStream fis = new FileInputStream(f);
		BufferedReader bis = new BufferedReader(new InputStreamReader(fis, encoding));
		String s;
		while ((s = bis.readLine()) != null) {
		    List<String> words = YaziIsleyici.kelimeAyikla(s);
		    for (String str : words) {
			if (str.length() > 1) {
			    if (cozumleyici.cozumlenebilir(str)) {
				bw.write(str + "\n");
			    }
			    else if (customCheck(str)) {
				bw.write(str + "\n");
			    }
			}

		    }
		}
		bis.close();
	    }
	}
	catch (IOException e) {
	    LOGGER.error(e);
	}
	finally {
	    if (bw != null)
		try {
		    bw.close();
		}
		catch (IOException e) {
		}
	}
    }

    public static List<String> extractWords() {
	Vector<String> retList = new Vector<String>();
	List<String> texts = readFiles("windows-1254");

	KelimeCozumleyici cozumleyici = DeasciifierHelper.ZEMBEREK.cozumleyici();

	for (String text : texts) {
	    List<String> words = YaziIsleyici.kelimeAyikla(text);

	    for (String str : words) {
		if (cozumleyici.cozumlenebilir(str)) {
		    retList.add(str);
		}
		else if (customCheck(str)) {
		    retList.add(str);
		}
	    }
	}
	LOGGER.info("Kelimeler ayıklandı. Toplam Kelime Sayısı:" + retList.size());

	return retList;
    }

    private static class CustomSAXParser extends DefaultHandler {

	int count = 60444;

	String folder = "C:\\Users\\musta_000\\Desktop\\m2\\";

	StringBuilder builder = new StringBuilder();

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
	    super.startElement(uri, localName, qName, attributes);

	    if (qName.equalsIgnoreCase("DOC")) {
		builder = new StringBuilder();
		count++;
		System.out.println(count);
	    }

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
	    super.endElement(uri, localName, qName);
	    if (qName.equalsIgnoreCase("S")) {
		builder.append("\n");
	    }
	    else if (qName.equalsIgnoreCase("DOC")) {
		try {
		    FileUtils.writeStringToFile(new File(folder + count + ".txt"), builder.toString(), "UTF-8");
		}
		catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
	    super.characters(ch, start, length);
	    String val = new String(ch, start, length);

	    if (StringUtils.isNotBlank(val)) {
		String[] words = val.split("\\s+");
		for (String str : words) {
		    if (StringUtils.isNotBlank(str)) {
			builder.append(str.trim()).append(" ");
		    }
		}

	    }
	}
    }

    public static void main(String[] args) throws Exception {
	// URL url = NGramHelper.class.getClassLoader().getResource("data.txt");
	// File dataFile = new File(url.getPath());
	// String file = readFile(dataFile, "windows-1254");

	// extractWordsAndWriteTo("asd.txt", "windows-1254");
	List<String> readFiles = readFiles("windows-1254");

	SemanticDeasciifier semanticDeasciifier = new SemanticDeasciifier();

	File file = new File("test.txt");
	if (!file.exists()) {
	    try {
		file.createNewFile();
	    }
	    catch (IOException e) {
		LOGGER.error(e);
	    }
	}
	FileWriter fw = null;
	BufferedWriter bw = null;
	try {
	    fw = new FileWriter(file.getAbsoluteFile());
	    bw = new BufferedWriter(fw);

	    for (String string : readFiles) {
		String[] split = string.split("\n");
		for (String string2 : split) {
		    if (StringUtils.isNotBlank(string2)) {
			bw.write(string2);
			bw.write("\n");
			// String line = string2.replaceAll("[ ]{2,}",
			// " ").trim();
			// System.out.println(line);
			// String line =
			// string2.replaceAll("[^a-zA-Z0-9 üğışçöÜĞİŞÇÖ.']",
			// " ").replaceAll("[ ]{2,}", " ").trim();
			// System.out.println(line);
			// String asciifiedLine =
			// DeasciifierHelper.asciify(line);
			//
			// String deasciifiedLine =
			// semanticDeasciifier.deasciify(asciifiedLine);
			//
			// if (!deasciifiedLine.trim().equals(line)) {
			// System.out.println(line);
			// System.out.println(deasciifiedLine);
			// error++;
			// }
			// else {
			// succes++;
			// }

		    }
		}
	    }
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
	finally {
	    if (bw != null) {
		bw.close();
	    }
	}
	// int succes = 0;
	// int error = 0;
	// for (String string : readFiles) {
	// String[] split = string.split("\n");
	// for (String string2 : split) {
	// if (StringUtils.isNotBlank(string2)) {
	// // String line = string2.replaceAll("[ ]{2,}", " ").trim();
	// // System.out.println(line);
	// String line = string2.replaceAll("[^a-zA-Z0-9 üğışçöÜĞİŞÇÖ.']",
	// " ").replaceAll("[ ]{2,}", " ").trim();
	// System.out.println(line);
	// // String asciifiedLine = DeasciifierHelper.asciify(line);
	// //
	// // String deasciifiedLine =
	// // semanticDeasciifier.deasciify(asciifiedLine);
	// //
	// // if (!deasciifiedLine.trim().equals(line)) {
	// // System.out.println(line);
	// // System.out.println(deasciifiedLine);
	// // error++;
	// // }
	// // else {
	// // succes++;
	// // }
	//
	// }
	// }
	// }
	// System.out.println(succes);
	// System.out.println(error);
	// System.out.println(readFiles);
    }

    public static void convertEncoding() throws Exception {
	Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
		"D:\\Workspace\\eclipse-workspace\\semantic-deasciifier\\69yazar\\milliyet\\data.txt"), "windows-1254"));
	try {
	    FileInputStream fis = new FileInputStream(new File("C:\\Users\\musta_000\\Desktop\\m2\\data.txt"));
	    BufferedReader bis = new BufferedReader(new InputStreamReader(fis, "utf-8"));
	    String s;
	    while ((s = bis.readLine()) != null) {
		out.write(s);
		out.write("\n");
	    }
	    bis.close();

	}
	finally {
	    out.close();
	}
    }

    public static String readPatternTable() {
	try {
	    File file = new File("turkish-pattern.json");
	    File f = new File(FileHelper.class.getClassLoader().getResource("turkish-pattern.json").getPath());
	    if (f.exists()) {
		return FileUtils.readFileToString(f);
	    }
	    else {
		return FileUtils.readFileToString(file);
	    }

	}
	catch (IOException e) {
	    LOGGER.error("turkish-pattern.json okunamadı", e);
	    return StringUtils.EMPTY;
	}
    }
}

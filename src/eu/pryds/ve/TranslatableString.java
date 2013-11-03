package eu.pryds.ve;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

public class TranslatableString {
	private String translatorComments;
	private String extractedComments;
	private Vector <String> reference;
	private Vector <String> flags;
	private String previousContext;
	private String previousUntranslatedString;
	private String previousUntranslatedStringPlural;
	private String context;
	private String untranslatedString;
	private String untranslatedStringPlural;
	private Vector <String> translatedString;
	
	public TranslatableString() {
		translatorComments = "";
		extractedComments = "";
		reference = new Vector<String>();
		flags = new Vector<String>();
		previousContext = "";
		previousUntranslatedString = "";
		previousUntranslatedStringPlural = "";
		context = "";
		untranslatedString = "";
		untranslatedStringPlural = "";
		translatedString = new Vector<String>();
	}
	
	public static TranslatableString[] parse(String[] poFileLines) {
		Vector<TranslatableString> v = new Vector<TranslatableString>();
		
		TranslatableString str = new TranslatableString();
		
		for (int i = 0; i < poFileLines.length; i++) {
			if (poFileLines[i] == null || poFileLines[i].trim().equals("")) {
				v.add(str);
				str = new TranslatableString();
			
			} else if (poFileLines[i].startsWith("# ")) {
				if (str.translatorComments.length() > 0)
					str.translatorComments += '\n';
				str.translatorComments += poFileLines[i].substring(2).trim();
			
			} else if (poFileLines[i].startsWith("#.")) {
				if (str.extractedComments.length() > 0)
					str.extractedComments += '\n';
				str.extractedComments += poFileLines[i].substring(2).trim();
			
			} else if (poFileLines[i].startsWith("#:")) {
				str.reference.add(poFileLines[i].substring(2).trim());
			
			} else if (poFileLines[i].startsWith("#,")) {
				str.flags.add(poFileLines[i].substring(2).trim());
			
			} else if (poFileLines[i].startsWith("#| msgctxt")) {
				str.previousContext = trimQuotes(poFileLines[i].substring(10));
			
			} else if (poFileLines[i].startsWith("#| msgid_plural")) {
				str.previousUntranslatedStringPlural = trimQuotes(poFileLines[i].substring(8));
			
			} else if (poFileLines[i].startsWith("#| msgid")) {
				str.previousUntranslatedString = trimQuotes(poFileLines[i].substring(8));
			
			} else if (poFileLines[i].startsWith("msgctxt")) {
				str.context = trimQuotes(poFileLines[i].substring(7));
			
			} else if (poFileLines[i].startsWith("msid_plural")) {
				str.untranslatedStringPlural = trimQuotes(poFileLines[i].substring(11));
			
			} else if (poFileLines[i].startsWith("msgid")) {
				str.untranslatedString = trimQuotes(poFileLines[i].substring(5));
			
			} else if (poFileLines[i].startsWith("msgstr[")) {
				int indexOfSquareEndBracket = poFileLines[i].indexOf(']');
				String strNoStr = poFileLines[i].substring(7, indexOfSquareEndBracket);
				int strNo = Integer.parseInt(strNoStr);
				str.translatedString.set(strNo, trimQuotes(poFileLines[i].substring(strNo+1)));
				
			} else if (poFileLines[i].startsWith("msgstr")) {
				str.translatedString = new Vector<String>();
				str.translatedString.add(trimQuotes(poFileLines[i].substring(6)));
			}
		}
		v.add(str);
		
		return v.toArray(new TranslatableString[]{});
	}
	
	private static String trimQuotes(String str) {
		return str.trim().replaceAll("^\"|\"$", "");
	}
	
	public static void main(String[] args) {
		Vector<String> v = new Vector<String>();
		try {
			File f = new File(TranslatableString.class.getResource("test.po").toURI());
			BufferedReader r = new BufferedReader(new FileReader(f));
			
			String l;
			while ((l = r.readLine()) != null) {
				v.add(l);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		TranslatableString[] strs = parse(v.toArray(new String[]{}));
		System.out.println("=== Parsed data ===");
		System.out.println("Translator comments: " + strs[0].translatorComments);
		System.out.println("Extracted comments: " + strs[0].extractedComments);
		System.out.println("Reference: " + strs[0].reference);
		System.out.println("Flag: " + strs[0].flags);
		System.out.println("Previous context: " + strs[0].previousContext);
		System.out.println("Previous untranslated string: " + strs[0].previousUntranslatedString);
		System.out.println("Previous untranslated string plural: " + strs[0].previousUntranslatedStringPlural);
		System.out.println("Context: " + strs[0].context);
		System.out.println("Untranslated string: " + strs[0].untranslatedString);
		System.out.println("Untranslated string plural: " + strs[0].untranslatedStringPlural);
		System.out.println("Translated string(s): " + strs[0].translatedString);

	}
}

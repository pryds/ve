package eu.pryds.ve;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Hashtable;
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
	private Hashtable<Integer, String> translatedString;
	
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
		translatedString = new Hashtable<Integer, String>();
	}
	
	public static TranslatableString[] parse(String[] poFileLines) {
		Vector<TranslatableString> v = new Vector<TranslatableString>();
		
		TranslatableString str = new TranslatableString();
		
		final int MSGID = 0;
		final int MSGID_PLURAL = 1;
		final int MSGSTR = 2;
		int lastWrittenMultiliner = MSGID;
		int lastWrittenMsgstrIndex = 0;
		
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
				String[] parts = poFileLines[i].substring(2).trim().split(" ");
				for (int j = 0; j < parts.length; j++) {
					str.reference.add(parts[j]);
				}
			
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
			
			} else if (poFileLines[i].startsWith("msgid_plural")) {
				str.untranslatedStringPlural = trimQuotes(poFileLines[i].substring(12));
				lastWrittenMultiliner = MSGID_PLURAL;
			
			} else if (poFileLines[i].startsWith("msgid")) {
				str.untranslatedString = trimQuotes(poFileLines[i].substring(5));
				lastWrittenMultiliner = MSGID;
			
			} else if (poFileLines[i].startsWith("msgstr[")) {
				int indexOfSquareEndBracket = poFileLines[i].indexOf(']');
				String strNoStr = poFileLines[i].substring(7, indexOfSquareEndBracket);
				int strNo = Integer.parseInt(strNoStr);
				if (strNo == 0) {
					str.translatedString = new Hashtable<Integer, String>();
				}
				str.translatedString.put(strNo, trimQuotes(poFileLines[i].substring(indexOfSquareEndBracket+1)));
				lastWrittenMultiliner = MSGSTR;
				lastWrittenMsgstrIndex = strNo;
				
			} else if (poFileLines[i].startsWith("msgstr")) {
				str.translatedString = new Hashtable<Integer, String>();
				str.translatedString.put(0, trimQuotes(poFileLines[i].substring(6)));
				lastWrittenMultiliner = MSGSTR;
				lastWrittenMsgstrIndex = 0;
				
			} else if (poFileLines[i].startsWith("\"")) {
				if (lastWrittenMultiliner == MSGID) {
					if (str.untranslatedString.equals(""))
						str.untranslatedString = trimQuotes(poFileLines[i]);
					else
						str.untranslatedString += '\n' + trimQuotes(poFileLines[i]);
					
				} else if (lastWrittenMultiliner == MSGID_PLURAL) {
					if (str.untranslatedStringPlural.equals(""))
						str.untranslatedStringPlural = trimQuotes(poFileLines[i]);
					else
						str.untranslatedStringPlural += '\n' + trimQuotes(poFileLines[i]);
					
				} else if (lastWrittenMultiliner == MSGSTR) {
					String existingData = str.translatedString.get(lastWrittenMsgstrIndex);
					if (existingData.equals(""))
						str.translatedString.put(lastWrittenMsgstrIndex, trimQuotes(poFileLines[i]));
					else
						str.translatedString.put(lastWrittenMsgstrIndex, existingData + '\n' + trimQuotes(poFileLines[i]));
				}
				
			} else {
				System.out.println("Unexpected line " + (i+1) + ": " + poFileLines[i]);
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
			r.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("v size() = " + v.size());
		TranslatableString[] strs = parse(v.toArray(new String[]{}));
		for (int i = 0; i < strs.length; i++) {
			System.out.println("=== Parsed data for string " + i + " ===");
			System.out.println("Translator comments: " + strs[i].translatorComments);
			System.out.println("Extracted comments: " + strs[i].extractedComments);
			System.out.println("Reference: " + strs[i].reference);
			System.out.println("Flag: " + strs[i].flags);
			System.out.println("Previous context: " + strs[i].previousContext);
			System.out.println("Previous untranslated string: " + strs[i].previousUntranslatedString);
			System.out.println("Previous untranslated string plural: " + strs[i].previousUntranslatedStringPlural);
			System.out.println("Context: " + strs[i].context);
			System.out.println("Untranslated string: " + strs[i].untranslatedString);
			System.out.println("Untranslated string plural: " + strs[i].untranslatedStringPlural);
			System.out.println("Translated string(s): " + strs[i].translatedString);
		}
	}
}

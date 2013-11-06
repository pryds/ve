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
		final int MSGCTXT = 3;
		int lastWrittenMultiliner = MSGID;
		int lastWrittenMsgstrIndex = 0;
		
		for (int i = 0; i < poFileLines.length; i++) {
			if (poFileLines[i] == null || poFileLines[i].trim().equals("")) {
				v.add(str);
				str = new TranslatableString();
			
			} else if (poFileLines[i].startsWith("# ")) {
				if (str.translatorComments.length() > 0)
					str.translatorComments += "\n";
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
				String[] parts = poFileLines[i].substring(2).trim().split(", ");
				for (int j = 0; j < parts.length; j++) {
					str.flags.add(parts[j]);
				}
			
			} else if (poFileLines[i].startsWith("#| msgctxt")) {
				if (str.previousContext.length() > 0)
					str.previousContext += '\n';
				str.previousContext += trimQuotes(poFileLines[i].substring(10));
			
			} else if (poFileLines[i].startsWith("#| msgid_plural")) {
				if (str.previousUntranslatedStringPlural.length() > 0)
					str.previousUntranslatedStringPlural += '\n';
				str.previousUntranslatedStringPlural += trimQuotes(poFileLines[i].substring(8));
			
			} else if (poFileLines[i].startsWith("#| msgid")) {
				if (str.previousUntranslatedString.length() > 0)
					str.previousUntranslatedString += '\n';
				str.previousUntranslatedString += trimQuotes(poFileLines[i].substring(8));
			
			} else if (poFileLines[i].startsWith("msgctxt")) {
				str.context = trimQuotes(poFileLines[i].substring(7));
				lastWrittenMultiliner = MSGCTXT;
			
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
				} else if (lastWrittenMultiliner == MSGCTXT) {
					//TODO
				}
				
			} else {
				System.out.println("Unexpected line " + (i+1) + ": " + poFileLines[i]);
			}
		}
		v.add(str);
		
		return v.toArray(new TranslatableString[]{});
	}
	
	public static String[] toPoFile(TranslatableString[] strings) {
		Vector<String> outputLines = new Vector<String>();
		
		for (int i = 0; i < strings.length; i++) {
			if (i != 0)
				outputLines.add("");
			
			if (!strings[i].translatorComments.equals("")) {
				String[] transCommLines = strings[i].translatorComments.split("\n");
				for (int j = 0; j < transCommLines.length; j++)
					outputLines.add("#  " + transCommLines[j]);
			}
			
			if (!strings[i].extractedComments.equals("")) {
				String[] extrCommLines = strings[i].extractedComments.split("\n");
				for (int j = 0; j < extrCommLines.length; j++)
					outputLines.add("#. " + extrCommLines[j]);
			}
			
			final int LENGTH_OF_LINES_MINUS_SHORT_PREFIX = 80 - "#  ".length(); // 77
			final int LENGTH_OF_LINES_MINUS_MSGID_PREFIX = 80 - "#| msgid ".length(); // 71
			if (strings[i].reference.size() > 0) {
				StringBuffer refstr = new StringBuffer();
				for (int j = 0; j < strings[i].reference.size(); j++) {
					if (j != 0)
						refstr.append(" ");
					refstr.append(strings[i].reference.get(j));
				}
				String[] refWrapped = wordWrapToArray(refstr.toString(), LENGTH_OF_LINES_MINUS_SHORT_PREFIX);
				for (int j = 0; j < refWrapped.length; j++)
					outputLines.add("#: " + refWrapped[j]);
			}
			
			if (strings[i].flags.size() > 0) {
				StringBuffer flagsstr = new StringBuffer();
				for (int j = 0; j < strings[i].flags.size(); j++) {
					if (j != 0)
						flagsstr.append(", ");
					flagsstr.append(strings[i].flags.get(j));
				}
				String[] flagsWrapped = wordWrapToArray(flagsstr.toString(), LENGTH_OF_LINES_MINUS_SHORT_PREFIX);
				for (int j = 0; j < flagsWrapped.length; j++)
					outputLines.add("#, " + flagsWrapped[j]);
			}
			
			if (!strings[i].previousContext.equals(""))
				outputLines.add("#| msgctxt " + strings[i].previousContext);
			//TODO: Multiline
			
			if (!strings[i].previousUntranslatedString.equals("")) {
				String[] prevUntrStrWrapped = wordWrapToArray(strings[i].previousUntranslatedString, LENGTH_OF_LINES_MINUS_MSGID_PREFIX);
				for (int j = 0; j < prevUntrStrWrapped.length; j++)
					outputLines.add("#| msgid " + prevUntrStrWrapped[j]);
			}
		}
		return outputLines.toArray(new String[]{});
	}
	
	private static String trimQuotes(String str) {
		return str.trim().replaceAll("^\"|\"$", "");
	}
	
	/**
	 * Word-wraps a long lined string to the given max-width.
	 * <p>
	 * If there are already newlines in the input string, the input
	 * is split at these newlines, and each substring is fed to the
	 * private method {@link #wordWrapOneLine(String, int)}, after
	 * which all (now wrapped) substrings are re-joined, with a
	 * newline in-between each of them.
	 * @param input a string to be word-wrapped
	 * @param width maximum length of each line, in characters
	 * @return new string containing word-wrapped version of input
	 * 
	 * @see #wordWrapOneLine(String, int)
	 */
	public static String wordWrap(String input, int width) {
	    String[] inputLines = input.split("\n");
	    StringBuffer output = new StringBuffer();
	    for (int i = 0; i < inputLines.length; i++) {
	        if (i != 0)
	            output.append("\n");
	        output.append(wordWrapOneLine(inputLines[i], width));
	    }
	    return output.toString();
	}
	
	public static String[] wordWrapToArray(String input, int width) {
		return wordWrap(input, width).split("\n");
	}
	
	private static String wordWrapOneLine(String input, int width) {
	    input = input.trim();
	    if (input.length() <= width) {
	        return input;
	    } else {
	        int lastSpaceIndex = input.lastIndexOf(" ", width);
	        if (lastSpaceIndex == -1)
	            lastSpaceIndex = width;
	        
	        String output1 = input.substring(0, lastSpaceIndex).trim() + "\n";
	        String output2 = input.substring(lastSpaceIndex).trim();
	        input = null;
	        return output1 + wordWrapOneLine(output2, width);
	    }
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
		String[] outputLines = toPoFile(strs);
		
		for (int i = 0; i < outputLines.length; i++)
			System.out.println(outputLines[i]);
		
		/*
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
		}*/
	}
}

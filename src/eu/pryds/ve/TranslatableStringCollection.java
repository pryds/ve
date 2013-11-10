package eu.pryds.ve;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.Vector;

public class TranslatableStringCollection {
	Vector<TranslatableString> strings;
	
	public TranslatableStringCollection() {
		strings = new Vector<TranslatableString>();
	}
	
	
	public void parse(String[] poFileLines) {
		TranslatableString str = new TranslatableString();
		
		final int MSGID = 0;
		final int MSGID_PLURAL = 1;
		final int MSGSTR = 2;
		final int MSGCTXT = 3;
		int lastWrittenMultiliner = MSGID;
		int lastWrittenMsgstrIndex = 0;
		
		for (int i = 0; i < poFileLines.length; i++) {
			if (poFileLines[i] == null || poFileLines[i].trim().equals("")) {
				strings.add(str);
				str = new TranslatableString();
			
			} else if (poFileLines[i].startsWith("# ")) {
				if (str.getTranslatorComments().length() > 0)
					str.addtoTranslatorComments(""+'\n');
				str.addtoTranslatorComments(poFileLines[i].substring(2).trim());
			
			} else if (poFileLines[i].startsWith("#.")) {
				if (str.getExtractedComments().length() > 0)
					str.addtoExtractedComments(""+'\n');
				str.addtoExtractedComments(poFileLines[i].substring(2).trim());
			
			} else if (poFileLines[i].startsWith("#:")) {
				String[] parts = poFileLines[i].substring(2).trim().split(" ");
				for (int j = 0; j < parts.length; j++) {
					str.getReferences().add(parts[j]);
				}
			
			} else if (poFileLines[i].startsWith("#,")) {
				String[] parts = poFileLines[i].substring(2).trim().split(", ");
				for (int j = 0; j < parts.length; j++) {
					str.getFlags().add(parts[j]);
				}
			
			} else if (poFileLines[i].startsWith("#| msgctxt")) {
				if (str.getPreviousContext().length() > 0)
					str.addtoPreviousContext(""+'\n');
				str.addtoPreviousContext(trimQuotes(poFileLines[i].substring(10)));
			
			} else if (poFileLines[i].startsWith("#| msgid_plural")) {
				if (str.getPreviousUntranslatedStringPlural().length() > 0)
					str.addtoPreviousUntranslatedStringPlural(""+'\n');
				str.addtoPreviousUntranslatedStringPlural(trimQuotes(poFileLines[i].substring(15)));
			
			} else if (poFileLines[i].startsWith("#| msgid")) {
				if (str.getPreviousUntranslatedString().length() > 0)
					str.addtoPreviousUntranslatedString(""+'\n');
				str.addtoPreviousUntranslatedString(trimQuotes(poFileLines[i].substring(8)));
			
			} else if (poFileLines[i].startsWith("msgctxt")) {
				str.setContext(trimQuotes(poFileLines[i].substring(7)));
				lastWrittenMultiliner = MSGCTXT;
			
			} else if (poFileLines[i].startsWith("msgid_plural")) {
				str.setUntranslatedStringPlural(trimQuotes(poFileLines[i].substring(12)));
				lastWrittenMultiliner = MSGID_PLURAL;
			
			} else if (poFileLines[i].startsWith("msgid")) {
				str.setUntranslatedString(trimQuotes(poFileLines[i].substring(5)));
				lastWrittenMultiliner = MSGID;
			
			} else if (poFileLines[i].startsWith("msgstr[")) {
				int indexOfSquareEndBracket = poFileLines[i].indexOf(']');
				String strNoStr = poFileLines[i].substring(7, indexOfSquareEndBracket);
				int strNo = Integer.parseInt(strNoStr);
				if (strNo == 0) {
					str.resetTranslatedString();
				}
				str.getTranslatedString().put(strNo, trimQuotes(poFileLines[i].substring(indexOfSquareEndBracket+1)));
				lastWrittenMultiliner = MSGSTR;
				lastWrittenMsgstrIndex = strNo;
				
			} else if (poFileLines[i].startsWith("msgstr")) {
				str.resetTranslatedString();
				str.getTranslatedString().put(0, trimQuotes(poFileLines[i].substring(6)));
				lastWrittenMultiliner = MSGSTR;
				lastWrittenMsgstrIndex = 0;
				
			} else if (poFileLines[i].startsWith("\"")) {
				if (lastWrittenMultiliner == MSGID) {
					if (!str.getUntranslatedString().equals(""))
						str.addtoUntranslatedString(""+'\n');
					str.addtoUntranslatedString(trimQuotes(poFileLines[i]));
					
				} else if (lastWrittenMultiliner == MSGID_PLURAL) {
					if (!str.getUntranslatedStringPlural().equals(""))
						str.addtoUntranslatedStringPlural(""+'\n');
					str.addtoUntranslatedStringPlural(trimQuotes(poFileLines[i]));
					
				} else if (lastWrittenMultiliner == MSGSTR) {
					String existingData = str.getTranslatedString().get(lastWrittenMsgstrIndex);
					if (existingData.equals(""))
						str.getTranslatedString().put(lastWrittenMsgstrIndex, trimQuotes(poFileLines[i]));
					else
						str.getTranslatedString().put(lastWrittenMsgstrIndex, existingData + '\n' + trimQuotes(poFileLines[i]));
				} else if (lastWrittenMultiliner == MSGCTXT) {
					if (!str.getContext().equals(""))
						str.addtoContext(""+'\n');
					str.addtoContext(trimQuotes(poFileLines[i]));
				}
				
			} else {
				System.out.println("Unexpected line " + (i+1) + ": " + poFileLines[i]);
			}
		}
		strings.add(str);
		
		//TODO: Check for empty TranslatableStrings
	}
	
	public String[] toPoFile() {
		Vector<String> outputLines = new Vector<String>();
		
		for (int i = 0; i < strings.size(); i++) {
			if (i != 0)
				outputLines.add("");
			
			if (!strings.get(i).getTranslatorComments().equals("")) {
				String[] transCommLines = strings.get(i).getTranslatorComments().split("\n");
				for (int j = 0; j < transCommLines.length; j++)
					outputLines.add("#  " + transCommLines[j]);
			}
			
			if (!strings.get(i).getExtractedComments().equals("")) {
				String[] extrCommLines = strings.get(i).getExtractedComments().split("\n");
				for (int j = 0; j < extrCommLines.length; j++)
					outputLines.add("#. " + extrCommLines[j]);
			}
			
			final int LENGTH_OF_LINES_MINUS_SHORT_PREFIX = 80 - "#  ".length(); // 77
			if (strings.get(i).getReferences().size() > 0) {
				StringBuffer refstr = new StringBuffer();
				for (int j = 0; j < strings.get(i).getReferences().size(); j++) {
					if (j != 0)
						refstr.append(" ");
					refstr.append(strings.get(i).getReferences().get(j));
				}
				String[] refWrapped = wordWrapToArray(refstr.toString(), LENGTH_OF_LINES_MINUS_SHORT_PREFIX);
				for (int j = 0; j < refWrapped.length; j++)
					outputLines.add("#: " + refWrapped[j].trim());
			}
			
			if (strings.get(i).getFlags().size() > 0) {
				StringBuffer flagsstr = new StringBuffer();
				for (int j = 0; j < strings.get(i).getFlags().size(); j++) {
					if (j != 0)
						flagsstr.append(", ");
					flagsstr.append(strings.get(i).getFlags().get(j));
				}
				String[] flagsWrapped = wordWrapToArray(flagsstr.toString(), LENGTH_OF_LINES_MINUS_SHORT_PREFIX);
				for (int j = 0; j < flagsWrapped.length; j++)
					outputLines.add("#, " + flagsWrapped[j]);
			}
			
			if (!strings.get(i).getPreviousContext().equals("")) {
				String[] prevContextLines = strings.get(i).getPreviousContext().split("\n");
				for (int j = 0; j < prevContextLines.length; j++)
					outputLines.add("#| msgctxt \"" + prevContextLines[j] + "\"");
			}
			
			if (!strings.get(i).getPreviousUntranslatedString().equals("")) {
				String[] prevUntrStrWrapped = strings.get(i).getPreviousUntranslatedString().split("\n");
				for (int j = 0; j < prevUntrStrWrapped.length; j++)
					outputLines.add("#| msgid \"" + prevUntrStrWrapped[j] + "\"");
			}
			
			if (!strings.get(i).getPreviousUntranslatedStringPlural().equals("")) {
				String[] prevUntrStrPlurWrapped = strings.get(i).getPreviousUntranslatedStringPlural().split("\n");
				for (int j = 0; j < prevUntrStrPlurWrapped.length; j++) 
					outputLines.add("#| msgid_plural \"" + prevUntrStrPlurWrapped[j] + "\"");
			}
			
			if (!strings.get(i).getContext().equals("")) {
				writeMultilinesTo(outputLines, "msgctxt ", strings.get(i).getContext());
			}
			
			if (!strings.get(i).getUntranslatedString().equals("")) {
				writeMultilinesTo(outputLines, "msgid ", strings.get(i).getUntranslatedString());
			}
			
			if (!strings.get(i).getUntranslatedStringPlural().equals("")) {
				writeMultilinesTo(outputLines, "msgid_plural ", strings.get(i).getUntranslatedStringPlural());
			}
			
			if (strings.get(i).getTranslatedString().size() > 0) {
				if (strings.get(i).getTranslatedString().size() == 1) {
					writeMultilinesTo(outputLines, "msgstr ", strings.get(i).getTranslatedString().get(0));
				} else {
					for (int j = 0; j < strings.get(i).getTranslatedString().size(); j++)
						writeMultilinesTo(outputLines, "msgstr[" + j + "] ", strings.get(i).getTranslatedString().get(j));
				}
			}
		}
		return outputLines.toArray(new String[]{});
	}
	
	private static String trimQuotes(String str) {
		return str.trim().replaceAll("^\"|\"$", "");
	}
	
	public static String[] wordWrapToArray(String input, int width) {
		return wordWrap(input, width).split("\n");
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
	
	private static String wordWrapOneLine(String input, int width) {
	    if (input.length() <= width) {
	        return input;
	    } else {
	        int lastSpaceIndex = input.lastIndexOf(" ", width);
	        if (lastSpaceIndex == -1)
	            lastSpaceIndex = width;
	        
	        String output1 = input.substring(0, lastSpaceIndex).trim() + " " + '\n';
	        String output2 = input.substring(lastSpaceIndex).trim();
	        input = null;
	        return output1 + wordWrapOneLine(output2, width);
	    }
	}
	
	private static void writeMultilinesTo(Vector<String> outputLines, String prefix, String writeString) {
		final int LINE_WIDTH = 80;
		if (writeString.length() > LINE_WIDTH - prefix.length()) {
			String[] wrappedLines = wordWrapToArray(writeString, LINE_WIDTH - "\"\"".length());
			outputLines.add(prefix + "\"\"");
			for (int i = 0; i < wrappedLines.length; i++)
				outputLines.add("\"" + wrappedLines[i] + "\"");
		} else {
			outputLines.add(prefix + "\"" + writeString + "\"");
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
		
		TranslatableStringCollection coll = new TranslatableStringCollection();
		coll.parse(v.toArray(new String[]{}));
		String[] outputLines = coll.toPoFile();
		
		for (int i = 0; i < outputLines.length; i++)
			System.out.println(outputLines[i]);
		
		
		/*
		System.out.println("==============================================================");
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

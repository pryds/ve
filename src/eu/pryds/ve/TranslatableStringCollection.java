package eu.pryds.ve;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;

public class TranslatableStringCollection implements Parcelable {
    Vector<TranslatableString> strings;
    TranslatableString header; //contains po header info
    StringBuffer removedStrings = new StringBuffer(); // strings in bottom of file, prefixed "#~ "
    
    public TranslatableStringCollection() {
        strings = new Vector<TranslatableString>();
        header = null;
    }
    
    public TranslatableString getString(int id) {
        return strings.get(id);
    }
    
    public TranslatableString getHeader() {
        return header;
    }
    
    public int size() {
        return strings.size();
    }
    
    public int countFuzzyStrings() {
        int count = 0;
        for (int i = 0; i < strings.size(); i++)
            if (strings.get(i).isFuzzy())
                count++;
        return count;
    }
    
    public int countUntranslatedStrings() {
        int count = 0;
        for (int i = 0; i < strings.size(); i++)
            if (strings.get(i).isUntranslated())
                count++;
        return count;
    }
    
    public boolean parse(File poFile, Activity activity) {
        Vector<String> poFileLines = new Vector<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(poFile));
            
            String line;
            while ((line = reader.readLine()) != null) {
                poFileLines.add(line);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace(); // TODO: Proper exception handling
        }
        
        if (poFileLines.size() == 0 || !poFileLines.get(0).startsWith("#"))
            return false;
        
        TranslatableString str = new TranslatableString();
        
        final int MSGID = 0;
        final int MSGID_PLURAL = 1;
        final int MSGSTR = 2;
        final int MSGCTXT = 3;
        int lastWrittenMultiliner = MSGID;
        int lastWrittenMsgstrIndex = 0;
        boolean removedStringsReached = false;
        
        for (int i = 0; i < poFileLines.size(); i++) {
            if (removedStringsReached) {
                removedStrings.append(poFileLines.get(i)).append('\n');
            
            } else if (poFileLines.get(i) == null || poFileLines.get(i).trim().equals("")) {
                strings.add(str);
                str = new TranslatableString();
                
            } else if (poFileLines.get(i).startsWith("# ")) {
                if (str.getTranslatorComments().length() > 0)
                    str.addtoTranslatorComments("" + '\n');
                str.addtoTranslatorComments(poFileLines.get(i).substring(2).trim());
                
            } else if (poFileLines.get(i).startsWith("#.")) {
                if (str.getExtractedComments().length() > 0)
                    str.addtoExtractedComments("" + '\n');
                str.addtoExtractedComments(poFileLines.get(i).substring(2).trim());
                
            } else if (poFileLines.get(i).startsWith("#:")) {
                String[] parts = poFileLines.get(i).substring(2).trim().split(" ");
                for (int j = 0; j < parts.length; j++) {
                    str.getReferences().add(parts[j]);
                }
                
            } else if (poFileLines.get(i).startsWith("#,")) {
                String[] parts = poFileLines.get(i).substring(2).trim().split(", ");
                for (int j = 0; j < parts.length; j++) {
                    str.getFlags().add(parts[j]);
                }
                
            } else if (poFileLines.get(i).startsWith("#| msgctxt")) {
                if (str.getPreviousContext().length() > 0)
                    str.addtoPreviousContext("" + '\n');
                str.addtoPreviousContext(trimQuotes(poFileLines.get(i)
                        .substring(10)));
                
            } else if (poFileLines.get(i).startsWith("#| msgid_plural")) {
                if (str.getPreviousUntranslatedStringPlural().length() > 0)
                    str.addtoPreviousUntranslatedStringPlural("" + '\n');
                str.addtoPreviousUntranslatedStringPlural(trimQuotes(poFileLines.get(i)
                        .substring(15)));
                
            } else if (poFileLines.get(i).startsWith("#| msgid")) {
                if (str.getPreviousUntranslatedString().length() > 0)
                    str.addtoPreviousUntranslatedString("" + '\n');
                str.addtoPreviousUntranslatedString(trimQuotes(poFileLines.get(i)
                        .substring(8)));
                
            } else if (poFileLines.get(i).startsWith("msgctxt")) {
                str.setContext(trimQuotes(poFileLines.get(i).substring(7)));
                lastWrittenMultiliner = MSGCTXT;
                
            } else if (poFileLines.get(i).startsWith("msgid_plural")) {
                str.setUntranslatedStringPlural(trimQuotes(poFileLines.get(i)
                        .substring(12)));
                lastWrittenMultiliner = MSGID_PLURAL;
                
            } else if (poFileLines.get(i).startsWith("msgid")) {
                str.setUntranslatedString(trimQuotes(poFileLines.get(i)
                        .substring(5)));
                lastWrittenMultiliner = MSGID;
                
            } else if (poFileLines.get(i).startsWith("msgstr[")) {
                int indexOfSquareEndBracket = poFileLines.get(i).indexOf(']');
                String strNoStr = poFileLines.get(i).substring(7,
                        indexOfSquareEndBracket);
                int strNo = Integer.parseInt(strNoStr);
                if (strNo == 0) {
                    str.resetTranslatedString();
                }
                str.getTranslatedString().put(
                        strNo,
                        trimQuotes(poFileLines.get(i)
                                .substring(indexOfSquareEndBracket + 1)));
                lastWrittenMultiliner = MSGSTR;
                lastWrittenMsgstrIndex = strNo;
                
            } else if (poFileLines.get(i).startsWith("msgstr")) {
                str.resetTranslatedString();
                str.getTranslatedString().put(0,
                        trimQuotes(poFileLines.get(i).substring(6)));
                lastWrittenMultiliner = MSGSTR;
                lastWrittenMsgstrIndex = 0;
                
            } else if (poFileLines.get(i).startsWith("#~ ")) {
                removedStrings.append(poFileLines.get(i)).append('\n');
                removedStringsReached = true;
                
            } else if (poFileLines.get(i).startsWith("\"")) {
                if (lastWrittenMultiliner == MSGID) {
                    if (!str.getUntranslatedString().equals(""))
                        str.addtoUntranslatedString("" + '\n');
                    str.addtoUntranslatedString(trimQuotes(poFileLines.get(i)));
                    
                } else if (lastWrittenMultiliner == MSGID_PLURAL) {
                    if (!str.getUntranslatedStringPlural().equals(""))
                        str.addtoUntranslatedStringPlural("" + '\n');
                    str.addtoUntranslatedStringPlural(trimQuotes(poFileLines.get(i)));
                    
                } else if (lastWrittenMultiliner == MSGSTR) {
                    String existingData = str.getTranslatedString().get(
                            lastWrittenMsgstrIndex);
                    if (existingData.equals(""))
                        str.getTranslatedString().put(lastWrittenMsgstrIndex,
                                trimQuotes(poFileLines.get(i)));
                    else
                        str.getTranslatedString().put(
                                lastWrittenMsgstrIndex,
                                existingData + '\n'
                                        + trimQuotes(poFileLines.get(i)));
                } else if (lastWrittenMultiliner == MSGCTXT) {
                    if (!str.getContext().equals(""))
                        str.addtoContext("" + '\n');
                    str.addtoContext(trimQuotes(poFileLines.get(i)));
                }
                
            } else {
                //System.out.println("Unexpected line " + (i + 1) + ": "
                //        + poFileLines.get(i));
            }
        }
        strings.add(str);
        
        // Check for and remove completely empty TranslatableStrings
        for (int i = strings.size()-1; i >= 0; i--) {
            if (strings.get(i).isEmpty())
                strings.remove(i);
        }
        
        // Find index of first occurrence of empty untranslated string
        // and move that to the header var instead
        for (int i = 0; i < strings.size(); i++) {
            if (strings.get(i).containsHeaderInfo()) {
                header = strings.get(i);
                strings.remove(i);
                break;
            }
        }
        
        // If no header entry was found, create one now
        if (header == null) {
            header = new TranslatableString();
            header.initiateHeaderInfo(activity);
        }
        return true;
    }
    
    public String[] toPoFile(Activity activity) {
        // update existing header entry
        header.updateHeaderInfo(activity);
        
        Vector<TranslatableString> strToWrite = (Vector<TranslatableString>) strings.clone();
        strToWrite.add(0, header);
        Vector<String> outputLines = new Vector<String>();
        
        for (int i = 0; i < strToWrite.size(); i++) {
            if (i != 0)
                outputLines.add("");
            
            if (!strToWrite.get(i).getTranslatorComments().equals("")) {
                String[] transCommLines = strToWrite.get(i)
                        .getTranslatorComments().split("\n");
                for (int j = 0; j < transCommLines.length; j++)
                    outputLines.add("#  " + transCommLines[j]);
            }
            
            if (!strToWrite.get(i).getExtractedComments().equals("")) {
                String[] extrCommLines = strToWrite.get(i).getExtractedComments()
                        .split("\n");
                for (int j = 0; j < extrCommLines.length; j++)
                    outputLines.add("#. " + extrCommLines[j]);
            }
            
            final int LENGTH_OF_LINES_MINUS_SHORT_PREFIX = 80 - "#  ".length(); // 77
            if (strToWrite.get(i).getReferences().size() > 0) {
                StringBuffer refstr = new StringBuffer();
                for (int j = 0; j < strToWrite.get(i).getReferences().size(); j++) {
                    if (j != 0)
                        refstr.append(" ");
                    refstr.append(strToWrite.get(i).getReferences().get(j));
                }
                String[] refWrapped = wordWrapToArray(refstr.toString(),
                        LENGTH_OF_LINES_MINUS_SHORT_PREFIX);
                for (int j = 0; j < refWrapped.length; j++)
                    outputLines.add("#: " + refWrapped[j].trim());
            }
            
            if (strToWrite.get(i).getFlags().size() > 0) {
                StringBuffer flagsstr = new StringBuffer();
                for (int j = 0; j < strToWrite.get(i).getFlags().size(); j++) {
                    if (j != 0)
                        flagsstr.append(", ");
                    flagsstr.append(strToWrite.get(i).getFlags().get(j));
                }
                String[] flagsWrapped = wordWrapToArray(flagsstr.toString(),
                        LENGTH_OF_LINES_MINUS_SHORT_PREFIX);
                for (int j = 0; j < flagsWrapped.length; j++)
                    outputLines.add("#, " + flagsWrapped[j]);
            }
            
            if (!strToWrite.get(i).getPreviousContext().equals("")) {
                String[] prevContextLines = strToWrite.get(i).getPreviousContext()
                        .split("\n");
                for (int j = 0; j < prevContextLines.length; j++)
                    outputLines.add("#| msgctxt \"" + prevContextLines[j]
                            + "\"");
            }
            
            if (!strToWrite.get(i).getPreviousUntranslatedString().equals("")) {
                String[] prevUntrStrWrapped = strToWrite.get(i)
                        .getPreviousUntranslatedString().split("\n");
                for (int j = 0; j < prevUntrStrWrapped.length; j++)
                    outputLines.add("#| msgid \"" + prevUntrStrWrapped[j]
                            + "\"");
            }
            
            if (!strToWrite.get(i).getPreviousUntranslatedStringPlural()
                    .equals("")) {
                String[] prevUntrStrPlurWrapped = strToWrite.get(i)
                        .getPreviousUntranslatedStringPlural().split("\n");
                for (int j = 0; j < prevUntrStrPlurWrapped.length; j++)
                    outputLines.add("#| msgid_plural \""
                            + prevUntrStrPlurWrapped[j] + "\"");
            }
            
            if (!strToWrite.get(i).getContext().equals("")) {
                writeMultilinesTo(outputLines, "msgctxt ", strToWrite.get(i)
                        .getContext());
            }
            
            if (!strToWrite.get(i).getUntranslatedString().equals("")) {
                writeMultilinesTo(outputLines, "msgid ", strToWrite.get(i)
                        .getUntranslatedString());
            }
            
            if (!strToWrite.get(i).getUntranslatedStringPlural().equals("")) {
                writeMultilinesTo(outputLines, "msgid_plural ", strToWrite.get(i)
                        .getUntranslatedStringPlural());
            }
            
            if (strToWrite.get(i).getTranslatedString().size() > 0) {
                if (strToWrite.get(i).getTranslatedString().size() == 1) {
                    writeMultilinesTo(outputLines, "msgstr ", strToWrite.get(i)
                            .getTranslatedString().get(0));
                } else {
                    for (int j = 0; j < strToWrite.get(i).getTranslatedString()
                            .size(); j++)
                        writeMultilinesTo(outputLines, "msgstr[" + j + "] ",
                                strToWrite.get(i).getTranslatedString().get(j));
                }
            }
        }
        if (removedStrings.length() > 0) {
            outputLines.add("");
            outputLines.add(removedStrings.toString().trim());
        }
        
        return outputLines.toArray(new String[] {});
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
     * If there are already newlines in the input string, the input is split at
     * these newlines, and each substring is fed to the private method
     * {@link #wordWrapOneLine(String, int)}, after which all (now wrapped)
     * substrings are re-joined, with a newline in-between each of them.
     * 
     * @param input
     *            a string to be word-wrapped
     * @param width
     *            maximum length of each line, in characters
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
            
            String output1 = input.substring(0, lastSpaceIndex).trim() + " "
                    + '\n';
            String output2 = input.substring(lastSpaceIndex).trim();
            input = null;
            return output1 + wordWrapOneLine(output2, width);
        }
    }
    
    private static void writeMultilinesTo(Vector<String> outputLines,
            String prefix, String writeString) {
        final int LINE_WIDTH = 80;
        if (writeString.length() > LINE_WIDTH - prefix.length()) {
            String[] wrappedLines = wordWrapToArray(writeString, LINE_WIDTH
                    - "\"\"".length());
            outputLines.add(prefix + "\"\"");
            for (int i = 0; i < wrappedLines.length; i++)
                outputLines.add("\"" + wrappedLines[i] + "\"");
        } else {
            outputLines.add(prefix + "\"" + writeString + "\"");
        }
    }
    
    // Parcelable stuff
    
    private TranslatableStringCollection(Parcel in) {
        in.readTypedList(strings, TranslatableString.CREATOR);
        header = (TranslatableString) in.readParcelable(
                TranslatableString.class.getClassLoader());
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel parcel, int parcelflags) {
        parcel.writeTypedList(strings);
        parcel.writeParcelable(header, PARCELABLE_WRITE_RETURN_VALUE);
    }
    
    public static final Parcelable.Creator<TranslatableStringCollection> CREATOR =
            new Parcelable.Creator<TranslatableStringCollection>() {
        public TranslatableStringCollection createFromParcel(Parcel in) {
            return new TranslatableStringCollection(in);
        }
        public TranslatableStringCollection[] newArray(int size) {
            return new TranslatableStringCollection[size];
        }
    };
    
    /*
     * Useful resources on Parcelable:
     * http://theopentutorials.com/tutorials/android/android-sending-object-from-one-activity-to-another-using-parcelable/
     * http://stackoverflow.com/questions/7042272/how-to-properly-implement-parcelable-with-an-arraylistparcelable
     * http://developer.android.com/reference/android/os/Parcelable.html
     * 
     * Useful resources on instance bundles (in which can be saved Parcelable objects):
     * http://developer.android.com/training/basics/activity-lifecycle/recreating.html
     * http://www.intertech.com/Blog/saving-and-retrieving-android-instance-state-part-1/
     */
}

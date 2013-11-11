package eu.pryds.ve;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class TranslatableString {
    private String translatorComments;
    private String extractedComments;
    private Vector<String> reference;
    private Vector<String> flags;
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
    
    public boolean isEmpty() {
        return (
                isEmpty(translatorComments) && isEmpty(extractedComments) &&
                isEmpty(reference) && isEmpty(flags) &&
                isEmpty(previousContext) &&
                isEmpty(previousUntranslatedString) &&
                isEmpty(previousUntranslatedStringPlural) &&
                isEmpty(context) && isEmpty(untranslatedString) &&
                isEmpty(untranslatedStringPlural) && isEmpty(translatedString)
                );
    }
    
    public boolean containsHeaderInfo() {
        return (isEmpty(untranslatedString) && !this.isEmpty());
    }
    
    private final int DEFAULT_PLURAL_FORM_COUNT = 100; //TODO: change to 2
    private final String BSLASHN_NL = "\\n\n";
    
    public int getHeaderPluralFormCount() {
        if (!this.containsHeaderInfo())
            return -1;
        String[] headerLines = translatedString.get(0).split(BSLASHN_NL);
        for (int i = 0; i < headerLines.length; i++) {
            if (headerLines[i].startsWith("Plural-Forms:")) {
                int index1 = headerLines[i].indexOf("nplurals=") + 9;
                int index2 = headerLines[i].indexOf(';', index1);
                if (index1 != -1 && index2 != -1) {
                    String index =
                            headerLines[i].substring(index1, index2).trim();
                    return Integer.parseInt(index);
                }
            }
        }
        return DEFAULT_PLURAL_FORM_COUNT;
    }
    
    public void initiateHeaderInfo() {
        translatedString = new Hashtable<Integer, String>();
        translatedString.put(0,
                "PO-Revision-Date: " + (new SimpleDateFormat(
                    "yyyy-MM-dd HH:mmZ").format(new Date())) + BSLASHN_NL +
                "Last-Translator: " + BSLASHN_NL + //TODO: Fill with real values
                "Language-Team: " + BSLASHN_NL +
                "Language: " + BSLASHN_NL +
                "MIME-Version: 1.0" + BSLASHN_NL +
                "Content-Type: text/plain; charset=UTF-8" + BSLASHN_NL +
                "Content-Transfer-Encoding: 8bit" + BSLASHN_NL +
                "Plural-Forms: nplurals=" + DEFAULT_PLURAL_FORM_COUNT +
                    "; plural=;" + BSLASHN_NL +
                "X-Generator: " + "\\n"
                );
    }
    
    public void updateHeaderInfo() {
        //TODO
    }
    
    public boolean isFuzzy() {
        for (int i = 0; i < flags.size(); i++)
            if (flags.get(i).toLowerCase().equals("fuzzy"))
                return true;
        return false;
    }
    
    public String getTranslatorComments() {
        return translatorComments;
    }
    
    public void addtoTranslatorComments(String postfix) {
        translatorComments += postfix;
    }
    
    public String getExtractedComments() {
        return extractedComments;
    }
    
    public void addtoExtractedComments(String postfix) {
        extractedComments += postfix;
    }
    
    public Vector<String> getReferences() {
        return reference;
    }
    
    public Vector<String> getFlags() {
        return flags;
    }
    
    public String getPreviousContext() {
        return previousContext;
    }
    
    public void addtoPreviousContext(String postfix) {
        previousContext += postfix;
    }
    
    public String getPreviousUntranslatedString() {
        return previousUntranslatedString;
    }
    
    public void addtoPreviousUntranslatedString(String postfix) {
        previousUntranslatedString += postfix;
    }
    
    public String getPreviousUntranslatedStringPlural() {
        return previousUntranslatedStringPlural;
    }
    
    public void addtoPreviousUntranslatedStringPlural(String postfix) {
        previousUntranslatedStringPlural += postfix;
    }
    
    public String getContext() {
        return context;
    }
    
    public void setContext(String context) {
        this.context = context;
    }
    
    public void addtoContext(String postfix) {
        context += postfix;
    }
    
    public String getUntranslatedString() {
        return untranslatedString;
    }
    
    public void setUntranslatedString(String untranslatedString) {
        this.untranslatedString = untranslatedString;
    }
    
    public void addtoUntranslatedString(String postfix) {
        untranslatedString += postfix;
    }
    
    public String getUntranslatedStringPlural() {
        return untranslatedStringPlural;
    }
    
    public void setUntranslatedStringPlural(String untranslatedStringPlural) {
        this.untranslatedStringPlural = untranslatedStringPlural;
    }
    
    public void addtoUntranslatedStringPlural(String postfix) {
        untranslatedStringPlural += postfix;
    }
    
    public Hashtable<Integer, String> getTranslatedString() {
        return translatedString;
    }
    
    public String getTranslatedString(int index) {
        return translatedString.get(index);
    }
    
    public void resetTranslatedString() {
        translatedString = new Hashtable<Integer, String>();
    }
    
    public String toString() {
        return "[" + untranslatedString + "|" + untranslatedStringPlural + "]";
    }
    
    private static boolean isEmpty(Vector<String> v) {
        if (v == null)
            return true;
        for (int i = 0; i < v.size(); i++)
            if (!isEmpty(v.get(i)))
                return false;
        return true;
    }
    
    private static boolean isEmpty(Hashtable<Integer, String> h) {
        if (h == null)
            return true;
        Enumeration<Integer> e = h.keys();
        while (e.hasMoreElements()) {
            Integer i = e.nextElement();
            if (!isEmpty(h.get(i)))
                return false;
        }
        return true;
    }
    
    private static boolean isEmpty(String s) {
        return (s == null || s.trim().equals(""));
    }
}

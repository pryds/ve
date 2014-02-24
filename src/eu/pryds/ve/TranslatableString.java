package eu.pryds.ve;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;

public class TranslatableString implements Parcelable {
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
    
    private final String BSLASHN_NL = "\\n\n";
    
    public int getHeaderPluralFormCount(Activity activity) {
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
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(activity);
        return Languages.getNplurals(pref.getString("pref_lang", Languages.DEFAULT_LANG_CODE));
    }
    
    public void initiateHeaderInfo(Activity activity) {
        translatedString = new Hashtable<Integer, String>();
        translatedString.put(0, "");
        updateHeaderInfo(activity);
    }
    
    public void updateHeaderInfo(Activity activity) {
        String version = "";
        try {
            version = activity.getPackageManager().getPackageInfo(
                    activity.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) { }
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(activity);
        
        String[] headerArray = translatedString.get(0).split("\n");
        Vector<String> headerLines = new Vector<String>(Arrays.asList(headerArray));
        
        //last entry might end in a backslash-n. If so, remove those two chars:
        for (int i = 0; i < headerLines.size(); i++) {
            if (headerLines.get(i).trim().endsWith("\\n")) {
                String item = headerLines.remove(i).trim();
                headerLines.add(i, item.substring(0, item.length()-2));
            }
        }
        
        replaceOrAddString(headerLines, "PO-Revision-Date:",
                (new SimpleDateFormat("yyyy-MM-dd HH:mmZ").format(new Date())));
        
        replaceOrAddString(headerLines, "Last-Translator:",
                pref.getString("pref_name", "") + " <" +
                pref.getString("pref_email", "") + ">");
        
        String langcode = pref.getString("pref_lang", Languages.DEFAULT_LANG_CODE);
        if (langcode == null || langcode.length() == 0)
            langcode = Languages.DEFAULT_LANG_CODE;
        
        replaceOrAddString(headerLines, "Language-Team:",
                Languages.getName(langcode) +
                " <" + pref.getString("pref_maillist", "") + ">");
        
        replaceOrAddString(headerLines, "Language:", langcode);
        
        replaceOrAddString(headerLines, "MIME-Version:", "1.0");
        
        replaceOrAddString(headerLines, "Content-Type:",
                "text/plain; charset=UTF-8");
        
        replaceOrAddString(headerLines, "Content-Transfer-Encoding:", "8bit");
        
        replaceOrAddString(headerLines, "Plural-Forms:",
                "nplurals=" + Languages.getNplurals(langcode) +
                "; plural=" + Languages.getPlural(langcode) + ";");
        
        replaceOrAddString(headerLines, "X-Generator:", "Vé " + version);
        
        translatedString.put(0, implode(headerLines, BSLASHN_NL) + "\\n");
        
        
        headerArray = translatorComments.split("\n");
        headerLines = new Vector<String>(Arrays.asList(headerArray));
        
        int lineMatchingTranslatorInfo = -1;
        for (int i = 0; i < headerLines.size(); i++) {
            if (headerLines.get(i).matches(
                    pref.getString("pref_name", "") + " *<" +
                    pref.getString("pref_email", "") + ">.*"
                    )) {
                lineMatchingTranslatorInfo = i;
                break;
            }
        }
        String thisYear = new SimpleDateFormat("yyyy").format(new Date());
        if (lineMatchingTranslatorInfo == -1) {
            headerLines.add(pref.getString("pref_name", "") + " <" +
                pref.getString("pref_email", "") + ">, " + thisYear + ".");
        } else {
            String fullStr = headerLines.get(lineMatchingTranslatorInfo);
            String yearsStr = fullStr.substring(fullStr.indexOf(">, ")+3, fullStr.length()-1);
            String[] years = yearsStr.trim().split(",");
            boolean yearIsAlreadyThere = false;
            for (int i = 0; i < years.length; i++) {
                
                if (years[i].trim().equals(thisYear)) {
                    yearIsAlreadyThere = true;
                    break;
                }
            }
            if (!yearIsAlreadyThere) {
                yearsStr = yearsStr.trim() + ", " + thisYear;
                headerLines.remove(lineMatchingTranslatorInfo);
                headerLines.add(lineMatchingTranslatorInfo,
                        pref.getString("pref_name", "") + " <" +
                        pref.getString("pref_email", "") + ">, " +
                        yearsStr + ".");
            }
        }
        translatorComments = implode(headerLines, "\n");
    }
    
    private void replaceOrAddString(Vector<String> strings, String strHeader,
            String strContent) {
        int index = -1;
        int l = strings.size();
        for (int i = 0; i < l; i++) {
            if (strings.get(i).startsWith(strHeader)) {
                index = i;
                break;
            }
        }
        if (index != -1)
            strings.remove(index);
        strings.add(index == -1 ? strings.size() : index,
                strHeader + " " + strContent);
    }
    
    public boolean isFuzzy() {
        for (int i = 0; i < flags.size(); i++)
            if (flags.get(i).toLowerCase().equals("fuzzy"))
                return true;
        return false;
    }
    
    public void setFuzzy(boolean fuzzy) {
        if (fuzzy) {
            if (!isFuzzy()) {
                flags.add("fuzzy");
            }
        } else {
            removeFlag("fuzzy");
        }
    }
    
    public boolean isUntranslated() {
        if (translatedString == null)
            return true;
        
        for (int i = 0; i < translatedString.size(); i++) {
            if (translatedString.get(i).trim().length() > 0)
                return false;
        }
        return true;
    }
    
    public boolean needsWork() {
        return isUntranslated() || isFuzzy();
    }
    
    public boolean isPluralString() {
        return !(untranslatedStringPlural == null ||
                untranslatedStringPlural.equals(""));
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
    
    public String getReferencesAsString() {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < reference.size(); i++) {
            if (i != 0)
                str.append("\n");
            str.append(reference.get(i));
        }
        return str.toString();
    }
    
    public Vector<String> getFlags() {
        return flags;
    }
    
    public void removeFlag(String flag) {
        flag = flag.trim();
        for (int i = 0; i < flags.size(); i++) {
            if (flags.get(i).equals(flag)) {
                flags.remove(i);
                return;
            }
        }
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
    
    public void setTranslatedString(int index, String string) {
        if (translatedString == null)
            resetTranslatedString();
        
        translatedString.put(index, string);
    }
    
    public void resetTranslatedString() {
        translatedString = new Hashtable<Integer, String>();
    }
    
    public String toString() {
        return "[" + untranslatedString + "|" + untranslatedStringPlural + "]";
    }
    
    private static String implode(Vector<String> array, String separator) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < array.size(); i++) {
            if (str.length() != 0)
                str.append(separator);
            str.append(array.get(i));
        }
        return str.toString();
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
    
    // Parcelable stuff
    
    private TranslatableString(Parcel in) {
        translatorComments = in.readString();
        extractedComments = in.readString();
        reference = (Vector<String>) in.readSerializable();
        flags = (Vector<String>) in.readSerializable();
        previousContext = in.readString();
        previousUntranslatedString = in.readString();
        previousUntranslatedStringPlural = in.readString();
        context = in.readString();
        untranslatedString = in.readString();
        untranslatedStringPlural = in.readString();
        translatedString = (Hashtable<Integer, String>) in.readSerializable();
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel parcel, int parcelflags) {
        parcel.writeString(translatorComments);
        parcel.writeString(extractedComments);
        parcel.writeSerializable(reference);
        parcel.writeSerializable(flags);
        parcel.writeString(previousContext);
        parcel.writeString(previousUntranslatedString);
        parcel.writeString(previousUntranslatedStringPlural);
        parcel.writeString(context);
        parcel.writeString(untranslatedString);
        parcel.writeString(untranslatedStringPlural);
        parcel.writeSerializable(translatedString);
    }
    
    public static final Parcelable.Creator<TranslatableString> CREATOR =
            new Parcelable.Creator<TranslatableString>() {
        public TranslatableString createFromParcel(Parcel in) {
            return new TranslatableString(in);
        }
        public TranslatableString[] newArray(int size) {
            return new TranslatableString[size];
        }
    };
}

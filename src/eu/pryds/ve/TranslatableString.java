package eu.pryds.ve;

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
	
	public void resetTranslatedString() {
		translatedString = new Hashtable<Integer, String>();
	}
}

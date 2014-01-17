package eu.pryds.ve;

import android.content.Context;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.util.AttributeSet;

public class EditTextPreferenceShowValue extends EditTextPreference {
    public EditTextPreferenceShowValue(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }
    
    public EditTextPreferenceShowValue(Context context) {
        super(context);
        init();
    }
    
    private void init() {
        setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary(getText());
                return true;
            }
        });
    }
    
    @Override
    public CharSequence getSummary() {
        // if something is filled in, show that; otherwise get summary from strings.xml
        CharSequence summaryText;
        if (super.getText().trim().length() == 0) {
            if (super.getKey().equals("pref_name")) {
                summaryText = getContext().getResources().getText(R.string.pref_name_summ);
            } else if (super.getKey().equals("pref_email")) {
                summaryText = getContext().getResources().getText(R.string.pref_email_summ);
            } else if (super.getKey().equals("pref_lang")) {
                summaryText = getContext().getResources().getText(R.string.pref_lang_summ);
            } else if (super.getKey().equals("pref_maillist")) {
                summaryText = getContext().getResources().getText(R.string.pref_maillist_summ);
            } else {
                summaryText = "";
            }
        } else {
            summaryText = getText();
        }
        
        return summaryText;
    }
}

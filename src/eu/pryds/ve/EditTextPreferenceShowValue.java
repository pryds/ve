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
        return (super.getText().trim().length() == 0 ?
                getContext().getResources().getText(R.string.pref_name_summ) :
                super.getText() );
    }
}

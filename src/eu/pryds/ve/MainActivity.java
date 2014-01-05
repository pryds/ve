package eu.pryds.ve;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity {
    
    private TranslatableStringCollection str;
    private int currentString = 0;
    private int currentPluralForm = 0;
    private Menu menu;
    public final static int CHOOSE_FILE_REQUEST = 1;
    public final static String CHOOSE_FILE_MESSAGE = "eu.pryds.ve.choosefile";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        
        Switch approved = (Switch) findViewById(R.id.approved);
        approved.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (str != null) {
                    TranslatableString currStr = str.getString(currentString);
                    currStr.setFuzzy(!isChecked);
                    updateMetadata();
                }
            }
        });
        
        TextView origStr = (TextView) findViewById(R.id.orig_str);
        origStr.setMovementMethod(new ScrollingMovementMethod()); //make scrollable
        
        final EditText translStr = (EditText) findViewById(R.id.transl_str);
        translStr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
               String changedText = translStr.getText().toString();
               if (str != null) {
                   TranslatableString currStr = str.getString(currentString);
                   currStr.setTranslatedString(currentPluralForm, changedText);
                   updateMetadata();
               }
            }
        });
        
        TextView metadata = (TextView) findViewById(R.id.metadata);
        metadata.setMovementMethod(new ScrollingMovementMethod());
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable("str", str);
        savedInstanceState.putInt("currentString", currentString);
        savedInstanceState.putInt("currentPluralForm", currentPluralForm);
        
        super.onSaveInstanceState(savedInstanceState);
    }
    
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        
        if (savedInstanceState != null) {
            str = (TranslatableStringCollection) savedInstanceState.getParcelable("str");
            currentString = savedInstanceState.getInt("currentString");
            currentPluralForm = savedInstanceState.getInt("currentPluralForm");
            
            updateScreen();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        
        if (str != null)
            enableInitiallyDisabledViews(true);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
        case R.id.action_previous:
            currentString--;
            if (currentString < 0)
                currentString = str.size() - 1;
            currentPluralForm = 0;
            updateScreen();
            return true;
        case R.id.action_next:
            currentString++;
            if (currentString >= str.size())
                currentString = 0;
            currentPluralForm = 0;
            updateScreen();
            return true;
        case R.id.action_settings:
            openSettings();
            return true;
        case R.id.action_about:
            //show about dialog
            AboutDialogFragment about = new AboutDialogFragment();
            about.show(getFragmentManager(), "AboutFragment");
            return true;
        case R.id.action_load:
            str = new TranslatableStringCollection();
            
            Intent intent = new Intent(this, FileChooser.class);
            startActivityForResult(intent, CHOOSE_FILE_REQUEST);
            
            return true;
        case R.id.action_save:
            String[] poLines = str.toPoFile(this);
            
            //TODO: the following is a temporary hack to show resulting file
            StringBuffer temp = new StringBuffer();
            for (int i = 0; i < poLines.length; i++)
                temp.append(poLines[i] + '\n');
            TextView origStr = (TextView) findViewById(R.id.orig_str);
            origStr.setText(temp);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_FILE_REQUEST) {
            if (resultCode == RESULT_OK) {
                String filePath = data.getStringExtra(CHOOSE_FILE_MESSAGE);
                
                File file = new File(filePath);
                if (!file.exists() || !file.canRead()) {
                    int errorMsg = (!file.exists()
                            ? R.string.file_notexist : R.string.file_notreadable);
                    new AlertDialog.Builder(this)
                        .setTitle(R.string.error)
                        .setMessage(errorMsg)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
                    return;
                }
                str.parse(file, this); // TODO: In a separate thread
                //TODO: Abort and warn if not a (proper) po file
                updateScreen();
                
                enableInitiallyDisabledViews(true);
            }
        }
        /*
        
        */
    }
    
    /** Called when the user touches one of the plural form buttons */
    public void changePluralForm(View view) {
        switch (view.getId()) {
        case R.id.plural0:
            currentPluralForm = 0;
            updateScreen();
            return;
        case R.id.plural1:
            currentPluralForm = 1;
            updateScreen();
            return;
        case R.id.plural2:
            currentPluralForm = 2;
            updateScreen();
            return;
        case R.id.plural3:
            currentPluralForm = 3;
            updateScreen();
            return;
        case R.id.plural4:
            currentPluralForm = 4;
            updateScreen();
            return;
        case R.id.plural5:
            currentPluralForm = 5;
            updateScreen();
            return;
        default:
            return;
        }
    }
    
    private void updateScreen() {
        if (str == null)
            return;
        TranslatableString currentStr = str.getString(currentString);
        
        Switch approved = (Switch) findViewById(R.id.approved);
        approved.setChecked(!currentStr.isFuzzy());
        
        Button[] plural = new Button[] {
                (Button) findViewById(R.id.plural0),
                (Button) findViewById(R.id.plural1),
                (Button) findViewById(R.id.plural2),
                (Button) findViewById(R.id.plural3),
                (Button) findViewById(R.id.plural4),
                (Button) findViewById(R.id.plural5)
        };
        
        int pluralForms = str.getHeader().getHeaderPluralFormCount(this);
        
        for (int i = 0; i < plural.length; i++) {
            if (i < pluralForms && currentStr.isPluralString())
                plural[i].setVisibility(Button.VISIBLE);
            else
                plural[i].setVisibility(Button.GONE);
        }
        
        TextView origStr = (TextView) findViewById(R.id.orig_str);
        origStr.setText(currentPluralForm > 0 ?
                currentStr.getUntranslatedStringPlural() :
                currentStr.getUntranslatedString()
                );
        
        EditText translString = (EditText) findViewById(R.id.transl_str);
        translString.setText(currentStr.getTranslatedString(currentPluralForm));
        
        updateMetadata();
    }
    
    private void updateMetadata() {
        TranslatableString currentStr = str.getString(currentString);
        TextView metadata = (TextView) findViewById(R.id.metadata);
        int fuzzyCount = str.countFuzzyStrings();
        int untransCount = str.countUntranslatedStrings();
        
        metadata.setText(
                getResources().getText(R.string.meta_str_no) + " " +
                (currentString+1) + "/" + str.size() +
                
                " (" +
                getResources().getQuantityString(R.plurals.meta_not_ready, fuzzyCount, fuzzyCount) +
                " " +
                getResources().getQuantityString(R.plurals.meta_untranslated, untransCount, untransCount) +
                ")" + '\n' +
                
                getResources().getText(R.string.meta_context) + " " +
                (currentStr.getContext().equals("") ?
                "" : currentStr.getContext()) + '\n' +
                
                getResources().getText(R.string.meta_notes) + " " +
                (currentStr.getTranslatorComments().equals("") ?
                "" : currentStr.getTranslatorComments() + '\n') +
                (currentStr.getExtractedComments().equals("") ?
                "" : currentStr.getExtractedComments()) + '\n' +
                
                getResources().getText(R.string.meta_files) + " " +
                (currentStr.getReferences().size() == 0 ?
                "" : currentStr.getReferencesAsString()) + '\n'
                );
    }
    
    private void enableInitiallyDisabledViews(boolean enable) {
        MenuItem actionPrev = menu.findItem(R.id.action_previous);
        actionPrev.setEnabled(enable);
        
        MenuItem actionNext = menu.findItem(R.id.action_next);
        actionNext.setEnabled(enable);
        
        MenuItem actionSave = menu.findItem(R.id.action_save);
        actionSave.setEnabled(enable);
        
        Switch approvedSwitch = (Switch) findViewById(R.id.approved);
        approvedSwitch.setEnabled(enable);
        
        TextView origStr = (TextView) findViewById(R.id.orig_str);
        origStr.setEnabled(enable);
        
        EditText translStr = (EditText) findViewById(R.id.transl_str);
        translStr.setEnabled(enable);
    }
    
    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    
}

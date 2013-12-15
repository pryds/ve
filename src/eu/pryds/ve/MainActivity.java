package eu.pryds.ve;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity {
    
    private TranslatableStringCollection str;
    private Menu menu;
    private int currentString = 0;
    private int currentPluralForm = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        
        TextView origStr = (TextView) findViewById(R.id.orig_str);
        origStr.setMovementMethod(new ScrollingMovementMethod()); //make scrollable
        
        TextView metadata = (TextView) findViewById(R.id.metadata);
        metadata.setMovementMethod(new ScrollingMovementMethod());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
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
            File sdDir = Environment.getExternalStorageDirectory();
            File file = new File(sdDir, "test.po"); // TODO: File chooser
            str.parse(file, this); // TODO: In a separate thread
            
            updateScreen();
            
            enableInitiallyDisabledViews(true);
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
        
        TextView metadata = (TextView) findViewById(R.id.metadata);
        metadata.setText(
                getResources().getText(R.string.str_no) + " " +
                (currentString+1) + "/" + str.size() + '\n' +
                
                getResources().getText(R.string.context) + " " +
                (currentStr.getContext().equals("") ?
                "" : currentStr.getContext()) + '\n' +
                
                getResources().getText(R.string.notes) + " " +
                (currentStr.getTranslatorComments().equals("") ?
                "" : currentStr.getTranslatorComments() + '\n') +
                (currentStr.getExtractedComments().equals("") ?
                "" : currentStr.getExtractedComments()) + '\n' +
                
                getResources().getText(R.string.files) + " " +
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

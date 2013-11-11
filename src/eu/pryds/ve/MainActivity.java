package eu.pryds.ve;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity {
    
    private TranslatableStringCollection str;
    private Menu menu;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
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
            //System.out.println("Previous button pressed");
            return true;
        case R.id.action_next:
            //System.out.println("Next button pressed");
            return true;
        case R.id.action_settings:
            openSettings();
            return true;
        case R.id.action_load:
            str = new TranslatableStringCollection();
            File sdDir = Environment.getExternalStorageDirectory();
            File file = new File(sdDir, "test.po"); // TODO: File chooser
            str.parse(file); // TODO: In a separate thread
            
            updateScreen(1); // TODO: Show first item that is not header info
            
            enableInitiallyDisabledViews();
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void updateScreen(int stringIndex) {
        TranslatableString currentStr = str.getString(stringIndex);
        
        Switch approved = (Switch) findViewById(R.id.approved);
        approved.setChecked(currentStr.isFuzzy());
        
        TextView origStr = (TextView) findViewById(R.id.orig_str);
        origStr.setText(currentStr.getUntranslatedString());
        
        EditText translString = (EditText) findViewById(R.id.transl_str);
        translString.setText(currentStr.getTranslatedString(0)); //TODO: Handle plural forms
        
        // TODO: Update metadata view
    }
    
    private void enableInitiallyDisabledViews() {
        MenuItem actionPrev = menu.findItem(R.id.action_previous);
        actionPrev.setEnabled(true);
        
        MenuItem actionNext = menu.findItem(R.id.action_next);
        actionNext.setEnabled(true);
        
        MenuItem actionSave = menu.findItem(R.id.action_save);
        actionSave.setEnabled(true);
        
        Switch approvedSwitch = (Switch) findViewById(R.id.approved);
        approvedSwitch.setEnabled(true);
        
        TextView origStr = (TextView) findViewById(R.id.orig_str);
        origStr.setEnabled(true);
        
        EditText translStr = (EditText) findViewById(R.id.transl_str);
        translStr.setEnabled(true);
    }
    
    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    
}

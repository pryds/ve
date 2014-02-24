package eu.pryds.ve;

import android.app.backup.BackupManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
	
	@Override
    public void onStop() {
        super.onStop();
        
        // Request backup, data might have changed.
        BackupManager bm = new BackupManager(getActivity());
        bm.dataChanged();
    }
}

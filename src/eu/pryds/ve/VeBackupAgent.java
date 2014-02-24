package eu.pryds.ve;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

public class VeBackupAgent extends BackupAgentHelper {
    private static final String DEFAULT_PREFS_NAME = "eu.pryds.ve_preferences";
    
    public void onCreate() {
        SharedPreferencesBackupHelper helper =
                new SharedPreferencesBackupHelper(this, DEFAULT_PREFS_NAME);
        addHelper(DEFAULT_PREFS_NAME, helper);
    }
}

package eu.pryds.ve;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

public class AboutDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String version = "";
        try {
            version = getActivity().getPackageManager().getPackageInfo(
                    getActivity().getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) { }
        
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.about_title)
               .setMessage(String.format(getString(R.string.about_version), version) + "\n" +
                       getString(R.string.about_author) + "\n" +
                       (getString(R.string.locale_of_this_translation).equals("en") ?
                               "" : getString(R.string.about_translate_credits) + "\n" ) +
                       "\n" + getString(R.string.about_license) )
               .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // no action, just close the dialog
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}

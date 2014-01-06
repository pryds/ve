package eu.pryds.ve;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class FileChooser extends ListActivity {
    
    protected File[] fileList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_file_chooser);
        
        File sdDir = Environment.getExternalStorageDirectory();
        updateFileList(sdDir);
        
        ListView lw = getListView();
        lw.setTextFilterEnabled(true);
        lw.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (fileList[position].isDirectory()) {
                    if (!fileList[position].canRead()) {
                        Toast.makeText(getApplicationContext(),
                                getResources().getText(R.string.file_cannotreaddir) +
                                "\n" + fileList[position], Toast.LENGTH_LONG).show();
                    } else {
                        updateFileList(fileList[position]);
                    }
                } else {
                    if (!fileList[position].canRead()) {
                        Toast.makeText(getApplicationContext(),
                                getResources().getText(R.string.file_cannotreadfile) +
                                "\n" + fileList[position], Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "" + fileList[position], Toast.LENGTH_LONG).show();
                        
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(MainActivity.CHOOSE_FILE_MESSAGE, fileList[position].getAbsolutePath());
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                }
            }
        });
    }
    
    private void updateFileList(File dir) {
        fileList = listFilesIncludingParentDir(dir);
        String[] fileListStr = new String[fileList.length];
        
        for (int i = 0; i < fileListStr.length; i++) {
            if (i == 0 && dir.getParent() != null &&
                    fileList[i].getAbsolutePath().equals(dir.getParentFile().getAbsolutePath())) {
                fileListStr[i] = "" + getResources().getText(R.string.file_parent_dir);
            } else {
                fileListStr[i] = fileList[i].getName() +
                        (fileList[i].isDirectory() ?
                        " " + getResources().getText(R.string.file_directory_postfix) :
                        "");
            }
        }
        
        setListAdapter(new ArrayAdapter<String>(this,
                R.layout.activity_file_chooser, fileListStr));
        
        Toast.makeText(getApplicationContext(),
                getResources().getText(R.string.file_current_dir) +
                "\n" + dir, Toast.LENGTH_SHORT).show();
    }
    
    private File[] listFilesIncludingParentDir(File dir) {
        File parent = dir.getParentFile();
        File[] dirList = dir.listFiles();
        if (parent == null) {
            return dirList;
        } else {
            File[] dirListInclParent = new File[dirList.length + 1];
            dirListInclParent[0] = parent;
            for (int i = 0; i < dirList.length; i++) {
                dirListInclParent[i+1] = dirList[i];
            }
            return dirListInclParent;
        }
    }
}

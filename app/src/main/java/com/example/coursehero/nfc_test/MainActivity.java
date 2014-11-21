package com.example.coursehero.nfc_test;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ListActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.net.Uri;
import android.content.Intent;
import java.util.ArrayList;

import java.io.File;
import java.util.List;

public class MainActivity extends Activity {
    NfcAdapter mNfcAdapter;
    private Uri[] mFileUris = new Uri[10];
    private Intent mIntent;
    // Instance that returns available files from this app
    private FileUriCallback mFileUriCallback;
    private Uri fileUri;
    private String mParentPath;
    private File[] files;
    private FileObserver observer;

    /**
     * Callback that Android Beam file transfer calls to get
     * files to share
     */
    private class FileUriCallback implements
            NfcAdapter.CreateBeamUrisCallback {
        public FileUriCallback() {

        }
        /**
         * Create content URIs as needed to share with another device
         */
        @Override
        public Uri[] createBeamUris(NfcEvent event) {
            return mFileUris;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);

        // Now create a new list adapter bound to the cursor.
        // SimpleListAdapter is designed for binding to a Cursor.
        final ListView listview = (ListView) findViewById(R.id.listview);
        files = getFiles();
        final DocumentAdapter adapter = new DocumentAdapter(this, files);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                transferClickedImage(position);
            }

        });
        observer = new FileObserver(Environment.getExternalStoragePublicDirectory("beam").toString()) { // set up a file observer to watch this directory on sd card

            @Override
            public void onEvent(int event, String file) {
                Log.d("int", "Something changed");
                files = getFiles();
                final BaseAdapter adp = (BaseAdapter) listview.getAdapter();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("blah", "Am I notifying adp of the change?");
                        adp.notifyDataSetChanged();
                    }
                });



            }
        };
        observer.startWatching();


    }

    private File[] getFiles() {
        ArrayList<File> returnFiles = new ArrayList<File>();
        File fpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File[] files = fpath.listFiles();
        for (File file : files) {
            if (!returnFiles.contains(file) && file.getName().startsWith("thumbnail")) {
                returnFiles.add(file);
            }
        }

        File fpath2 = Environment.getExternalStoragePublicDirectory("beam");
        Log.d("int", "F path 2's absolute path " + fpath2.getAbsolutePath());
        File[] files2 = fpath2.listFiles();
        if (files2 != null) {
            for (File file : files2) {
                if (!returnFiles.contains(file) && file.getName().startsWith("thumbnail")) {
                    returnFiles.add(file);
                }
                Log.d("int", "These are the files " + file.getName());
            }
        }

        File[] arrFile = new File[returnFiles.size()];
        arrFile = returnFiles.toArray(arrFile);

        return arrFile;

    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.d("int", "Resuming...");
//        // Check to see that the Activity started due to an Android Beam
//        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
//            Log.d("int", "Handling view intent");
//            handleViewIntent();
//        }
//    }
//
//    @Override
//    public void onNewIntent(Intent intent) {
//        Log.d("int", "I am here in new intent?");
//        setIntent(intent);
//
//        //handleViewIntent();
//    }
//
    public void transferClickedImage(int position) {
         /*
         * Create a list of URIs, get a File,
         * and set its permissions
         */
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (!mNfcAdapter.isEnabled()) {
            Log.d("int", "Nfc is not enabled");
        } else if (!mNfcAdapter.isNdefPushEnabled()) {
            Log.d("int", "Beam is not enabled");
        } else {
            Log.d("int", "Everything is enabled");
        }
        File requestFile = files[position];
        requestFile.setReadable(true, false);
        Log.d("int", "Tried to beam file " + requestFile.getPath());
        mNfcAdapter.setBeamPushUris(new Uri[]{Uri.fromFile(requestFile)}, this);

    }
//
//    private void handleViewIntent() {
//        // Get the Intent action
//        mIntent = getIntent();
//        String action = mIntent.getAction();
//        Log.d("int", "This is action " + action);
//        /*
//         * For ACTION_VIEW, the Activity is being asked to display data.
//         * Get the URI.
//         */
//        if (TextUtils.equals(action, Intent.ACTION_VIEW)) {
//            // Get the URI from the Intent
//            Uri beamUri = mIntent.getData();
//            /*
//             * Test for the type of URI, by getting its scheme value
//             */
//            if (TextUtils.equals(beamUri.getScheme(), "file")) {
//                mParentPath = handleFileUri(beamUri);
//                Log.d("int", "Path is file!");
//            }
//            Log.d("int", "Outside of file equals scheme " + mParentPath);
//        }
//    }

//    public String handleFileUri(Uri beamUri) {
//        // Get the path part of the URI
//        String fileName = beamUri.getPath();
//        // Create a File object for this filename
//        File copiedFile = new File(fileName);
//        // Get a string containing the file's parent directory
//        Log.d("int", "copied file " + copiedFile.getParent());
//        Log.d("int", "filename " + fileName);
//        return copiedFile.getParent();
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

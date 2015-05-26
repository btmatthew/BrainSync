package com.example.anonymous.brainsync;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;
import com.note.anonymous.brainsync.R;
import com.note.anonymous.brainsync.Settings;


public class GoogleDrive extends FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private final int RESOLVE_CONNECTION_REQUEST_CODE = 1;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    private static final int REQUEST_CODE_RESOLUTION = 3;
    private Bitmap mBitmapToSave;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          //  setContentView(R.layout.activity_google_drive);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        onStart();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        Log.d("G", "I got here too!");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("G", "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            Log.i("G", "1st");
            // show the localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an
        // authorization
        // dialog is displayed to the user.
        try {
            Log.i("G", "2nd");
            result.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
            return;
        } catch (IntentSender.SendIntentException e) {
            Log.e("G", "Exception while starting resolution activity", e);
        }

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        Log.d("G", "OnActivity Called!");
        switch (requestCode) {
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                } else if (resultCode == RESULT_CANCELED) {
                    new AlertDialog.Builder(this)
                            .setTitle("Important Stuff!")
                            .setMessage("Do you want to cancel authorization?")
                            .setNegativeButton("No, Go Back!", null)
                            .setPositiveButton("Yes, Please!", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    Intent intent = new Intent(GoogleDrive.this, Settings.class);
                                    startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                }
                            }).create().show();
                        break;
                    }

                }

        }



    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_google_drive, menu);
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


        @Override
        public void onConnected(Bundle connectionHint) {
            Log.i("G", "API client connected.");

            IntentSender i = Drive.DriveApi
                    .newOpenFileActivityBuilder()
                    .build(mGoogleApiClient);
            try {
                startIntentSenderForResult(i, REQUEST_CODE_RESOLUTION, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }




            Log.d("G", "I got here!");


       }



    @Override
    public void onConnectionSuspended(int i) {

    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode, this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((GoogleDrive)getActivity()).onDialogDismissed();
        }
    }


}


//}




//}        if (connectionResult.hasResolution()) {
//            try {
//                connectionResult.startResolutionForResult(this, connectionResult.getErrorCode());
//                Toast.makeText(this, "Connection Failed", Toast.LENGTH_LONG).show();
//              //  onStart();
//            } catch (IntentSender.SendIntentException e) {
//
//
//            }
//        } else {
//            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
//        }
 //   }



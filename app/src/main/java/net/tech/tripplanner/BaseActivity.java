package net.tech.tripplanner;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

/**
 * Created by ashwini on 4/9/2017.
 */

public class BaseActivity extends AppCompatActivity {
    private final int REQUEST_CODE_ASK_PERMISSIONS = 10111;
    private final String TAG = getClass().getSimpleName();
    private final String[] permissionList = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };


    protected void requestPermission() {

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            createPermissonAskDialog(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(BaseActivity.this, permissionList,
                            REQUEST_CODE_ASK_PERMISSIONS);
                }
            });

        } else
            ActivityCompat.requestPermissions(BaseActivity.this, permissionList,
                    REQUEST_CODE_ASK_PERMISSIONS);

    }


    protected boolean checkPermissionStatus() {
        Boolean hasPermission = true;
        for (String permission : permissionList) {
            hasPermission = hasPermission && (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED);
            Log.e(TAG, "hasPermission " + hasPermission);
        }
        Log.e(TAG, "hasPermission " + hasPermission);
        return hasPermission;
    }


    private void createPermissonAskDialog(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage("Application needs Location permission to access your current location !!")
                .setPositiveButton(android.R.string.ok, okListener)
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            permissionDenied(grantResults);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    protected void permissionDenied(@NonNull int[] grantResults) {
    }

    protected  void openAppPermissionScreen(View view){
        Snackbar.make(view,
                "location service denied",
                Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(Color.YELLOW)
                .setAction("SETTING", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).show();
    }
}

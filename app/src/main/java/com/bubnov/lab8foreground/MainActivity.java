package com.bubnov.lab8foreground;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationManagerCompat;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

import java.net.InetSocketAddress;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //checks if service is running or not
        ConstraintLayout contained = findViewById(R.id.myConstraintLayout);
        if(isMyServiceRunning(ForegroundService.class)){
            findViewById(R.id.btnLaunchService).setEnabled(false);
            Snackbar snackbar = Snackbar.make(contained, R.string.stop, Snackbar.LENGTH_INDEFINITE)
                    .setAction("STOP", view -> stopMyService());
            snackbar.show();
        }else{
            Log.i("MainActivity: ", "Foreground service not running");
            findViewById(R.id.btnLaunchService).setEnabled(true);
        }

        //check if notification are enabled or not, if not ask to enable them
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        boolean areNotificationsEnabled = notificationManagerCompat.areNotificationsEnabled();
        if(!areNotificationsEnabled){
            Snackbar snackbar = Snackbar.make(contained, R.string.enabled, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Enable", view -> openNotificationSettingForApp());
            snackbar.show();
        }

        findViewById(R.id.btnLaunchService).setOnClickListener(view ->{
            EditText input = findViewById(R.id.etInputText);
            String text = input.getText().toString();
            Intent foregroundStart = new Intent(this, ForegroundService.class);
            foregroundStart.putExtra("input", text);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                startForegroundService(foregroundStart);
            }else startService(foregroundStart);
        });
    }

    //method that opens the app notification system
    private void openNotificationSettingForApp(){
        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.putExtra("app_package", getPackageName());
        intent.putExtra("app_uid", getApplicationInfo());
        intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
        startActivity(intent);
    }

    //method to stop the foreground service
    private void stopMyService() {
        Intent stopMyForegroundService = new Intent(this, ForegroundService.class);
        stopService(stopMyForegroundService);
        finish();
    }

    private boolean isMyServiceRunning(Class<ForegroundService> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo info : manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(info.service.getClassName())){
                return true;
            }
        }
        return false;
    }
}
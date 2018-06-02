package com.example.hush;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private static final int REQUEST_CODE_OVERLAY_PERMISSION = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SeekBar sensitivity = findViewById(R.id.seekbar_sensitivity);
        sensitivity.setProgress(AppSettings.getSensitivity());
        sensitivity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                AppSettings.setSensitivity(seekBar.getProgress());
                Log.d(TAG, "sensitivity changed: "+seekBar.getProgress());
            }
        });

        SeekBar forgiveness = findViewById(R.id.seekbar_forgiveness);
        forgiveness.setProgress(AppSettings.getForgiveness());
        forgiveness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                AppSettings.setForgiveness(seekBar.getProgress());
                Log.d(TAG, "forgiveness changed: "+seekBar.getProgress());
            }
        });

        SeekBar cooldown = findViewById(R.id.seekbar_cooldown);
        cooldown.setProgress(AppSettings.getCooldown());
        cooldown.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                AppSettings.setCooldown(seekBar.getProgress());
                Log.d(TAG, "cooldown changed: "+seekBar.getProgress());
            }
        });

        findViewById(R.id.button_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToStartService();
            }
        });

        findViewById(R.id.button_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(SettingsActivity.this, SoundMeterService.class));
            }
        });
    }

    private void tryToStartService() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        } else {
            startService(new Intent(this, SoundMeterService.class));
            Toast.makeText(this, "Service started!", Toast.LENGTH_SHORT).show();
        }

        // overlay permissions are not automatically granted on 6.0 and up
        if (needOverlayPermission()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION);
        }
    }

    private boolean needOverlayPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            tryToStartService();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION) {
            if (needOverlayPermission()) {
                Toast.makeText(this, "You must grant the overlay permission!", Toast.LENGTH_LONG).show();
            } else {
                tryToStartService();
            }
        } else {
            Log.wtf(TAG, "unexpected result! check your code!");
        }
    }
}

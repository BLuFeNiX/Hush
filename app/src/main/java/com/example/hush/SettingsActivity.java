package com.example.hush;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

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

        findViewById(R.id.button_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToStartService();
            }
        });
    }

    private void tryToStartService() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        } else {
            startService(new Intent(this, SoundMeterService.class));
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startService(new Intent(this, SoundMeterService.class));
            finish();
        }
    }
}

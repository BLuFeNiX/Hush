package com.example.hush;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class SoundMeterService extends Service {

    private static final String TAG = SoundMeterService.class.getSimpleName();

    private MediaRecorder recorder = null;

    HandlerThread handlerThread = new HandlerThread(SoundMeterService.class.getSimpleName()+"-Thread");
    Handler handler;
    Runnable soundCheck = new Runnable() {
        @Override
        public void run() {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SoundMeterService.this, "amp: "+recorder.getMaxAmplitude(), Toast.LENGTH_SHORT).show();
                }
            });
            handler.postDelayed(this, 1000);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // not a bound service
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (recorder == null) {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            recorder.setOutputFile("/dev/null");
            try {
                recorder.prepare();
            } catch (IOException e) {
                Log.e(TAG, "whoops!", e);
            }
            recorder.start();

            handlerThread.start();
            handler = new Handler(handlerThread.getLooper());
            handler.post(soundCheck);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
        }

        if (handlerThread != null) {
            handler.removeCallbacks(soundCheck);
            handlerThread.quitSafely();
        }
    }

}

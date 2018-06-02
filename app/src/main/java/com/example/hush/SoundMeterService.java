package com.example.hush;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SoundMeterService extends Service {

    private static final String TAG = SoundMeterService.class.getSimpleName();
    private static final String NOTIFICATION_CHANNEL_ID_SERVICE = "my_notification_channel_id";

    private MediaRecorder recorder = null;

    private RunningAverage avgAmp = new RunningAverage(6000);

    HandlerThread handlerThread = new HandlerThread(SoundMeterService.class.getSimpleName()+"-Thread");
    Handler handler;
    Runnable soundCheck = new Runnable() {
        @Override
        public void run() {
            int amp = recorder.getMaxAmplitude(); // highest possible value is 32767
            int avg = avgAmp.add(amp);
            int adjustedAmp = amp-avg;

            Log.d(TAG, "avg: "+avg + ", amp: "+amp + ", diff: "+(amp-avg));

            if (adjustedAmp >= threshold) {
                Log.d(TAG, "Hush!");
            }

            handler.postDelayed(this, 100);
        }
    };

    /**
     * how high the amplitude needs to be before we care
     * should be from 0 to 32767
     */
    int threshold = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // not a bound service
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate!!!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand!!!");

        threshold = (int) (32767 - (double) AppSettings.getSensitivity() / 100 * 32767);
        Log.d(TAG, "threshold is now: "+threshold);

        if (recorder == null) {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            recorder.setMaxDuration(0);
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

        startForeground(1, buildForegroundNotification());

        return START_STICKY;
    }

    private Notification buildForegroundNotification() {
        Notification.Builder b = new Notification.Builder(this)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentTitle("Hush!")
                .setContentText("Measuring microphone amplitude...")
                .setSmallIcon(android.R.drawable.stat_sys_speakerphone)
                .setTicker("what's a ticker?")
                .setAutoCancel(true)
                .setVisibility(Notification.VISIBILITY_PUBLIC);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID_SERVICE, "App Service", NotificationManager.IMPORTANCE_DEFAULT));
            b.setChannelId(NOTIFICATION_CHANNEL_ID_SERVICE);
        }

        return b.build();
    }

    @Override
    public void onDestroy() {

        Log.d(TAG, "onDestroy!!!");

        if (recorder != null) {
            recorder.stop();
            recorder.release();
        }

        if (handlerThread != null) {
            handler.removeCallbacks(soundCheck);
            handlerThread.quitSafely();
        }
    }

    private class RunningAverage {

        private final int historySize;
        private final List<Integer> values = new ArrayList<>();

        private int index;

        RunningAverage(int historySize) {
            if (historySize < 1) throw new IllegalArgumentException("must track at least 1 value!");
            this.historySize = historySize;
        }

        public int add(int newValue) {
            if (values.size() < historySize) {
                values.add(newValue);
            } else {
                values.set(index++, newValue);
                if (index >= historySize) index = 0;
            }

            long total = 0;
            for (int i : values) {
                total += i;
            }
            return (int) (total/values.size());
        }
    }

}

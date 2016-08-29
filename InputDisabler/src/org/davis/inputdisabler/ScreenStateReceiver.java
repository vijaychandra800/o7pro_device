package org.davis.inputdisabler;

/*
 * Created by Dāvis Mālnieks on 04/10/2015
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;

import org.davis.inputdisabler.Constants;

public class ScreenStateReceiver extends BroadcastReceiver implements SensorEventListener {

    public static final String TAG = "ScreenStateReceiver";

    public static final boolean DEBUG = false;

    public static final int DOZING_TIME = 1000 * 5;

    android.os.Handler mDozeDisable;

    boolean mScreenOn;
	
	boolean mTouchOn;

    SensorManager mSensorManager;
	
    Sensor mSensor;
	
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "Received intent");

        switch (intent.getAction()) {
            case Intent.ACTION_SCREEN_ON:
                Log.d(TAG, "Screen on!");
			    Log.d(TAG, "Screen on!: mScreenOn " + mScreenOn);
			    Log.d(TAG, "Screen on!: " + mTouchOn);
				
				mScreenOn = true;
				mTouchOn = true;
				enableDevices(true);
				
				Log.d(TAG, "Screen on! end");
			    Log.d(TAG, "Screen on! end: mScreenOn " + mScreenOn);
			    Log.d(TAG, "Screen on! end: " + mTouchOn);
					
                break;
            case Intent.ACTION_SCREEN_OFF:
                Log.d(TAG, "Screen off!");
				Log.d(TAG, "Screen off!: mScreenOn " + mScreenOn);
			    Log.d(TAG, "Screen off!: " + mTouchOn);
			
				mScreenOn = false;
				mTouchOn = false;
				enableDevices(false);
				
				Log.d(TAG, "Screen off! end");
				Log.d(TAG, "Screen off! end: mScreenOn " + mScreenOn);
			    Log.d(TAG, "Screen off! end: " + mTouchOn);
					
                break;
            case Constants.ACTION_DOZE_PULSE_STARTING:
                Log.d(TAG, "Doze");

                mDozeDisable = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if(!mScreenOn) {
                            Log.d(TAG, "Screen was turned on while dozing");
			                Log.d(TAG, "Screen was turned on while dozing: mScreenOn " + mScreenOn);
			                Log.d(TAG, "Screen was turned on while dozing: " + mTouchOn);
							mScreenOn = false;
							mTouchOn = false;
							enableDevices(false);
							Log.d(TAG, "Screen was turned on while dozing end");
			                Log.d(TAG, "Screen was turned on while dozing end: mScreenOn " + mScreenOn);
			                Log.d(TAG, "Screen was turned on while dozing end: " + mTouchOn);
                        } else {
                           Log.d(TAG, "Screen was turned off while dozing");
			               Log.d(TAG, "Screen was turned off while dozing: mScreenOn " + mScreenOn);
			               Log.d(TAG, "Screen was turned off while dozing: " + mTouchOn);
						   mTouchOn = true;
						   mScreenOn = true;
						   enableDevices(true);
						   Log.d(TAG, "Screen was turned off while dozing end");
			               Log.d(TAG, "Screen was turned off while dozing end: mScreenOn " + mScreenOn);
			               Log.d(TAG, "Screen was turned off while dozing end: " + mTouchOn);
                        }
                    }
                };
                mDozeDisable.postDelayed(runnable, DOZING_TIME);
				Log.d(TAG, "Doze");
			    Log.d(TAG, "Doze: mScreenOn " + mScreenOn);
			    Log.d(TAG, "Doze: " + mTouchOn);
				mTouchOn = true;
                enableDevices(true);
				Log.d(TAG, "Doze end");
			    Log.d(TAG, "Doze end: mScreenOn " + mScreenOn);
			    Log.d(TAG, "Doze end: " + mTouchOn);
                break;
            case TelephonyManager.ACTION_PHONE_STATE_CHANGED:
                Log.d(TAG, "Phone state changed!");

                final TelephonyManager telephonyManager =
                        (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                switch (telephonyManager.getCallState()) {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
						Log.d(TAG, "Phone state: CALL_STATE_OFFHOOK");
                        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
                        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
                        mSensorManager.registerListener(this, mSensor, 3);
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
						Log.d(TAG, "Phone state: CALL_STATE_IDLE");
                        if(mSensorManager != null) {
                            mSensorManager.unregisterListener(this);
                        }
                        break;
                }
                break;
        }
    }
	
    // Enables or disables input devices by writing to sysfs path
    private void enableDevices(boolean enable) {
        boolean ret;
        if(enable) {
            // Turn on touch input
            ret = write_sysfs(Constants.TS_PATH, true);
            Log.d(TAG, "Enabled touchscreen, success? " + ret);
        } else {
            // Turn off touch input
            ret = write_sysfs(Constants.TS_PATH, false);
            Log.d(TAG, "Disabled touchscreen, success? " + ret);
        }
    }

    // Writes to sysfs node, returns true if success, false if fail
    private boolean write_sysfs(String path, boolean on) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            byte[] bytes = new byte[2];
            bytes[0] = (byte)(on ? '1' : '0');
            bytes[1] = '\n';
            fos.write(bytes);
            fos.close();
        } catch (Exception e) {
            Log.e(TAG, "Fail: " + e.getLocalizedMessage());
            return false;
        }
        return true;
    }
	
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.values[0] == 0.0f) {
            Log.d(TAG, "Proximity: screen off");
			Log.d(TAG, "Proximity: screen off mScreenOn " + mScreenOn);
			Log.d(TAG, "Proximity: screen off mScreenOn " + mTouchOn);
			mScreenOn = false;
			mTouchOn = false;
			enableDevices(false);
			Log.d(TAG, "Proximity: screen off end");
			Log.d(TAG, "Proximity: screen off end mScreenOn " + mScreenOn);
			Log.d(TAG, "Proximity: screen off end mScreenOn " + mTouchOn);	
        } else {
			Log.d(TAG, "Proximity: screen on");
			Log.d(TAG, "Proximity: screen on mScreenOn " + mScreenOn);
			Log.d(TAG, "Proximity: screen on mScreenOn " + mTouchOn);
			mScreenOn = true;
			mTouchOn = true;
			enableDevices(true);
			Log.d(TAG, "Proximity: screen on end");
			Log.d(TAG, "Proximity: screen on end mScreenOn " + mScreenOn);
			Log.d(TAG, "Proximity: screen on end mScreenOn " + mTouchOn);	
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
		Log.d(TAG, "onAccuracyChanged");
		Log.d(TAG, "onAccuracyChanged: screen on mScreenOn " + mScreenOn);
		Log.d(TAG, "onAccuracyChanged: screen on mScreenOn " + mTouchOn);
    }
}

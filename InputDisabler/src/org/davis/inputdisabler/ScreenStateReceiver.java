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
import android.os.PowerManager;
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

    SensorManager mSensorManager;
	
    Sensor mSensor;
	
	private PowerManager pm;
	
	// Check display
    private boolean check_screen() {
        pm = (PowerManager) InputDisablerService.this.getSystemService(Context.POWER_SERVICE);
		return pm.isInteractive();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "Received intent");

        switch (intent.getAction()) {
            case Intent.ACTION_SCREEN_ON:
                Log.d(TAG, "Screen on!");
				if(check_screen()){
					Log.d(TAG, "ACTION_SCREEN_ON: we check = true");
				}else{
					Log.d(TAG, "ACTION_SCREEN_ON: we check = false");
				}
				if(!mScreenOn){
					mScreenOn = true;
					enableDevices(true);
				}	
                break;
            case Intent.ACTION_SCREEN_OFF:
                Log.d(TAG, "Screen off!");
				if(check_screen()){
					Log.d(TAG, "ACTION_SCREEN_OFF: we check = true");
				}else{
					Log.d(TAG, "ACTION_SCREEN_OFF: we check = false");
				}
				if(mScreenOn){
					mScreenOn = false;
					enableDevices(false);
				}	
                break;
            case Constants.ACTION_DOZE_PULSE_STARTING:
                Log.d(TAG, "Doze");

                mDozeDisable = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if(!mScreenOn) {
                            Log.d(TAG, "Screen was turned on while dozing");
							enableDevices(false);
							if(check_screen()){
								Log.d(TAG, "Doze: !mScreenOn but we check = true");
							}else{
								Log.d(TAG, "Doze: !mScreenOn but we check = false");
							}	
                        } else {
                           Log.d(TAG, "Screen was turned off while dozing");
						   enableDevices(true);
						   
						   if(check_screen()){
								Log.d(TAG, "Doze: mScreenOn but we check = true");
							}else{
								Log.d(TAG, "Doze: mScreenOn but we check = false");
							}	
                        }
                    }
                };
                mDozeDisable.postDelayed(runnable, DOZING_TIME);
				if(check_screen()){
					Log.d(TAG, "Doze: we check = true");
				}else{
					Log.d(TAG, "Doze: we check = false");
				}
                enableDevices(true);
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
			if(mScreenOn){
				mScreenOn = false;
				enableDevices(false);
			}
			if(check_screen()){
				 Log.d(TAG, "Proximity: screen off But check_screen() true");
			}else{
				 Log.d(TAG, "Proximity: screen off check_screen() false");
			}	
        } else {
			if(!mScreenOn){
				mScreenOn = true;
                enableDevices(true);
			}
			if(check_screen()){
				 Log.d(TAG, "Proximity: screen on check_screen() true");
			}else{
				 Log.d(TAG, "Proximity: screen on but check_screen() false");
			}	
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}

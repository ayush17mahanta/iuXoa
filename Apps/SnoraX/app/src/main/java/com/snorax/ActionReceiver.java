package com.snorax;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int action = intent.getIntExtra("action", -1);
        Log.d("ActionReceiver", "Received action: " + action);

        switch (action) {
            case SystemControl.ACTION_NORMAL:
                SystemControl.setRingerMode(context, SystemControl.ACTION_NORMAL);
                break;
            case SystemControl.ACTION_VIBRATE:
                SystemControl.setRingerMode(context, SystemControl.ACTION_VIBRATE);
                break;
            case SystemControl.ACTION_SILENT:
                SystemControl.setRingerMode(context, SystemControl.ACTION_SILENT);
                break;
            default:
                Log.e("ActionReceiver", "Unknown action: " + action);
        }
    }
}
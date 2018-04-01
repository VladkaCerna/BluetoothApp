package bltconnectiontest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationDismissedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent mIntent = new Intent(context, SensorService.class);
        context.startService(mIntent);
        //Helpers.showToast(context, "Receiver called");
    }
}

package bltconnectiontest;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;


public class UnlockIntentService extends IntentService {

    public UnlockIntentService() {
        super("UnlockIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(intent.getIntExtra("ID", 1));
            Config config = new ConfigManager().getConfig(this);
            MessageManager messageManager = MessageManager.getMananager(this);
            Message unlockRequest = messageManager.createUnlockRequest(config.getPhoneId());
            messageManager.send(unlockRequest);

            Intent intent1 = new Intent(this, SensorService.class);
            startService(intent1);
        }
    }
}

package bltconnectiontest;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class NotificationActivity extends AppCompatActivity {
    //SensorService mService;
    boolean mBound = false;

//    private ServiceConnection mConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName className,
//                                       IBinder service) {
//            SensorService.LocalBinder binder = (SensorService.LocalBinder) service;
//            mService = binder.getService();
//            mBound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            mBound = false;
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        popUpWindowShow(this);
        //bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        //Helpers.showToast(this, "Pruhledna aktivita");
    }

    private void popUpWindowShow(final Context context) {
        AlertDialog.Builder mLockDialog = new AlertDialog.Builder(context);

        mLockDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Intent intent = new Intent(context, SensorService.class);
                startService(intent);
                //mService.registerSensorListener();
                finish();
            }
        });

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View mView = inflater.inflate(R.layout.lock_dialog, null);
        Button mBtnYes = mView.findViewById(R.id.btnYes);
        Button mBtnNo = mView.findViewById(R.id.btnNo);
        Button mBtnMaybe = mView.findViewById(R.id.btnMaybe);
        mLockDialog.setView(mView);
        final AlertDialog dialog = mLockDialog.create();
        dialog.show();

        mBtnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mService.unregisterSensorListener();
                dialog.dismiss();
                //sendLockRequest(context);
                Intent intent = new Intent(context, LockIntentService.class);
                context.startService(intent);
                //mService.showUnlockNotification();
                //mService.onDestroy();
                //mService.unbindService(mConnection);
                finish();
            }
        });
        mBtnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                //mService.registerSensorListener();
                Intent intent = new Intent(context, SensorService.class);
                startService(intent);
                finish();
            }
        });
        mBtnMaybe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                //mService.unregisterSensorListener();
                Intent intent = new Intent(context, LaterNotificationIntentService.class);
                startService(intent);
                //mService.showLaterNotification();
                finish();
            }
        });
    }

//    void sendLockRequest(Context context){
//        String phoneId = new ConfigManager().getConfig(context).getPhoneId();
//        MessageManager messageManager = MessageManager.GetMananager(context);
//        Message message = messageManager.createLockRequest(phoneId);
//        messageManager.Send(message);
//    }
}

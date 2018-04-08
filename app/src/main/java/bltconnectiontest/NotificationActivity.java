package bltconnectiontest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        popUpWindowShow(this);
    }

    private void popUpWindowShow(final Context context) {
        AlertDialog.Builder mLockDialog = new AlertDialog.Builder(context);

        mLockDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Intent intent = new Intent(context, SensorService.class);
                startService(intent);
                finish();
            }
        });

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup parent = (ViewGroup) getWindow().getDecorView();
        View mView = inflater.inflate(R.layout.lock_dialog,parent , false);
        Button mBtnYes = mView.findViewById(R.id.btnYes);
        Button mBtnNo = mView.findViewById(R.id.btnNo);
        Button mBtnMaybe = mView.findViewById(R.id.btnMaybe);
        mLockDialog.setView(mView);
        final AlertDialog dialog = mLockDialog.create();
        dialog.show();

        mBtnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(context, LockIntentService.class);
                context.startService(intent);
                finish();
            }
        });
        mBtnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(context, SensorService.class);
                startService(intent);
                finish();
            }
        });
        mBtnMaybe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(context, LaterNotificationIntentService.class);
                startService(intent);
                finish();
            }
        });
    }
}

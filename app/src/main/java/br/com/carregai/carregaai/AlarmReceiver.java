package br.com.carregai.carregaai;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent arg1) {
        // For our recurring task, we'll just display a message
        Toast.makeText(ctx, "I'm running", Toast.LENGTH_SHORT).show();
        Log.i("entro", "sim");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.drawable.ic_action_name)
                        .setContentTitle("Saldo baixo")
                        .setContentText("Seu saldo é baixo");

        NotificationManager mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }
}
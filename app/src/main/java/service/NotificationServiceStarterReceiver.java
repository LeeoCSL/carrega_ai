package service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public final class NotificationServiceStarterReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        final String[] items = {"segunda-feira", "terça-feira","quarta-feira","quinta-feira",
                "sexta-feira","sábado","domingo"};

        Calendar sCalendar = Calendar.getInstance();
        String actualDay = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        float saldoAtual = sharedPref.getFloat("saldo_atual", 0);
        float valorDiario = sharedPref.getFloat("valor_diario", 0);

        for(int i = 0; i < items.length; i++){
            if(sharedPref.getBoolean(items[i], false) && items[i].equals(actualDay)){

                if(saldoAtual - valorDiario > 0){
                    saldoAtual -= valorDiario;

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putFloat("saldo_atual", saldoAtual);
                }

                if(saldoAtual < 10){
                    NotificationEventReceiver.setupAlarm(context);
                }
            }
        }
        //apagar isso
        NotificationEventReceiver.setupAlarm(context);
    }
}
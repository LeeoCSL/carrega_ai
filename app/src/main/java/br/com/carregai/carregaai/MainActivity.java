package br.com.carregai.carregaai;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.zip.Inflater;

import fragments.DialogViagemExtra;
import fragments.DialogWeek;
import service.NotificationEventReceiver;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private FirebaseAuth mAuth;

    private Button mValorDiario, mSaldoAtual, mValorRecarga, mTarifa;

    final String[] items = {"Segunda-Feira", "Terça-Feira","Quarta-Feira","Quinta-Feira",
            "Sexta-Feira","Sábado","Domingo"};

    private String[] tarifas = {"Tarifa Comum", "Tarifa Estudante",
                                "Integração Comum", "Integração Estudante"};
    private double[] valores = {3.80, 1.90, 6.80, 3.40};

    private TextView[] mDiasView;

    private int currentIndex = 0;

    private TextView mTarifaTxt;

    private boolean lock;

    private PendingIntent pendingIntent;
    private AlarmManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar)findViewById(R.id.main_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_action_name);

        mValorDiario = (Button)findViewById(R.id.btn_valor_diario);
        mSaldoAtual = (Button)findViewById(R.id.btn_saldo_atual);
        mValorRecarga = (Button)findViewById(R.id.btn_valor_recarga);
        mTarifa = (Button) findViewById(R.id.btn_tarifa);

        mDiasView = new TextView[items.length];

        mDiasView[0] = (TextView)findViewById(R.id.segunda_feira);
        mDiasView[1] = (TextView)findViewById(R.id.terca_feira);
        mDiasView[2] = (TextView)findViewById(R.id.quarta_feira);
        mDiasView[3] = (TextView)findViewById(R.id.quinta_feira);
        mDiasView[4] = (TextView)findViewById(R.id.sexta_feira);
        mDiasView[5] = (TextView)findViewById(R.id.sabado);
        mDiasView[6] = (TextView)findViewById(R.id.domingo);

        mTarifaTxt = (TextView)findViewById(R.id.txt_tarifa);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        lock = sharedPref.getBoolean("lock", false);

        Toast.makeText(this, "Lock: " +lock, Toast.LENGTH_LONG).show();

        if(!lock){

            NotificationEventReceiver.setupAlarm(getApplicationContext());


/*            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

            startAlarm();*/

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("lock", true);
            editor.commit();
        }
    }

    private void startAlarm() {
        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        int interval = 10000;

        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateButtons();
    }

    public void viagemExtra(View v){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        float value = Float.parseFloat(sharedPref.getString("viagem_extra", "0"));

        if(value == 0){
            FragmentManager fragmentManager = getSupportFragmentManager();
            DialogViagemExtra dialog = new DialogViagemExtra();
            dialog.show(fragmentManager, "viagens_extra");
        }else{
            SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
            float saldoAtual = sp.getFloat("saldo_atual", 0);
            saldoAtual -= value;
            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat("saldo_atual", saldoAtual);
            editor.commit();
            mSaldoAtual.setText("R$ " +String.format("%.2f", saldoAtual));

            Toast.makeText(this, "Seu saldo foi atualizado. [Viagem Extra: R$ " +value+" ]", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateButtons(){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

        float valorDiario = sharedPref.getFloat("valor_diario", 0);
        mValorDiario.setText("R$ " +String.format("%.2f", valorDiario));

        float valorRecarga = sharedPref.getFloat("valor_recarga", 0);
        mValorRecarga.setText("R$ " +String.format("%.2f", valorRecarga));

        float saldoAtual = sharedPref.getFloat("saldo_atual", 0);
        mSaldoAtual.setText("R$ " +String.format("%.2f", saldoAtual));

        for(int i = 0; i < items.length; i++){
            String dia = items[i].toLowerCase();

            boolean ativo = sharedPref.
                    getBoolean(items[i].toLowerCase(), false);

            if(ativo){
                mDiasView[i].setTypeface(null, Typeface.BOLD);
            }else{
                mDiasView[i].setTypeface(null, Typeface.NORMAL);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId()){
            case R.id.menu_logout:
                logout();
                return true;

            case R.id.menu_configuracoes:
                startActivity(new Intent(MainActivity.this, MyPreferencesActivity.class));
                return true;
        }
        return false;
    }

    private void logout(){
        mAuth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void selectDays(View v){
        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogWeek dialogWeek = new DialogWeek();
        dialogWeek.show(fragmentManager, "dias_semana");
    }

    public void valorRecarga(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Valor Recarga");

        final EditText input = new EditText(this);
        String inputText = mValorRecarga.getText().toString();
        input.setText(inputText.substring(3));
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        builder.setView(input);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                float value = Float.parseFloat(input.getText().toString().replace(',','.'));

                String saldoInput = mSaldoAtual.getText().toString().substring(3).replace(',','.');
                float saldo = Float.parseFloat(saldoInput);

                float saldoFinal = saldo + value;
                mSaldoAtual.setText("R$ " + String.format("%.2f", saldoFinal));

                mValorRecarga.setText("R$ " + String.format("%.2f", value));

                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putFloat("saldo_atual", saldoFinal);
                editor.putFloat("valor_recarga", value);

                editor.commit();
            }
        });

        builder.show();
    }

    public void valorDiario(View v){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Valor Diário");

        final EditText input = new EditText(this);
        String inputText = mValorDiario.getText().toString();
        input.setText(inputText.substring(3));
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        builder.setView(input);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                float value = Float.parseFloat(input.getText().toString().replace(',','.'));

                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putFloat("valor_diario", value);
                editor.commit();

                mValorDiario.setText("R$ " + String.format("%.2f", value));
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void mudar(View v){

        mTarifaTxt.setText(tarifas[currentIndex]);
        mTarifa.setText("R$ " + String.format("%.2f", valores[currentIndex]));

        currentIndex++;

        if(currentIndex > tarifas.length - 1)
            currentIndex = 0;
    }
}

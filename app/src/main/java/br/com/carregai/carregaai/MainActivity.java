package br.com.carregai.carregaai;

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

import java.util.zip.Inflater;

import fragments.DialogWeek;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private FirebaseAuth mAuth;

    private Button mValorDiario, mSaldoAtual, mValorRecarga;

    final String[] items = {"Segunda-Feira", "Terça-Feira","Quarta-Feira","Quinta-Feira",
            "Sexta-Feira","Sábado","Domingo"};

    private TextView[] mDiasView;

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

        mDiasView = new TextView[items.length];

        mDiasView[0] = (TextView)findViewById(R.id.segunda_feira);
        mDiasView[1] = (TextView)findViewById(R.id.terca_feira);
        mDiasView[2] = (TextView)findViewById(R.id.quarta_feira);
        mDiasView[3] = (TextView)findViewById(R.id.quinta_feira);
        mDiasView[4] = (TextView)findViewById(R.id.sexta_feira);
        mDiasView[5] = (TextView)findViewById(R.id.sabado);
        mDiasView[6] = (TextView)findViewById(R.id.domingo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateButtons();
    }

    private void updateButtons(){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

        float valorDiario = sharedPref.getFloat("valor_diario", 0);
        mValorDiario.setText("R$ " +String.valueOf(valorDiario));

        float valorRecarga = sharedPref.getFloat("valor_recarga", 0);
        mValorRecarga.setText("R$ " +valorRecarga);

        float saldoAtual = sharedPref.getFloat("saldo_atual", 0);
        mSaldoAtual.setText("R$ " +saldoAtual);

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
                mSaldoAtual.setText("R$ " + saldoFinal);

                mValorRecarga.setText("R$ " + value);

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

                float value = Float.parseFloat(input.getText().toString());

                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putFloat("valor_diario", value);
                editor.commit();

                mValorDiario.setText("R$ " +value);
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
}

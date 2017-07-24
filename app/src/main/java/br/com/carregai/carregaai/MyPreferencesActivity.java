package br.com.carregai.carregaai;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.prefs.PreferenceChangeListener;

/**
 * Created by renan.boni on 24/07/2017.
 */
public class MyPreferencesActivity extends PreferenceActivity
{
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().
                replace(android.R.id.content, new MyPreferencesFragment()).commit();
    }

    public static class MyPreferencesFragment extends PreferenceFragment
    {
        private EditTextPreference mSaldo, mValorDiario, mValorRecarga;
        private CheckBoxPreference mRecarga;
        private ListPreference mDiasDaSemana;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            mSaldo = (EditTextPreference)findPreference("saldo");
            mValorDiario = (EditTextPreference)findPreference("valor_diario");

            mDiasDaSemana = (ListPreference)findPreference("dias_da_semana");

            mValorRecarga = (EditTextPreference)findPreference("valor_recarga");

            mRecarga = (CheckBoxPreference)findPreference("recarga");

            mSaldo.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Log.i("novo", (String)newValue);
                    return false;
                }
            });
        }
    }
}

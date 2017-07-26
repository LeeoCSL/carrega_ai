package br.com.carregai.carregaai;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.prefs.PreferenceChangeListener;

/**
 * Created by renan.boni on 24/07/2017.
 */
public class MyPreferencesActivity extends PreferenceActivity
{
    private static boolean firstOpen = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().
                replace(android.R.id.content, new MyPreferencesFragment()).commit();
    }

    public static class MyPreferencesFragment extends PreferenceFragment
    {
        private EditTextPreference mViagemExtra;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            mViagemExtra = (EditTextPreference)findPreference("viagem_extra");

            mViagemExtra.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    saveChanges("viagem_extra", (String)newValue);
                    return true;
                }
            });
        }

        private void saveChanges(String key, String value){
            SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, value);
            editor.commit();
        }
    }
}

package br.com.carregai.carregaai;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

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
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}

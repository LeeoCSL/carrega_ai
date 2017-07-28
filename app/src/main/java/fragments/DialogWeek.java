package fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import br.com.carregai.carregaai.R;


public class DialogWeek extends DialogFragment {

    private TextView[] mDiasSemana;
    private boolean mCheckeds[];

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        final String[] items = {"segunda-feira", "terça-feira","quarta-Feira","quinta-Feira",
                    "sexta-feira","sábado","domingo"};

        mDiasSemana = new TextView[items.length];

        mDiasSemana[0] = (TextView)getActivity().findViewById(R.id.segunda_feira);
        mDiasSemana[1] = (TextView)getActivity().findViewById(R.id.terca_feira);
        mDiasSemana[2] = (TextView)getActivity().findViewById(R.id.quarta_feira);
        mDiasSemana[3] = (TextView)getActivity().findViewById(R.id.quinta_feira);
        mDiasSemana[4] = (TextView)getActivity().findViewById(R.id.sexta_feira);
        mDiasSemana[5] = (TextView)getActivity().findViewById(R.id.sabado);
        mDiasSemana[6] = (TextView)getActivity().findViewById(R.id.domingo);

        mCheckeds = new boolean[items.length];

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        for(int i = 0; i < items.length; i++){
            String key = items[i];
            boolean value = sharedPref.getBoolean(key, false);

            mCheckeds[i] = value;
        }

        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        builder.setTitle("Dias de uso")
        .setMultiChoiceItems(items, mCheckeds,
        		new DialogInterface.OnMultiChoiceClickListener() {
        	public void onClick(DialogInterface dialog, int item, boolean isChecked) {
                String key = items[item].toLowerCase();

                mCheckeds[item] = isChecked;

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(key, isChecked);
                editor.commit();
            }
	    });

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TextView v = (TextView)getActivity().findViewById(R.id.segunda_feira);

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sharedPref.edit();

                for(int i = 0; i < items.length; i++){

                    editor.putBoolean(items[i], mCheckeds[i]);
                    editor.commit();

                    if(mCheckeds[i]){
                        mDiasSemana[i].setTypeface(null, Typeface.BOLD);
                    }else{
                        mDiasSemana[i].setTypeface(null, Typeface.NORMAL);
                    }
                }
                dialog.dismiss();
            }

        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }
}


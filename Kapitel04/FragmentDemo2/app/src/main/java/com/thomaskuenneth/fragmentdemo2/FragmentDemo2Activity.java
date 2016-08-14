package com.thomaskuenneth.fragmentdemo2;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class FragmentDemo2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Benutzeroberfläche anzeigen
        setContentView(R.layout.activity_main);
        final Button button = (Button) findViewById(R.id.button);
        // auf das Anklicken reagieren
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                // Fragmente erzeugen und hinzufügen
                for (int i = 0; i < 3; i++) {
                    EinfachesFragment fragment = new EinfachesFragment();
                    fragmentTransaction.add(R.id.ll, fragment);
                }
                // Transaktion auf Zurück-Stapel legen
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    public static class EinfachesFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Benutzeroberfläche aufblasen (laden)
            return inflater.inflate(R.layout.einfaches_fragment, container, false);
        }
    }
}

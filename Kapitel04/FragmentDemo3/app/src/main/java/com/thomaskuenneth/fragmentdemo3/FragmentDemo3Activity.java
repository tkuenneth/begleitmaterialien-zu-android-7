package com.thomaskuenneth.fragmentdemo3;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class FragmentDemo3Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Benutzeroberfläche der Hauptactivity anzeigen
        setContentView(R.layout.fragmentdemo3);
    }

    /**
     * Dieses Fragment zeigt eine Auswahlliste
     */
    public static class AuswahlFragment extends ListFragment {

        private static final String STR_ZULETZT_SELEKTIERT =
                "zuletztSelektiert";

        boolean zweiSpaltenModus;
        int zuletztSelektiert = 0;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // Liste aus einem String-Array befüllen
            setListAdapter(new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_activated_1,
                    new String[]{"eins", "zwei", "drei"}));

            // prüfen, ob das Details-Fragment direkt eingebunden
            // werden kann (dazu muss es im Layout ein Element mit
            // der Id details geben)
            View detailsFrame = getActivity().findViewById(R.id.details);
            zweiSpaltenModus = detailsFrame != null &&
                    detailsFrame.getVisibility() == View.VISIBLE;

            if (savedInstanceState != null) {
                // ggf. zuletztSelektiert wiederherstellen
                zuletztSelektiert =
                        savedInstanceState.getInt(STR_ZULETZT_SELEKTIERT, 0);
            }

            if (zweiSpaltenModus) {
                // Im Zweispalten-Modus invertiert die View das
                // selektierte Element
                getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                // Details anzeigen
                detailsAnzeigen(zuletztSelektiert);
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            // zuletzt selektierten Eintrag merken
            outState.putInt(STR_ZULETZT_SELEKTIERT, zuletztSelektiert);
        }

        @Override
        public void onListItemClick(ListView l, View v,
                                    int position, long id) {
            detailsAnzeigen(position);
        }

        /**
         * Diese Hilfsmethode zeigt die Details zu dem selektierten
         * Element an; entweder wird ein Fragment "in place" in der
         * aktuellen Activity angezeigt, oder es wird eine neue gestartet
         */
        void detailsAnzeigen(int index) {
            zuletztSelektiert = index;
            if (zweiSpaltenModus) {
                // "in place"-Darstellung
                getListView().setItemChecked(index, true);
                DetailsFragment details = (DetailsFragment)
                        getFragmentManager().findFragmentById(R.id.details);
                if (details == null || details.getIndex() != index) {
                    // neues Fragment passend zum selektierten
                    // Eintrag erzeugen und anzeigen
                    details = DetailsFragment.newInstance(index);
                    FragmentTransaction ft =
                            getFragmentManager().beginTransaction();
                    ft.replace(R.id.details, details);
                    // einen Übergang darstellen
                    ft.setTransition(
                            FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                }
            } else {
                // neue Activity starten
                Intent intent = new Intent();
                intent.setClass(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsFragment.INDEX, index);
                startActivity(intent);
            }
        }
    }

    public static class DetailsFragment extends Fragment {

        public static final String INDEX = "index";

        public static DetailsFragment newInstance(int index) {
            DetailsFragment f = new DetailsFragment();
            Bundle args = new Bundle();
            args.putInt(INDEX, index);
            f.setArguments(args);
            return f;
        }

        public int getIndex() {
            return getArguments().getInt(INDEX, 0);
        }

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container,
                                 Bundle savedInstanceState) {
            ScrollView scroller = null;
            // View nur erzeugen, wenn das Fragment
            // angezeigt werden wird
            if (container != null) {
                scroller = new ScrollView(getActivity());
                TextView text = new TextView(getActivity());
                scroller.addView(text);
                text.setText("Element #" + (1 + getIndex())
                        + " ist sichtbar");
            }
            return scroller;
        }
    }

    public static class DetailsActivity extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                finish();
                return;
            }
            if (savedInstanceState == null) {
                DetailsFragment details = new DetailsFragment();
                details.setArguments(getIntent().getExtras());
                getFragmentManager().beginTransaction().
                        add(android.R.id.content, details).commit();
            }
        }
    }
}

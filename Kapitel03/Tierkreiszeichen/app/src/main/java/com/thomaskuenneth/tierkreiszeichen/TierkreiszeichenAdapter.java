package com.thomaskuenneth.tierkreiszeichen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Diese Klasse bildet das Modell für die Listenansicht der App.
 *
 * @author Thomas Künneth
 * @see BaseAdapter
 */
public class TierkreiszeichenAdapter extends BaseAdapter {

    private final List<Tierkreiszeichen> zodiak;
    private final LayoutInflater inflator;
    private final DateFormat df;
    private final Calendar cal;

    public TierkreiszeichenAdapter(Context context) {
        // wird für das Aufblasen der XML-Datei benötigt
        inflator = LayoutInflater.from(context);
        // Tierkreiszeichen für alle Monate ermitteln
        zodiak = new ArrayList<>();
        for (int monat = Calendar.JANUARY;
             monat <= Calendar.DECEMBER; monat++) {
            Tierkreiszeichen zeichen = Zodiak
                    .getTierkreiszeichenFuerMonat(monat);
            zodiak.add(zeichen);
        }
        // Legt fest, in welchem Format das Datum ausgegeben wird
        df = new SimpleDateFormat(context.getString(
                R.string.format_string),
                Locale.US);
        cal = Calendar.getInstance();
    }

    @Override
    public int getCount() {
        return zodiak.size();
    }

    @Override
    public Object getItem(int position) {
        return zodiak.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position,
                        View convertView, ViewGroup parent) {
        ViewHolder holder;
        // falls nötig, convertView bauen
        if (convertView == null) {
            // Layoutdatei entfalten
            convertView = inflator.inflate(R.layout.icon_text_text,
                    parent, false);
            // Holder erzeugen
            holder = new ViewHolder();
            holder.name =
                    (TextView) convertView.findViewById(R.id.text1);
            holder.datumsbereich = (TextView) convertView
                    .findViewById(R.id.text2);
            holder.icon =
                    (ImageView) convertView.findViewById(R.id.icon);

            convertView.setTag(holder);
        } else {
            // Holder bereits vorhanden
            holder = (ViewHolder) convertView.getTag();
        }
        Context context = parent.getContext();
        Tierkreiszeichen zeichen = (Tierkreiszeichen) getItem(position);
        holder.name.setText(zeichen.getName(context));
        holder.icon.setImageResource(zeichen.getIdForDrawable());
        cal.set(Calendar.DAY_OF_MONTH, zeichen.getTag());
        cal.set(Calendar.MONTH, zeichen.getMonat());
        String datum1 = df.format(cal.getTime());
        if (++position >= getCount()) {
            position = 0;
        }
        zeichen = (Tierkreiszeichen) getItem(position);
        cal.set(Calendar.DAY_OF_MONTH, zeichen.getTag() - 1);
        cal.set(Calendar.MONTH, zeichen.getMonat());
        String datum2 = df.format(cal.getTime());
        holder.datumsbereich.setText(context.getString(R.string.interval,
                datum1, datum2));
        return convertView;
    }

    static class ViewHolder {
        TextView name, datumsbereich;
        ImageView icon;
    }
}

package com.thomaskuenneth.tierkreiszeichen;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Diese Klasse bildet die 12 Tierkreiszeichen
 * in eine {@link HashMap} ab,
 * deren Schlüssel der Monat ist. Die Datumsangaben
 * stellen Mittelwerte dar und beziehen sich auf ein
 * Jahr mit 365 Tagen. Schaltjahre sind in den Datierungen
 * der Tierkreiszeichen also nicht berücksichtigt.
 *
 * @author Thomas Künneth
 */
public final class Zodiak {

    private static final Zodiak INSTANCE = new Zodiak();

    private final Map<Integer, Tierkreiszeichen> map;

    private Zodiak() {
        map = new HashMap<>();
        map.put(Calendar.JANUARY,
                new Tierkreiszeichen(21, Calendar.JANUARY,
                        R.string.aquarius));
        map.put(Calendar.FEBRUARY,
                new Tierkreiszeichen(20, Calendar.FEBRUARY,
                        R.string.pisces));
        map.put(Calendar.MARCH, new Tierkreiszeichen(21, Calendar.MARCH,
                R.string.aries));
        map.put(Calendar.APRIL, new Tierkreiszeichen(21, Calendar.APRIL,
                R.string.taurus));
        map.put(Calendar.MAY, new Tierkreiszeichen(22, Calendar.MAY,
                R.string.gemini));
        map.put(Calendar.JUNE, new Tierkreiszeichen(22, Calendar.JUNE,
                R.string.cancer));
        map.put(Calendar.JULY,
                new Tierkreiszeichen(24, Calendar.JULY,
                        R.string.leo));
        map.put(Calendar.AUGUST,
                new Tierkreiszeichen(24, Calendar.AUGUST,
                        R.string.virgo));
        map.put(Calendar.SEPTEMBER, new Tierkreiszeichen(24,
                Calendar.SEPTEMBER, R.string.libra));
        map.put(Calendar.OCTOBER,
                new Tierkreiszeichen(24, Calendar.OCTOBER,
                        R.string.scorpius));
        map.put(Calendar.NOVEMBER,
                new Tierkreiszeichen(23, Calendar.NOVEMBER,
                        R.string.sagittarius));
        map.put(Calendar.DECEMBER,
                new Tierkreiszeichen(22, Calendar.DECEMBER,
                        R.string.capricornus));
    }

    /**
     * Liefert eine Referenz auf ein Element der
     * Hashtable, die das Sternzeichen des übergebenen
     * Monats repräsentiert.
     *
     * @param monat Monat, zum Beispiel {@code Calendar.JUNE}
     * @return Instanz eines {@link Tierkreiszeichen}s
     */
    public static Tierkreiszeichen getTierkreiszeichenFuerMonat(
            int monat) {
        return INSTANCE.map.get(monat);
    }
}

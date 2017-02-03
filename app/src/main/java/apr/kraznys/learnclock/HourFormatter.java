package apr.kraznys.learnclock;


import android.util.Log;

public class HourFormatter {
    private static final String[][] hourNames ={ {
            "pierwsza",
            "druga",
            "trzecia",
            "czwarta",
            "piąta",
            "szósta",
            "siódma",
            "ósma",
            "dziewiąta",
            "dziesiąta",
            "jedenasta",
            "dwunasta",
            "trzynasta",
            "czternasta",
            "piętnasta",
            "szesnasta",
            "siedemnasta",
            "osiemnasta",
            "dziewiętnasta",
            "dwudziesta",
            "dwudziesta pierwsza",
            "dwudziesta druga",
            "dwudziesta trzecia",
            "dwudziesta czwarta"},
        {"pierwszej",
            "drugej",
            "trzeciej",
            "czwartej",
            "piątej",
            "szóstej",
            "siódmej",
            "ósmej",
            "dziewiątej",
            "dziesiątej",
            "jedenastej",
            "dwunastej",
            "trzynastej",
            "czternastej",
            "piętnastej",
            "szesnastej",
            "siedemnastej",
            "osiemnastej",
            "dziewiętnastej",
            "dwudziestej",
            "dwudziestej pierwszej",
            "dwudziestej drugiej",
            "dwudziestej trzeciej",
            "dwudziestej czwartej"}};

    private static final String[] minuteNames = {
            "",
            "dwie",
            "trzy",
            "cztery",
            "pięć",
            "sześć",
            "siedem",
            "osiem",
            "dziewięć",
            "dziesięć",
            "jedynaście",
            "dwanaście",
            "trzynaście",
            "czternaście",
            "pietnaście",
            "szesnaście",
            "siedemnaście",
            "osiemnaście",
            "dziewietnaście",
            "dwadzieścia",
            "dwadzieścia jeden",
            "dwadzieścia dwie",
            "dwadzieścia trzy",
            "dwadzieścia cztery",
            "dwadzieścia pięć",
            "dwadzieścia sześć",
            "dwadzieścia siedem",
            "dwadzieścia osiem",
            "dwadzieścia dziewięć",
            "trzydzieści",
            "trzydzieści jeden",
            "trzydzieści dwie",
            "trzydzieści trzy",
            "trzydzieści cztery",
            "trzydzieści pięć",
            "trzydzieści sześć",
            "trzydzieści siedem",
            "trzydzieści osiem",
            "trzydzieści dziewięć",
            "czterdzieści",
            "czterdzieści jeden",
            "czterdzieści dwie",
            "czterdzieści trzy",
            "czterdzieści cztery",
            "czterdzieści pięć",
            "czterdzieści sześć",
            "czterdzieści siedem",
            "czterdzieści osiem",
            "czterdzieści dziewięć",
            "pięćdziesiat",
            "pięćdziesiat jeden",
            "pięćdziesiat dwie",
            "pięćdziesiat trzy",
            "pięćdziesiat cztery",
            "pięćdziesiat pięć",
            "pięćdziesiat sześć",
            "pięćdziesiat siedem",
            "pięćdziesiat osiem",
            "pięćdziesiat dziewięć"};

    private String[] minuteVariants = {"minuta", "minutę", "minut", "minuty"};

    private class NumberAndVariant {
        int variant;
        String number;

        public NumberAndVariant(int variant, String number) {
            this.variant = variant;
            this.number = number;
        }
    }
    public String formatTime(int hour, int minutes) {
        if (minutes == 0)
            return formatHour(0, hour);
        NumberAndVariant numberAndVariant = formatMinutesAfter(minutes);
        Log.d("CC", String.format("%1$d %2$d", numberAndVariant.variant, hour));
        return String.format("%1$s %2$s", numberAndVariant.number, formatHour(numberAndVariant.variant, hour));
    }

    public String formatTimeBefore(int hour, int minutes) {
        if (minutes == 60)
            return "";
        NumberAndVariant numberAndVariant = formatMinutesBefore(minutes);
        return String.format("%1$s %2$s", numberAndVariant.number, formatHour(numberAndVariant.variant, hour));
    }

    private NumberAndVariant formatMinutesAfter(int minutes) {
        switch (minutes) {
            case 15:
                return new NumberAndVariant(1, "kwadrans po");
            default:
                return new NumberAndVariant(1, String.format("%1$s %2$s po", minuteNames[minutes - 1], getMinuteName(minutes, true)));
        }
    }

    private NumberAndVariant formatMinutesBefore(int minutes) {
        switch (minutes) {
            case 15:
                return new NumberAndVariant(0, "za kwadrans");
            case 30:
                return new NumberAndVariant(1, "wpół do");
            default:
                return new NumberAndVariant(0, String.format("za %1$s %2$s", minuteNames[minutes - 1], getMinuteName(minutes, false)));
        }
    }

    private String getMinuteName(int minutes, boolean after) {
        if (minutes == 1)
            return minuteVariants[after ? 0 : 1];
        int mod10 = minutes % 10;
        if (minutes/10==1) {
                return minuteVariants[2];
        }
        if (mod10 >1 && mod10<5) {
            return minuteVariants[3];
        }
        return minuteVariants[2];
    }

    private String formatHour(int variant, int hour) {
        return hourNames[variant][hour-1];
    }

}

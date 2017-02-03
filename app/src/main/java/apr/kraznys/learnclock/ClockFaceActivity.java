package apr.kraznys.learnclock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class ClockFaceActivity extends AppCompatActivity {

    private HourFormatter hourFormatter = new HourFormatter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_face);
        ClockView clockView = (ClockView) findViewById(R.id.clock);

        final TextView digitalHourTextView = (TextView) findViewById(R.id.hourDigital);
        final TextView hourTextView = (TextView) findViewById(R.id.hourText);
        final TextView beforeHourTextView = (TextView) findViewById(R.id.hourTextBefore);

        clockView.setListener(new ClockView.OnTimeChangeListener() {
            @Override
            public void onTimeChange(int hour, int minute, int nextHour) {
                digitalHourTextView.setText(String.format("%1$d:%2$02d", hour, minute));
                hourTextView.setText(hourFormatter.formatTime(hour, minute));
                beforeHourTextView.setText(hourFormatter.formatTimeBefore(nextHour, 60-minute));
            }
        });
        Calendar calendar = Calendar.getInstance();
        clockView.setTime(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));

    }
}

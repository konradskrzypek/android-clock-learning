package apr.kraznys.learnclock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ClockFaceActivity extends AppCompatActivity {

    private HourFormatter hourFormatter = new HourFormatter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_face);
        ClockView clockView = (ClockView) findViewById(R.id.clock);
        final TextView textView = (TextView) findViewById(R.id.hourText);
        clockView.setListener(new ClockView.OnTimeChangeListener() {
            @Override
            public void onTimeChange(int hour, int minute, int nextHour) {
                textView.setText(String.format("%1$d:%2$02d\n\n%3$s\n\n%4$s", hour, minute, hourFormatter.formatTime(hour, minute), hourFormatter.formatTimeBefore(nextHour, 60-minute)));
            }


        });
    }
}

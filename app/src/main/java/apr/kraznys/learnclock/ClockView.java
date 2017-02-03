package apr.kraznys.learnclock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import static apr.kraznys.learnclock.RadialCoords.CartesianPoint;
import static apr.kraznys.learnclock.RadialCoords.RadialPoint;
import static java.lang.Math.PI;

public class ClockView extends View {


    private RadialCoords radialCoords;
    private float xMiddle;
    private float yMiddle;

    public interface OnTimeChangeListener {
        void onTimeChange(int hour, int minute, int nextHour);
    }

    private boolean isInitialized = false;
    private Paint paint;
    private int height;
    private int width;
    private float padding;
    private Paint minutePaint;
    float clockRadius;
    private static float TWOPI = (float) (2 * PI);

    private int hour = 12;
    private int minute = 15;

    private float middleRadius;
    private float handStartRadius;
    private float hourHandRadius;
    private float minuteHandRadius;

    private boolean isTouched = false;
    private boolean wasTouchedInHourZone = false;

    private RadialPoint previousRadial = new RadialPoint(0, 0);
    private static float hourTouchZoneCoeff = 0.4F;
    private boolean is24HourMode = false;
    private int backgroundColor;
    private int foregroundColor;

    private OnTimeChangeListener listener;

    public void setListener(OnTimeChangeListener listener) {
        this.listener = listener;
    }
    public void setTime(int hour, int minute) {
        this.hour = hour == 0 ? getMaxHour() : hour;
        this.minute = minute;
        callListener();
    }

    private int getMaxHour() {
        return is24HourMode ? 24 : 12;
    }

    public ClockView(Context context) {
        super(context);
    }

    public ClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        processAttributes(context, attrs);
    }

    private void processAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ClockView,
                0, 0);

        try {
            backgroundColor = a.getColor(R.styleable.ClockView_backgroundColor, getResources().getColor(R.color.defaultBackground));
            foregroundColor = a.getColor(R.styleable.ClockView_foregroundColor, getResources().getColor(R.color.defaultForeground));
        } finally {
            a.recycle();
        }
    }

    public ClockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        processAttributes(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            RadialPoint radial = radialCoords.toRadial(event.getX(), event.getY());
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (radial.r < clockRadius) {
                    this.isTouched = true;
                    this.wasTouchedInHourZone = radial.r < clockRadius * hourTouchZoneCoeff;
                } else {
                    this.isTouched = false;
                }
            }
            setTimeFromRadialPoint(radial, previousRadial);
            previousRadial = radial;
        } else {
            this.isTouched = false;
        }
        this.invalidate();
        return true;
    }

    private void setTimeFromRadialPoint(RadialPoint radial, RadialPoint previousRadial) {
        int newHour = hour;
        int newMinute = minute;
        if (wasTouchedInHourZone) {
            newHour = (int) ((radial.phi + PI / 24) * 12 / TWOPI);
        } else {
            newMinute = (int) (radial.phi * 60 / TWOPI);
            int historicalRadialAngleQuarter;
            if (previousRadial != null) {
                historicalRadialAngleQuarter = previousRadial.getAngleQuarter();
            } else {
                historicalRadialAngleQuarter = minute / 15;
            }

            int radialAngleQuarter = radial.getAngleQuarter();
            int quarterDifference = historicalRadialAngleQuarter - radialAngleQuarter;
            switch (quarterDifference) {
                case -3:
                    newHour = hour - 1;
                    break;
                case 3:
                    newHour = getNextHour();
                    break;
            }
        }
        if (newHour == 0)
            newHour = getMaxHour();

        if (hour != newHour || minute != newMinute) {
            minute = newMinute;
            hour = newHour;
            Log.d("AA", String.format("%1$f:%2$d|%3$f:%4$d", radial.phi, radial.getAngleQuarter(), previousRadial.phi, previousRadial.getAngleQuarter()));
            Log.d("BB", String.format("%1$d:%2$d|%3$d", hour, minute, getNextHour()));
            callListener();
        }
    }

    private int getNextHour() {
        return hour == getMaxHour() ? 1 : hour + 1;
    }

    private void callListener() {
        if (listener != null)
            listener.onTimeChange(hour, minute, getNextHour());
    }

    private void initClock() {
        paint = new Paint();
        minutePaint = new Paint();
        isInitialized = true;
        height = getHeight();
        width = getWidth();
        xMiddle = width / 2;
        yMiddle = height / 2;
        radialCoords = new RadialCoords(xMiddle, yMiddle);
        float maxRadius = Math.min(xMiddle, yMiddle);
        padding = maxRadius * 0.1F;
        clockRadius = maxRadius - padding;
        middleRadius = clockRadius * 0.05F;
        handStartRadius = clockRadius * 0.1F;
        hourHandRadius = clockRadius * 0.7F;
        minuteHandRadius = clockRadius * 0.8F;

    }


    private void invalidateTextPaintAndMeasurements() {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInitialized) {
            initClock();
        }
        canvas.drawColor(backgroundColor);
        drawFace(canvas);

        //drawNumbers(canvas);
        postInvalidateDelayed(500);

    }

    private void drawNumbers(Canvas canvas) {
        paint.reset();
        paint.setARGB(100, 255, 0, 0);
        paint.setStyle(Paint.Style.FILL);

        paint.setTextSize(50);
        if (previousRadial != null)
            canvas.drawText(String.format("%1$d:%2$d %3$f:%4$d", this.hour, this.minute, previousRadial.phi / TWOPI * 360, previousRadial.getAngleQuarter()), 0, 50, paint);
    }

    private void drawFace(Canvas canvas) {
        initPaint(paint);
        initPaint(minutePaint);

        canvas.drawCircle(xMiddle, yMiddle, clockRadius, paint);

        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(xMiddle, yMiddle, middleRadius, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        minutePaint.setStrokeWidth(2);
        for (int m = 0; m < 5 * 12; m += 1) {

            float angle = TWOPI / 60 * m;

            float markLengthCoeff = m % 5 == 0 ? 0.9F : 0.95F;

            Paint markPaint = m % 5 == 0 ? this.minutePaint : this.paint;

            drawRadialLine(angle, clockRadius, clockRadius * markLengthCoeff, markPaint, canvas);
        }

        // hands
        float hourAngle = TWOPI / 12 * (hour % 12) + TWOPI / (12 * 60) * (minute % 60);
        float minuteAngle = TWOPI / 60 * (minute % 60);
        paint.setStrokeWidth(10);
        drawRadialLine(hourAngle, handStartRadius, hourHandRadius, paint, canvas);
        paint.setStrokeWidth(5);
        drawRadialLine(minuteAngle, handStartRadius, minuteHandRadius, paint, canvas);

        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(10);
        canvas.drawCircle(xMiddle, yMiddle, clockRadius * hourTouchZoneCoeff, paint);

    }

    private void drawRadialLine(float phi, float rStart, float rEnd, Paint paint, Canvas canvas) {
        CartesianPoint beg = radialCoords.fromRadial(phi, rStart);
        CartesianPoint end = radialCoords.fromRadial(phi, rEnd);
        canvas.drawLine(beg.x, beg.y, end.x, end.y, paint);

    }

    private void initPaint(Paint paint) {
        paint.reset();
        paint.setColor(foregroundColor);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
    }
}

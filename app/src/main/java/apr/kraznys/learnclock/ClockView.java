package apr.kraznys.learnclock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import static java.lang.Math.PI;
import static java.lang.Math.sqrt;

/**
 * TODO: document your custom view class.
 */
public class ClockView extends View {

    private boolean isInitialized = false;
    private Paint paint;
    private int height;
    private int width;
    private int xMiddle;
    private int yMiddle;
    private int padding;
    private Paint minutePaint;
    int clockRadius;
    private static double TWOPI = 2 * PI;

    private int hour = 12;
    private int minute = 15;

    private float middleRadius;
    private float handStartRadius;
    private float hourHandRadius;
    private float minuteHandRadius;

    private boolean isTouched = false;
    private boolean wasTouchedInHourZone = false;

    private int touchX;
    private int touchY;
    private RadialPoint touchRadial;

    public ClockView(Context context) {
        super(context);
    }

    public ClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            RadialPoint radial = toRadial(event.getX(), event.getY(), xMiddle, yMiddle);
            touchRadial = radial;
            if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                if (radial.r < clockRadius) {
                    this.isTouched = true;
                    this.wasTouchedInHourZone = radial.r < clockRadius/2;
                } else {
                    this.isTouched = false;
                }
            }
            RadialPoint historicalRadial = null;
            if (event.getAction() == MotionEvent.ACTION_MOVE && (event.getHistorySize() > 0)) {
                historicalRadial = toRadial(event.getHistoricalX(0), event.getHistoricalY(0), xMiddle, yMiddle);
            }
            setTimeFromRadialPoint(radial, historicalRadial);
        } else {
            this.isTouched = false;
        }
        this.invalidate();
        return true;
    }

    private void setTimeFromRadialPoint(RadialPoint radial, RadialPoint historicalRadial) {
        if (wasTouchedInHourZone) {
            hour = (int)(radial.phi * 12 / TWOPI);
        } else {
            int newMinute = (int) (radial.phi * 60 / TWOPI);
            if (historicalRadial != null) {
                int historicalRadialAngleQuarter = historicalRadial.getAngleQuarter();
                int radialAngleQuarter = radial.getAngleQuarter();
                int quarterDifference = historicalRadialAngleQuarter - radialAngleQuarter;
                switch (quarterDifference) {
                    case -3 : hour = (hour - 1) % 24;
                        break;
                    case 3 : hour = (hour + 1) % 24;
                        break;
                }

            }
            minute = newMinute;
        }
    }

    private void initClock() {
        paint = new Paint();
        minutePaint = new Paint();
        isInitialized = true;
        height = getHeight();
        width = getWidth();
        xMiddle = width / 2;
        yMiddle = height / 2;
        int maxRadius = Math.min(xMiddle, yMiddle);
        padding = (int)(maxRadius * 0.1);
        clockRadius = maxRadius - padding;
        middleRadius = clockRadius/20;
        handStartRadius = clockRadius/20 * 2;
        hourHandRadius = clockRadius * 0.7F;
        minuteHandRadius = clockRadius * 0.8F;

    }

    private Point fromRadial(double phi, double r, int xOrigin, int yOrigin){
        double xDouble = xOrigin + Math.sin(phi) * r;
        double yDouble = yOrigin - Math.cos(phi) * r;
        return new Point((int)xDouble, (int)yDouble);
    }

    private class RadialPoint {
        double phi;
        double r;

        public RadialPoint(double phi, double r) {
            this.phi = phi;
            this.r = r;
        }

        public int getAngleQuarter() {
            return  (int)(2 * phi/PI);
        }
    }

    private RadialPoint toRadial(double x, double y, int xOrigin, int yOrigin) {
        double realX = x - xOrigin;
        double realY = -(yOrigin -y);
        double r = sqrt(realX*realX+realY*realY);
        double phi = Math.atan2(-realX, realY) + Math.PI;
        return new RadialPoint(phi, r);
    }


    private void invalidateTextPaintAndMeasurements() {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInitialized) {
            initClock();
        }
        canvas.drawColor(Color.BLACK);

        drawFace(canvas);

        drawTouch(canvas);
        drawNumbers(canvas);
        postInvalidateDelayed(500);

    }

    private void drawNumbers(Canvas canvas) {
        paint.reset();
        paint.setARGB(100, 255, 0, 0);
        paint.setStyle(Paint.Style.FILL);

        paint.setTextSize(50);
        if (touchRadial != null)
            canvas.drawText(String.format("%1$d:%2$d %3$f:%4$d", this.hour, this.minute, touchRadial.phi / TWOPI * 360, touchRadial.getAngleQuarter()), 0, 50, paint);
    }


    private void drawTouch(Canvas canvas) {
        if (isTouched) {
            paint.reset();
            paint.setARGB(100, 255, 0, 0);
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);
            canvas.drawCircle(touchX, touchY, middleRadius, paint);
        }
    }

    private void drawFace(Canvas canvas) {
        initPaint(paint);
        initPaint(minutePaint);

        canvas.drawCircle(xMiddle, yMiddle, clockRadius, paint);

        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(xMiddle, yMiddle, (float) middleRadius, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        minutePaint.setStrokeWidth(2);
        for (int m=0; m<5*12; m+=1 ) {

            double angle = TWOPI / 60 * m;

            double markLengthCoeff = m % 5 == 0 ? 0.9 : 0.95;

            Paint markPaint = m % 5 == 0 ? this.minutePaint : this.paint;

            drawRadialLine(angle, clockRadius, clockRadius * markLengthCoeff, markPaint, canvas);
        }

        // hands
        double hourAngle = (TWOPI / 12 * (hour % 12)) + (TWOPI /(12*60) *(minute % 60));
        double minuteAngle = TWOPI /(60) *(minute % 60);
        paint.setStrokeWidth(10);
        drawRadialLine(hourAngle, handStartRadius, hourHandRadius, paint, canvas);
        paint.setStrokeWidth(5);
        drawRadialLine(minuteAngle, handStartRadius, minuteHandRadius, paint, canvas);
    }

    private void drawRadialLine(double phi, double rStart, double rEnd, Paint paint, Canvas canvas) {
        Point beg = fromRadial(phi, rStart, xMiddle, yMiddle);
        Point end = fromRadial(phi, rEnd, xMiddle, yMiddle);
        canvas.drawLine(beg.x, beg.y, end.x, end.y, paint);

    }

    private void initPaint(Paint paint) {
        paint.reset();
        paint.setColor(getResources().getColor(android.R.color.white));
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
    }


}

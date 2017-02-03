package apr.kraznys.learnclock;

import static java.lang.Math.PI;
import static java.lang.Math.sqrt;

public class RadialCoords {

    public static class RadialPoint {
        float phi;
        float r;

        public RadialPoint(float phi, float r) {
            this.phi = phi;
            this.r = r;
        }

        public int getAngleQuarter() {
            return  (int)(2 * phi/PI);
        }
    }

    public static class CartesianPoint {
        float x;
        float y;

        public CartesianPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    private float originX;
    private float originY;

    public RadialCoords(float originX, float originY) {
        this.originX = originX;
        this.originY = originY;
    }

    public CartesianPoint fromRadial(float phi, float r){
        float xfloat = (float) (originX + Math.sin(phi) * r);
        float yfloat = (float) (originY - Math.cos(phi) * r);
        return new CartesianPoint(xfloat, yfloat);
    }


    public RadialPoint toRadial(float x, float y) {
        float realX = x - originX;
        float realY = -(originY -y);
        float r = (float) sqrt(realX*realX+realY*realY);
        float phi = (float) (Math.atan2(-realX, realY) + Math.PI);
        return new RadialCoords.RadialPoint(phi, r);
    }
}

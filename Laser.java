import sas.*;
import java.awt.Color;

public class Laser {
    private Rectangle laser;
    private double x, y, yOrigin;
    private double width, height;

    public Laser(double pX, double pY, double scale) {
        width = 2 * scale;
        height = 20 * scale;
        laser = new Rectangle(pX, pY, width, height, Color.red);
        x = pX;
        y = pY;
        yOrigin = pY;
    }

    public void move(double pY) {
        y += pY;
        laser.moveTo(x, y);
    }

    public boolean isCollidingWith(Picture object) {
        return laser.intersects(object);
    }

    public void setHidden(boolean pHidden) {
        laser.setHidden(pHidden);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Rectangle getLaser() {
        return laser;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void setLaser(double pX) {
        y = yOrigin;
        x = pX;
        laser.moveTo(x, y);
    }

    public boolean laserIntersects(Picture object) {
        return laser.intersects(object);
    }
}
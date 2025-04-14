
import sas.*;

public class Astroid {
    protected Picture astroid;
    private double scale;
    private Picture ufo;
    private double speed = 1.5, xSpeed = 0;
    private int scoreValue = 20;
    private boolean isPowerUp = false;
    private boolean isActive = false;

    public Astroid(double pX, double pY, double pScale, Ufo pUfo) {
        astroid = new Picture(pX, pY, 30 * pScale, 30 * pScale, "Astroid.png");
        scale = pScale;
        ufo = pUfo.getUfo();
        astroid.setHidden(false);
    }

    public void setAstroid(double pX, double pY) {
        astroid.moveTo(pX, pY);
    }

    public double getWidth() {
        return scale * 30;
    }

    public Picture getAstroid() {
        return astroid;
    }

    public boolean isColliding() {
        if (astroid.intersects(ufo)) {
            return true;
        } else {
            return false;
        }
    }

    public double getSpeed() {
        return speed;
    }

    public double getXSpeed() {
        return xSpeed;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public boolean isPowerUp() {
        return isPowerUp;
    }

    public boolean isActive() {
        return isActive;
    }

}

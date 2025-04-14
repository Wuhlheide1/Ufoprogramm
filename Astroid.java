
import sas.*;

public class Astroid {
    protected Picture astroid;
    private double scale;
    private Picture ufo;
    private double speed = 2.5, xSpeed = 0;
    private int scoreValue = 20;
    private boolean isPowerUp = false;
    private boolean isActive = false;
    private Shield shield;

    public Astroid(double pX, double pY, double pScale, Ufo pUfo, Shield shield) {
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

    public boolean isColliding(Ufo ufo) {
        if (astroid.intersects(ufo.getUfo())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isColliding(Shield shield) {
        if (astroid.intersects(shield.getShape())) {
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

    private int powerUpTime = 10000; // Default power-up time in milliseconds

    public int getPowerUpTime() {
        return this.powerUpTime;
    }
}

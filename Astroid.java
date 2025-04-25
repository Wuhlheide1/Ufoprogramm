
import sas.*;
import java.awt.Color;

public class Astroid {
    protected Picture astroid;
    private double scale;
    private Picture ufo;
    private double speed = 2.5, xSpeed = 0;
    private int scoreValue = 20;
    private boolean isPowerUp = false;
    private boolean isActive = false;
    private Shield shield;
    private String averageColour = "gray";

    public Astroid(double pX, double pY, double pScale, Ufo pUfo, Shield shield) {
        astroid = new Picture(pX, pY, 30 * pScale, 30 * pScale, "textures/GrayAstroidAlt2.png");
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

    public void explosionAnimation(Runnable afterAnimation) {
        new Thread(() -> {
            Picture frame;
            double x = astroid.getShapeX();
            double y = astroid.getShapeY();
            astroid.setHidden(true);
            for (int i = 1; i <= 12; i++) {
                frame = new Picture(x, y, 50 * scale, 50 * scale, "textures/" + getAverageColour() + "explosion" + i + ".png");
                frame.setHidden(false);
                frame.moveTo(x, y);
                try {
                    Thread.sleep(3);
                    frame.setHidden(true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Call the callback after animation is done
            if (afterAnimation != null)
                afterAnimation.run();
        }).start();
    }

    public String getAverageColour() {
        return averageColour;
    }
}

import sas.*;
import java.awt.Color;

public class Ufo {
    private Picture ufo;
    private double scale;
    public boolean exploded = false;
    private Picture explosion;
    private boolean isGunPoweredUp = false;
    private long lastShotTime = 0;
    private final long SHOT_INTERVAL = 300; // milliseconds between shots
    private Laser currentLaser;

    Ufo(double pX, double pY, double pScale, Laser pLaser) {
        ufo = new Picture(pX, pY, 45 * 0.75 * pScale, 64 * 0.75 * pScale, "rakete.png");
        scale = pScale;
        explosion = new Picture(ufo.getShapeX(), ufo.getShapeY(), 85 * 0.75 * scale, 64 * 0.75 * scale,
                "explosion.png");
        explosion.setHidden(true);
        ufo.setHidden(false);
        currentLaser = pLaser;
    }

    public void setUfo(double pX, double pY) {
        ufo.moveTo(pX, pY);
    }

    public Picture getUfo() {
        return ufo;
    }

    public void setUfoPosition(double pX, double pY) {
        ufo.moveTo(pX, pY);
    }

    public void ufoMove(double pAmount) {
        ufo.move(pAmount, 0);
    }

    public double getWidth() {
        return scale * 45 * 0.75;
    }

    public void explode() {
        // Calculate center position of UFO
        double ufoCenterX = ufo.getShapeX() + (ufo.getShapeWidth() / 2);
        double ufoCenterY = ufo.getShapeY() + (ufo.getShapeHeight() / 2);

        // Position explosion so its center matches the UFO's center
        double explosionX = ufoCenterX - (explosion.getShapeWidth() / 2);
        double explosionY = ufoCenterY - (explosion.getShapeHeight() / 2);

        explosion.moveTo(explosionX, explosionY);
        explosion.setHidden(false);
        ufo.setHidden(true);
        exploded = true;
    }

    public void hideExplosion() {
        explosion.setHidden(true);
    }

    public void gunPowerUp() {
        isGunPoweredUp = true;
        new Thread(() -> {
            try {
                Thread.sleep(5000); // Power-up lasts 5 seconds
                isGunPoweredUp = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void shootLaser() {
        if (isGunPoweredUp && System.currentTimeMillis() - lastShotTime > SHOT_INTERVAL) {
            lastShotTime = System.currentTimeMillis();
            // Calculate center position of UFO
            double ufoCenterX = ufo.getShapeX() + (ufo.getShapeWidth() / 2);
            // Position laser at UFO's center
            double laserX = ufoCenterX - (currentLaser.getWidth() / 2);
            currentLaser.setLaser(laserX); // Reset laser position
            currentLaser.getLaser().setHidden(false);

            new Thread(() -> {
                try {
                    while (currentLaser.getY() > 0) {
                        // This line needs to match your Laser.move() method signature
                        currentLaser.move(-10); // If your Laser.move() takes one parameter
                        Thread.sleep(50);
                    }
                    currentLaser.hide();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public Laser getCurrentLaser() {
        return currentLaser;
    }

    public boolean isGunPoweredUp() {
        return isGunPoweredUp;
    }
}

import sas.*;
import java.awt.Color;

public class Ufo {
    private Picture ufo;
    private double scale;
    public boolean exploded = false;
    private Picture explosion;
    private boolean isGunPoweredUp = false;
    private long lastShotTime = 0;
    private final long SHOT_INTERVAL = 200; // milliseconds between shots
    private Laser currentLaser;
    Ufoprogramm ufoprogramm;

    Ufo(double pX, double pY, double pScale, Laser pLaser, Ufoprogramm pUfoprogramm) {
        ufo = new Picture(pX, pY, 45 * 0.75 * pScale, 64 * 0.75 * pScale, "rakete.png");
        scale = pScale;
        explosion = new Picture(ufo.getShapeX(), ufo.getShapeY(), 85 * 0.75 * scale, 64 * 0.75 * scale,
                "explosion.png");
        explosion.setHidden(true);
        ufo.setHidden(false);
        currentLaser = pLaser;
        ufoprogramm = pUfoprogramm;
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
        lastShotTime = 0;
        new Thread(() -> {
            try {
                Thread.sleep(10000); // Power-up lasts 5 seconds
                isGunPoweredUp = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void shootLaser() {
        if (isGunPoweredUp == true && System.currentTimeMillis() - lastShotTime > SHOT_INTERVAL) {
            lastShotTime = System.currentTimeMillis();
            // Calculate center position of UFO
            double ufoCenterX = ufo.getShapeX() + (ufo.getShapeWidth() / 2);
            // Position laser at UFO's center
            double laserX = ufoCenterX - (currentLaser.getWidth() / 2);

            // Properly hide the current laser before creating a new one
            if (currentLaser != null) {
                currentLaser.setHidden(true);
            }

            currentLaser = new Laser(laserX, ufo.getShapeY(), scale);
            ufoprogramm.overwiriteLaser(currentLaser);
            currentLaser.getLaser().setHidden(false);

            new Thread(() -> {
                Laser thisLaser = currentLaser; // Store reference to this specific laser
                try {
                    while (thisLaser.getY() > 0) {
                        thisLaser.move(-10);
                        Thread.sleep(50);
                    }
                    // Always ensure the laser is hidden when done
                    thisLaser.setHidden(true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // Hide laser even if interrupted
                    thisLaser.setHidden(true);
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

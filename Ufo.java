import sas.*;
import java.awt.Color;
import java.util.ArrayList;

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
    // Add these fields to track multiple lasers
    private ArrayList<Laser> activeLasers = new ArrayList<>();
    private final int MAX_LASERS = 10; // Maximum number of lasers allowed at once

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
        // Clear any existing lasers when power-up starts
        for (Laser laser : activeLasers) {
            laser.setHidden(true);
        }
        activeLasers.clear();

        new Thread(() -> {
            try {
                Thread.sleep(10000); // Power-up lasts 10 seconds
                isGunPoweredUp = false;
                // Hide all lasers when power-up ends
                for (Laser laser : activeLasers) {
                    laser.setHidden(true);
                }
                activeLasers.clear();
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

            // Create a new laser
            Laser newLaser = new Laser(ufoCenterX - (2 * scale) / 2, ufo.getShapeY(), scale);
            newLaser.getLaser().setHidden(false);

            // Add to active lasers list (remove oldest if at capacity)
            if (activeLasers.size() >= MAX_LASERS) {
                Laser oldLaser = activeLasers.remove(0);
                oldLaser.setHidden(true);
            }
            activeLasers.add(newLaser);

            // Update the current laser reference for collision detection
            currentLaser = newLaser;
            ufoprogramm.overwiriteLaser(currentLaser);

            // Start a thread to move this specific laser
            new Thread(() -> {
                Laser thisLaser = newLaser;
                try {
                    while (thisLaser.getY() > -50) { // Allow to go slightly off-screen
                        thisLaser.move(-10);
                        Thread.sleep(50);
                    }
                    // Remove and hide when done
                    thisLaser.setHidden(true);
                    activeLasers.remove(thisLaser);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    thisLaser.setHidden(true);
                }
            }).start();
        }
    }

    // Add this method to get all active lasers for collision detection
    public ArrayList<Laser> getActiveLasers() {
        return activeLasers;
    }

    public Laser getCurrentLaser() {
        return currentLaser;
    }

    public boolean isGunPoweredUp() {
        return isGunPoweredUp;
    }

    public boolean laserIntersects(Picture object, Laser laser) {
        return laser.laserIntersects(object);
    }
}

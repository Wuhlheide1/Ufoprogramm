import sas.*;
import java.util.ArrayList;

public class Ufo {
    private Picture ufo;
    private double scale;
    public boolean exploded = false;
    private Picture explosion;
    private boolean isGunPoweredUp = false, isShieldActive = false;
    private long lastShotTime = 0;
    private final long SHOT_INTERVAL = 200; // milliseconds between shots
    private Laser currentLaser;
    Ufoprogramm ufoprogramm;
    // Add these fields to track multiple lasers
    private ArrayList<Laser> activeLasers = new ArrayList<>();
    private final int singleLaserAmount = 10;
    private int MAX_LASERS = singleLaserAmount; // Maximum number of lasers allowed at once
    private boolean isMultiShoot = false;

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

    public void multishoot() {
        // Only proceed if gun is powered up and cooldown has passed
        if (isGunPoweredUp && System.currentTimeMillis() - lastShotTime > SHOT_INTERVAL) {
            lastShotTime = System.currentTimeMillis();

            // Create and fire each laser independently
            createAndFireLaser(0); // Center laser
            createAndFireLaser(-30); // Left laser
            createAndFireLaser(30); // Right laser
        }
    }

    public void shootLaser() {
        shootLaser(0); // Default offset is centered on the UFO
    }

    public void shootLaser(double pOffsetX) {
        if (isGunPoweredUp && System.currentTimeMillis() - lastShotTime > SHOT_INTERVAL) {
            lastShotTime = System.currentTimeMillis();
            createAndFireLaser(pOffsetX);
        }
    }

    // Helper method to create and fire a single laser with an offset
    private void createAndFireLaser(double offsetX) {
        // Calculate center position of UFO
        double ufoCenterX = ufo.getShapeX() + (ufo.getShapeWidth() / 2);

        // Create a new laser
        Laser newLaser = new Laser((ufoCenterX - (2 * scale) / 2) + offsetX, ufo.getShapeY(), scale);
        newLaser.getLaser().setHidden(false);

        // Use base limit for single shoot, triple for multi-shoot
        int laserLimit = isMultiShoot ? singleLaserAmount * 3 : singleLaserAmount;

        // Add to active lasers list (remove oldest if at capacity)
        if (activeLasers.size() >= laserLimit) {
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

    public void gunPowerUp(Astroid asteroid) {
        isGunPoweredUp = true;
        lastShotTime = 0;
        // Clear any existing lasers when power-up starts
        for (Laser laser : activeLasers) {
            laser.setHidden(true);
        }
        activeLasers.clear();

        // Use default time if asteroid is null or time is invalid
        int time = (asteroid != null) ? asteroid.getPowerUpTime() : 10000;

        new Thread(() -> {
            try {
                Thread.sleep(time);
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

    public void setHidden(boolean hidden) {
        ufo.setHidden(hidden);
    }

    public void enableMultiShoot() {
        isMultiShoot = true;
    }

    public void disableMultiShoot() {
        isMultiShoot = false;
    }

    public boolean isMultiShoot() {
        return isMultiShoot;
    }

    public void enableShield(Astroid asteroid, Shield shield) {
        isShieldActive = true;
        shield.hideShield(false); // Show the shield first

        // Ensure minimum duration of 10 seconds (10000 milliseconds)
        int time = (asteroid != null && asteroid.getPowerUpTime() > 0) ? 
                  asteroid.getPowerUpTime() : 10000;

        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            try {
                // Continuously update shield position while active
                while (System.currentTimeMillis() - startTime < time) {
                    shield.centerOnUfo(this);
                    Thread.sleep(10); // Update position every 10ms
                }
                isShieldActive = false;
                shield.hideShield(true); // Hide the shield when the power-up ends
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}

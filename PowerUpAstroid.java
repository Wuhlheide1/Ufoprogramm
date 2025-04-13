import sas.*;

// FastlAstroid is a child class of Astroid
public class PowerUpAstroid extends Astroid {
    double speed = 1;
    int scoreValue = 0;
    boolean isPowerUp = true; // set to true for powerup astroid, false for normal astroid
    boolean isActive = false; // set to true when powerup is active, false when powerup is not active
    int powerUpTime = 1000; // time in milliseconds that powerup is active

    // Constructor that calls the parent constructor using super()
    public PowerUpAstroid(double pX, double pY, double pScale, Ufo pUfo) {
        super(pX, pY, pScale, pUfo); // Call parent constructor

        // Create a new Picture with the same position and size but different image
        this.astroid = new Picture(pX, pY, 30 * pScale, 30 * pScale, "RedAstroid.png");
    }

    // Override the getSpeed method from the parent class
    @Override
    public double getSpeed() {
        return this.speed; // Return the faster speed for this type of asteroid
    }

    @Override
    public int getScoreValue() {
        return scoreValue;
    }

    @Override
    public boolean isPowerUp() {
        return isPowerUp;
    }
}
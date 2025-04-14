import sas.*;

// FastlAstroid is a child class of Astroid
public class FastAstroid extends Astroid {
    double speed = 3;
    int scoreValue = 30;

    // Constructor that calls the parent constructor using super()
    public FastAstroid(double pX, double pY, double pScale, Ufo pUfo, Shield shield) {
        super(pX, pY, pScale, pUfo, shield); // Call parent constructor

        // Create a new Picture with the same position and size but different image
        this.astroid = new Picture(pX, pY, 30 * pScale, 30 * pScale, "BlueAstroid.png");
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
}
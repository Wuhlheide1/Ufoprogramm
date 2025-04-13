import sas.*;

// FastlAstroid is a child class of Astroid
public class ZigZagAstroid extends Astroid {
    double speed = 0.75;
    double xSpeed = 0.75;
    double scale;
    int scoreValue = 60;

    // Constructor that calls the parent constructor using super()
    public ZigZagAstroid(double pX, double pY, double pScale, Ufo pUfo) {
        super(pX, pY, pScale, pUfo); // Call parent constructor
        scale = pScale;
        // Create a new Picture with the same position and size but different image
        this.astroid = new Picture(pX, pY, 30 * pScale, 30 * pScale, "GreenAstroid.png");
    }

    // Override the getSpeed method from the parent class
    @Override
    public double getSpeed() {
        return this.speed; // Return the faster speed for this type of asteroid
    }

    // Override the isFast method from the parent class

    @Override
    public double getXSpeed() {
        // Check if asteroid is hitting the right edge
        if (this.getAstroid().getShapeX() + this.getAstroid().getShapeWidth() >= 300) {
            xSpeed = -Math.abs(xSpeed); // Force negative speed (move left)
        }
        // Check if asteroid is hitting the left edge
        else if (this.getAstroid().getShapeX() <= 0) {
            xSpeed = Math.abs(xSpeed); // Force positive speed (move right)
        }

        return xSpeed;
    }

    @Override
    public int getScoreValue() {
        return scoreValue;
    }
}